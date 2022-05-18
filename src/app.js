import { WebSocketServer } from 'ws'
import 'dotenv/config'
import { initDatabase, closeDatabase, insertMessageIntoDB } from './databaseOperations.js'

const WS = new WebSocketServer({
    host: process.env.WS_HOST,
    port: process.env.WS_PORT,
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


function messageParser(messageData) {
    const receivedJSON = JSON.parse(messageData.toString())
    receivedJSON.Username = escapeUnsafeMessageData(receivedJSON.Username)
    receivedJSON.Content = escapeUnsafeMessageData(receivedJSON.Content)
    console.log(receivedJSON)
    return JSON.stringify(receivedJSON)
}


function connectionHandler(websocket, request) {
    console.log('New client connected')

    websocket.on('message', data => {
        const parsedMessage = messageParser(data)
        broadcastToAllClientsExceptSender(parsedMessage, websocket)
        insertMessageIntoDB(parsedMessage)
    })

    websocket.on('close', () => {
        console.log('Client disconnected')
        closeDatabase()
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
    initDatabase()
})

WS.on('connection', connectionHandler)

WS.on('close', disconnectHandler)

WS.on('error', errorHandler)
