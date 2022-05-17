import { WebSocketServer } from 'ws';


const WS = new WebSocketServer({
    host: 'localhost',
    port: 9999,
})


function broadcastWS(message) {
    WS.clients.forEach(client => {
        client.send(message)
    })
}


function escapeUnsafeMessageData(messageData) {
    if (!messageData) return ""

    return messageData
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;')
}


function messageHandler(messageData) {
    const receivedJSON = JSON.parse(messageData.toString())
    receivedJSON.Username = escapeUnsafeMessageData(receivedJSON.Username)
    receivedJSON.Content = escapeUnsafeMessageData(receivedJSON.Content)
    console.log(receivedJSON)
    return JSON.stringify(receivedJSON)
}


function connectionHandler(websocket, request) {
    request.setEncoding('utf-8')

    console.log('New client connected')

    websocket.on('message', data => {
        broadcastWS(messageHandler(data))
    })

    websocket.on('close', () => {
        console.log('Client disconnected')
    })
}


function errorHandler(error) {
    console.log(error)
}


function disconnectHandler() {
    console.log('Server closed.')
}


WS.on('listening', () => {
    console.log('Server listening.')
})

WS.on('connection', connectionHandler)

WS.on('close', disconnectHandler)

WS.on('error', errorHandler)
