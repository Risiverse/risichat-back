import { broadcastToAllClientsExceptSender } from './appServer.js'
import { insertMessageIntoDB } from './appDatabase.js'
import { RawData, WebSocket } from 'ws'


function escapeUnsafeMessageData(messageData: string) {
    if (!messageData) return ""
    return messageData
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;')
}


function messageParser(messageData: RawData) {
    const receivedJSON = JSON.parse(messageData.toString())
    receivedJSON.Username = escapeUnsafeMessageData(receivedJSON.Username)
    receivedJSON.Content = escapeUnsafeMessageData(receivedJSON.Content)
    console.log(receivedJSON)
    return JSON.stringify(receivedJSON)
}


export function messageHandler(messageData: RawData, websocket: WebSocket) {
    const parsedMessage = messageParser(messageData)
    broadcastToAllClientsExceptSender(parsedMessage, websocket)
    insertMessageIntoDB(parsedMessage)
}
