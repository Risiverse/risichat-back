# risichat-back
- [Risichat backend](#risichat-backend)
  - [Use with Docker](#use-with-docker)
  - [Build and run it yourself](#build-and-run-it-yourself)
- [Environment variables](#environment-variables)
- [DTOs](#dtos)
  - [Client to server](#client-to-server)
    - [New message (to server)](#new-message-to-server)
  - [Server to client](#server-to-client)
    - [New message (to clients)](#new-message-to-clients)
    - [User status update](#user-status-update)
    - [Other](#other)

## Risichat backend
Made with Java, Maven, bson, dotenv-java, Java-WebSocket, json, mongodb-driver-sync, slf4j-api, slf4j-log4j12.

### Use with Docker
To **build** the app, run the command
```shell
docker build -t risichat-back:1.0.0 .
```
To **run** the app, run the command
```shell
docker run -p hostPort:localPort -t risichat-back:1.0.0 .
```
``localPort`` being the WS server port you chose in the .env file, and ``hostPort`` your machine port you want to redirect to. <br>
You may also want to bind Mongodb's port if it runs on the same machine than the container.

### Build and run it yourself
You'll need Maven and openjdk 18. <br> <br> 
To **build** the app into a JAR with all dependencies, run the command
```shell
mvn clean compile assembly:single
```
If everything is ok, it should generate an executable .jar file into ``./target`` folder. You can place this file wherever you want.<br>
To **run** the app, you'll have to first create (or move if already created) the .env file in the target folder, or the same folder you placed the .jar file.<br>
Then, simply run the command
```shell
java -jar ./target/risichat-back-jar-with-dependencies.jar
```
(or replace ``./target/risichat-back-jar-with-dependencies.jar`` with whatever name you gave it and/or where it is located)
## Environment variables
- ./env
  - WS_PORT
  - MONGO_HOST
  - MONGO_DB
  - MONGO_COLLECTION

MongoDB hostname should look something like this ``mongodb://host.docker.internal:27017`` if it runs on the same machine as the container.

## DTOs
All JSON requests should be stringified.
### Client to server
#### New message (to server)
```JSON
{
    "type": "newMessage",
    "data": {
        "userSSOID": 424242,
        "username": "Didier Deschamps",
        "content": "Hello World!"
    }
}
```
### Server to client
#### New message (to clients)
```JSON
{
    "type": "newMessage",
    "data": {
        "timestamp": 1659301486937,
        "username": "Didier Deschamps",
        "content": "Hello World!"
    }
}
```
#### User status update
```JSON
{
    "type": "newConnection",
    "data": {
        "message": "A new user has logged in."
    }
}
```
```JSON
{
    "type": "newDisconnection",
    "data": {
        "message": "A user has logged out."
    }
}
```
#### Other
```JSON
{
  "type": "error",
  "message": "JSON Format not valid.",
  "status": 400,
  "data": {
    "userSSOID": "pouet",
    "username": 4242,
    "pouet": "Hello World!"
  }
}
```