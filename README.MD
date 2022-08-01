# risichat-back
## Risichat backend

Made with NodeJs

## Environment variables
- ./src/.env :
    - WS_HOST
    - WS_PORT
    - MONGO_HOST
    - MONGO_DB
    - MONGO_COLLECTION

## DTOs
All JSON requests should be stringified.
### Client to server
```javascript
{
    type: 'newMessage',
    data: {
        userSSOID: 424242,
        username: 'Didier Deschamps',
        content: 'Hello World!'
    }
}
```
### Server to client
#### New message
```javascript
{
    type: 'newMessage',
    data: {
        timestamp: 1659301486937,
        username: 'Didier Deschamps',
        content: 'Hello World!'
    }
}
```
#### User status update
```javascript
{
    type: 'userStatusUpdate',
    data: {
        status: 'connected',
        username: 'Didier Deschamps'
    }
}
```
```javascript
{
    type: 'userStatusUpdate',
    data: {
        status: 'disconnected',
        username: 'Didier Deschamps'
    }
}
```