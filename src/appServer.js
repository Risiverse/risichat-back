import { initDatabase, closeDatabase, insertMessageIntoDB } from './appDatabase.js'
import { messageHandler } from './appServerMessages.js'
import { WS } from './app.js'


export function broadcastToAllClients(message) {
    WS.clients.forEach(client => {
        client.send(message)
    })
}


export function broadcastToAllClientsExceptSender(message, clientSender) {
    WS.clients.forEach(client => {
        if (!(client === clientSender)) {
            client.send(message)
        }
    })
}


export function serverConnectionHandler(websocket, request) {
    console.log('New client connected')

    websocket.on('message', messageData => {
        messageHandler(messageData, websocket)
    })

    websocket.on('close', () => {
        console.log('Client disconnected')
    })
}


export function serverErrorHandler(error) {
    console.log(error)
}


export function serverDisconnectHandler() {
    closeDatabase()
    console.log('Server closed.')
}


export function serverListeningHandler() {
    initDatabase()
    console.log('Server listening...')
}
