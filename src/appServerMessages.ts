import { broadcastToAllClientsExceptSender } from './appServer'
import { insertMessageIntoDB } from './appDatabase'
import { RawData, WebSocket } from 'ws'


interface Message {
    timestamp: number,
    username: string,
    content: string
}


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
    const receivedJSON: Message = JSON.parse(messageData.toString())
    const parsedJSON: Message = {
        timestamp: receivedJSON.timestamp,
        username: escapeUnsafeMessageData(receivedJSON.username),
        content: escapeUnsafeMessageData(receivedJSON.content)
    }
    console.log(parsedJSON)
    return JSON.stringify(parsedJSON)
}


function isMessageValid(message: Message) {
    return message.content && message.username && message.timestamp
}


export function messageHandler(messageData: RawData, websocket: WebSocket) {
    const parsedMessage = messageParser(messageData)
    if (isMessageValid(JSON.parse(parsedMessage))) {
        broadcastToAllClientsExceptSender(parsedMessage, websocket)
        insertMessageIntoDB(parsedMessage)
    }
}
