import { broadcastToAllClientsExceptSender } from './appServer'
import { insertMessageIntoDB } from './appDatabase'
import type { RawData, WebSocket } from 'ws'

interface chatMessage {
    timestamp: number,
    username: string,
    content: string
}

interface Message {
    userSSOID: number,
    type: string,
    data: chatMessage
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


function chatMessageParser(messageData: chatMessage): string {
    const parsedJSON: chatMessage = {
        timestamp: Date.now(),
        username: escapeUnsafeMessageData(messageData.username),
        content: escapeUnsafeMessageData(messageData.content)
    }
    return JSON.stringify(parsedJSON)
}


function isStringValid(field: string|null): boolean {
    return field != undefined &&
        (typeof field) === 'string' &&
        field !== ''
}


function isChatMessageValid(message: chatMessage): boolean {
    return isStringValid(message.content) &&
        isStringValid(message.username)
}


function chatMessageHandler(messageData: any, senderWS: WebSocket): void {
    if (isChatMessageValid(messageData)) {
        const validParsedMessage = chatMessageParser(messageData)
        broadcastToAllClientsExceptSender(validParsedMessage, senderWS)
        insertMessageIntoDB(validParsedMessage)
        console.log('Valid message')
    } else {
        senderWS.send(JSON.stringify({
            status: 400,
            message: 'The message could not be validated.',
            data: messageData
        }))
        console.log('Invalid message')
    }
}


const messagesTypesHandlers = [
    { type: 'chatMessage', handler: chatMessageHandler }
]


export function messageHandler(messageData: RawData, senderWS: WebSocket): void {
    const messageDataJSON: Message = JSON.parse(messageData.toString())

    console.log(messageDataJSON)

    messagesTypesHandlers.find(handlers => handlers.type === messageDataJSON.type)
        ?.handler(messageDataJSON.data, senderWS)
}
