import { WebSocketServer } from 'ws'
import 'dotenv/config'
import { initDatabase, closeDatabase, insertMessageIntoDB } from './databaseOperations.js'


const WS = new WebSocketServer({
    host: process.env.WS_HOST,
    port: parseInt(process.env.WS_PORT),
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


function messageHandler(messageData, websocket) {
    const parsedMessage = messageParser(messageData)
    broadcastToAllClientsExceptSender(parsedMessage, websocket)
    insertMessageIntoDB(parsedMessage)
}


function serverConnectionHandler(websocket, request) {
    console.log('New client connected')

    websocket.on('message', messageData => {
        messageHandler(messageData, websocket)
    })

    websocket.on('close', () => {
        console.log('Client disconnected')
    })
}


function serverErrorHandler(error) {
    console.log(error)
}


function serverDisconnectHandler() {
    closeDatabase()
    console.log('Server closed.')
}


function serverListeningHandler() {
    initDatabase()
    console.log('Server listening...')
}


WS.on('listening', serverListeningHandler)

WS.on('connection', serverConnectionHandler)

WS.on('close', serverDisconnectHandler)

WS.on('error', serverErrorHandler)
