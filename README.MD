# risichat-back
## Risichat backend

Made with NodeJs

## Environment variables
- ./env
  - WS_HOST
  - WS_PORT
  - MONGO_HOST
  - MONGO_DB
  - MONGO_COLLECTION

## DTOs
All JSON requests should be stringified.
### Client to server
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
#### New message
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