#!/usr/bin/env/bash

echo ''
echo "Get an OAuth Access Token:"
echo "curl -s http://acme:acmesecret@localhost:9999/uaa/oauth/token -d grant_type=password -d client_id=acme -d scope=webshop -d username=admin -d password=admin"
OAUTH_RESPONSE=`curl -s http://acme:acmesecret@localhost:9999/uaa/oauth/token -d grant_type=password -d client_id=acme -d scope=webshop -d username=admin -d password=admin`
echo $OAUTH_RESPONSE | jq .
TOKEN=`echo $OAUTH_RESPONSE | jq -r .access_token`
echo "Access Token: $TOKEN"

echo ''
echo "Call API with Accss Token..."
curl -s http://localhost:8765/api/product/123 -H "Authorization: Bearer $TOKEN"
