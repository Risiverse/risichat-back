import { initDatabase, closeDatabase } from './appDatabase'
import { messageHandler } from './appServerMessages'
import { WS } from './app'
import type WebSocket from 'ws'


export function broadcastToAllClients(message: string): void {
    WS.clients.forEach(client => {
        client.send(message)
    })
}


export function broadcastToAllClientsExceptSender(message: string, clientSender: WebSocket): void {
    WS.clients.forEach(client => {
        if (!(client === clientSender)) {
            client.send(message)
        }
    })
}


export function serverConnectionHandler(websocket: WebSocket): void {
    websocket.on('message', messageData => {
        messageHandler(messageData, websocket)
    })

    broadcastToAllClientsExceptSender(JSON.stringify({
        type: 'newConnection',
        data: {
            message: 'A new user has logged in.'
        }
    }), websocket)

    websocket.on('close', () => {
        broadcastToAllClientsExceptSender(JSON.stringify({
        type: 'newDisconnection',
        data: {
            message: 'A user has logged out.'
        }
    }), websocket)
    })
}


export function serverErrorHandler(error: Error): void {
    console.log(error)
}


export function serverDisconnectHandler(): void {
    closeDatabase()
    console.log('Server closed.')
}


export function serverListeningHandler(): void {
    initDatabase()
    console.log('[OK] Server started.')
}
