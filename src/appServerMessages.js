import { broadcastToAllClientsExceptSender } from './appServer.js'
import { insertMessageIntoDB } from './appDatabase.js'


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


export function messageHandler(messageData, websocket) {
    const parsedMessage = messageParser(messageData)
    broadcastToAllClientsExceptSender(parsedMessage, websocket)
    insertMessageIntoDB(parsedMessage)
}
