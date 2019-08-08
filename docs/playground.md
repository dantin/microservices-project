### Eureka, Zuul, Ribbon

Boot sequence: Discovery(Eureka), Core Services(product/recommendation/review), Composite Service(product-composite), Edge Server(Zuul)

    $ ./gradlew bootRun

Browser: `http://localhost:8761`

API:

    $ curl -s -H "Accept: application/json" http://localhost:8761/eureka/apps | jq '.applications.application[] | {service: .name, ip: .instance[0].ipAddr, port: .instance[0].port."$"}'

Edge Server's Port is always `8765`

    $ curl -s 'localhost:8765/productcomposite/product/1' | jq .

Inside microservices:

    $ curl -s 'localhost:58554/product/1' | jq .
    $ curl -s 'localhost:58566/review?productId=1' | jq .
    $ curl -s 'localhost:58562/recommendation?productId=1' | jq .
    $ curl -s 'localhost:58599/product/1' | jq .

Add another core service, e.g. `review`, see how load balance works.
