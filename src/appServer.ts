import { initDatabase, closeDatabase } from './appDatabase'
import { messageHandler } from './appServerMessages'
import { WS } from './app'
import WebSocket from 'ws'


export function broadcastToAllClients(message: string) {
    WS.clients.forEach(client => {
        client.send(message)
    })
}


export function broadcastToAllClientsExceptSender(message: string, clientSender: WebSocket) {
    WS.clients.forEach(client => {
        if (!(client === clientSender)) {
            client.send(message)
        }
    })
}


export function serverConnectionHandler(websocket: WebSocket) {
    websocket.on('message', messageData => {
        messageHandler(messageData, websocket)
    })

    websocket.on('close', () => {
        console.log('Client disconnected')
    })
}


export function serverErrorHandler(error: Error) {
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
