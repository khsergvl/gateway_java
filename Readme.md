# This is simple spring / web application 
This code base  designed to be built by gradle wrapper plugin;

To simplify testing exist docker-compose integration which  
will automatically boostrap all required services.  

### Requirements 
Java 11, docker & docker-compose

### Build and run
To test the application run in bash:  
1) Project build:  $ ./gradlew clean build
2) docker-compose: $  docker-compose up

This web application expose swagger-documentation,  
which is alive here : http://localhost:8080/swagger-ui/  

During bootstrap in memory db is populated by test data: accounts with id 1 and 2.

To check account state call GET /account/{id}, where id is 1, 2 
To test fund transfer call POST /transfer with JSON body like {"from":1,"to":2, "amount":0.05}  
Where from and to are account identifiers.   