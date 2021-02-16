## Insert user
    curl -d '{"userName":"davgf", "email":"davgf@gmail.com", "pass":"12345678.aA", "name": "david", "surname": "garc" }' -H "Content-Type: application/json" -X PUT http://192.168.49.2/user/signup | jq .

