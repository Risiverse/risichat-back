import { broadcastToAllClientsExceptSender } from './appServer'
import { insertMessageIntoDB } from './appDatabase'
import { RawData, WebSocket } from 'ws'


export function escapeUnsafeMessageData(messageData: string) {
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
