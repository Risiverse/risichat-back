import { WebSocketServer } from 'ws';
import 'dotenv/config'

const WS = new WebSocketServer({
    host: process.env.HOST,
    port: process.env.PORT,
})


function broadcastToAllClients(message) {
    WS.clients.forEach(client => {
        client.send(message)
    })
}


function broadcastToAllClientsExceptSender(message, clientSender) {
    WS.clients.forEach(client => {
        if (!(client === clientSender)) {
            client.send(message)
        }
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
    console.log('New client connected')

    websocket.on('message', data => {
        broadcastToAllClientsExceptSender(messageHandler(data))
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
