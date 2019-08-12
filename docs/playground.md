## Playbook

How to build a microservices project step by step.

### Service Discovery, Dynamic Routing (Load Balancer), Edge Server

Using: Eureka, Zuul, Ribbon

Boot sequence: Discovery(Eureka), Core Services(product/recommendation/review), Composite Service(product-composite), Edge Server(Zuul)

    $ ./gradlew bootRun

Browser: `http://localhost:8761`

Alive Service by API:

    $ curl -s -H "Accept: application/json" http://localhost:8761/eureka/apps | jq '.applications.application[] | {service: .name, ip: .instance[0].ipAddr, port: .instance[0].port."$"}'

Edge Server's Port is always `8765`

    $ curl -s 'localhost:8765/productcomposite/product/1' | jq .

Inside microservices:

    $ curl -s 'localhost:58554/product/1' | jq .
    $ curl -s 'localhost:58566/review?productId=1' | jq .
    $ curl -s 'localhost:58562/recommendation?productId=1' | jq .
    $ curl -s 'localhost:58599/product/1' | jq .

Add another core service, e.g. `review`, see how load balance works.

### Circuit Breaker, Monitoring

Using: Hystrix, Hystrix Dashboard, Turbine

Boot sequence: Discovery(Eureka), Core Services(product/recommendation/review), Composite Service(product-composite), Edge Server(Zuul), Monitoring(monitor-board)

    $ ./gradlew bootRun

Browser: `http://localhost:7979/hystrix`

Check edge server API:

    $ curl -s 'localhost:8765/productcomposite/product/1' | jq .

Cluster via turbine `http://127.0.0.1:7979/turbine.stream`.

Single Hystrix Application via turbine `http://127.0.0.1:52970/actuator/hystrix.stream`

Shutdown `review-service`, see log:

    ProductCompositeIntegration : get reviews...
    ProductCompositeIntegration : using fallback method for review-service

Circuit breaker status is __Closed__.

Using benchmark:

    $ ab -n 30 -c 5 localhost:8765/productcomposite/product/1

Circuit breaker status changes to __Open__.

Statup `review-service` and keep benching, see how the circuit breaker change from __Open__ to __Closed__.


### Spring Security, OAuth2.0

Boot sequence: Discovery(Eureka), Athentication Server(auth), Core Services(product/recommendation/review), Composite Service(product-composite), API Service(product-api), Edge Server(Zuul), Monitoring(monitor-board)

    $ ./gradlew bootRun

#### Authentication Code

Authentication code grant by Resource Owner:

Browser: `http://localhost:9999/oauth/authorize?response_type=code&client_id=acme&redirect_uri=http://example.com&scope=webshop&state=97536`

Get Access Token:

    $ CODE=zzjxzD
    $ curl acme:acmesecret@localhost:9999/uaa/oauth/token \
     -d grant_type=authorization_code \
     -d client_id=acme \
     -d redirect_uri=http://example.com \
     -d code=$CODE -s

Use Token to Access API:

    $ TOKEN=039a1ac1-d747-4d2a-ac7e-06de3af4a293
    $ curl 'http://localhost:8765/api/product/12' -H "Authorization: Bearer $TOKEN" -s

Let’s make a second attempt to get an access token for the same code. It should fail, e.g. the code is actually working as a _one time_ password:

    $ curl acme:acmesecret@localhost:9999/uaa/oauth/token \
     -d grant_type=authorization_code \
     -d client_id=acme \
     -d redirect_uri=http://example.com \
     -d code=$CODE -s

#### Implicit

Implicit grant:

Browser: `http://localhost:9999/oauth/authorize?response_type=token&client_id=acme&redirect_uri=http://example.com&scope=webshop&state=48532`

Access API

Without an access token, will fail:

    $ curl 'http://localhost:8765/api/product/123' -s | jq .

Try with a invalid access token, it should fail as well:

    $ curl 'http://localhost:8765/api/product/123' \
       -H  "Authorization: Bearer invalid-access-token" -s | jq .

Let's invalidate the access token, e.g. simulating that it has expired, call will fail.

    $ curl 'http://localhost:8765/api/product/12' -H "Authorization: Bearer $TOKEN" -s | jq .
