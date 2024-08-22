# This is a Demo Project for Demonstrating How to Secure a Spring Boot App Using Cerbos

### This is a demo project for my article: https://semaphoreci.com/securing-a-spring-boot-application-with-cerbos

## How to run this project:

1. Start Cerbos locally:

`docker-compose up`

2. Start the Spring Boot app

## How to test the application

* GET request

Send a cURL request with the following data:

`curl -X GET http://localhost:8080/api/profile/111/321`

* DELETE request

`curl -X DELETE http://localhost:8080/api/profile/222/456`
