import { broadcastToAllClientsExceptSender } from './appServer'
import { insertMessageIntoDB } from './appDatabase'
import type { RawData, WebSocket } from 'ws'


interface Message {
    timestamp: number,
    username: string,
    content: string
}


export function escapeUnsafeMessageData(messageData: string): string {
    if (!messageData) return ""
    return messageData
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;')
}


function messageParser(messageData: RawData): string {
    const receivedJSON: Message = JSON.parse(messageData.toString())
    const parsedJSON: Message = {
        timestamp: receivedJSON.timestamp,
        username: escapeUnsafeMessageData(receivedJSON.username),
        content: escapeUnsafeMessageData(receivedJSON.content)
    }
    return JSON.stringify(parsedJSON)
}


function isStringValid(field: string|null): boolean {
    return field != undefined &&
        (typeof field) === 'string' &&
        field !== ''
}


function isNumberValid(field: number|null): boolean {
    return field != undefined &&
        (typeof field) === 'number'
}


function isMessageValid(message: Message): boolean {
    return isStringValid(message.content) &&
        isStringValid(message.username) &&
        isNumberValid(message.timestamp)
}


export function messageHandler(messageData: RawData, websocket: WebSocket): void {
    const messageDataJSON = JSON.parse(messageData.toString())

    console.log(messageDataJSON)

    if (isMessageValid(messageDataJSON)) {
        const validParsedMessage = messageParser(messageData)
        broadcastToAllClientsExceptSender(validParsedMessage, websocket)
        insertMessageIntoDB(validParsedMessage)
        console.log('Message valid')
    } else {
        console.log('Message invalid')
    }
}
