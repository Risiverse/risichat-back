import { broadcastToAllClients } from './appServer'
import { insertMessageIntoDB } from './appDatabase'
import type { RawData, WebSocket } from 'ws'
import type { serverMessage, clientMessage, chatMessage } from './appServerMessages.interfaces'

export function escapeUnsafeMessageData(messageData: string): string {
    if (!messageData) return ""
    return messageData
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;')
}

function sendChatMessageError(senderWS: WebSocket, message: string, data: any): void {
    const response: serverMessage = {
        type: 'error',
        data: {
            status: 400,
            message,
            data
        }
    }
    senderWS.send(JSON.stringify(response))
}

function chatMessageParser(messageData: chatMessage): string {
    const parsedJSON: serverMessage = {
        type: 'newMessage',
        data: {
            timestamp: Date.now(),
            username: escapeUnsafeMessageData(messageData.username),
            content: escapeUnsafeMessageData(messageData.content)
        }
    }
    return JSON.stringify(parsedJSON)
}


function isStringValid(field: string|null): boolean {
    return field != undefined &&
        (typeof field) === 'string' &&
        field !== ''
}


function isChatMessageValid(messageData: chatMessage): boolean {
    if (messageData.content == undefined) return false
    return isStringValid(messageData.content) &&
        isStringValid(messageData.username)
}


function chatMessageHandler(messageData: chatMessage, senderWS: WebSocket): void {
    if (isChatMessageValid(messageData)) {
        const validParsedMessage = chatMessageParser(messageData)
        broadcastToAllClients(validParsedMessage)
        insertMessageIntoDB(validParsedMessage)
    } else {
        sendChatMessageError(
            senderWS,
            'The message could not be validated. Be sure to respect the JSON format.',
            messageData)
    }
}


const messagesTypesHandlers = [
    { type: 'newMessage', handler: chatMessageHandler }
]


export function messageHandler(messageData: RawData, senderWS: WebSocket): void {
    let messageDataJSON: clientMessage

    try {
        messageDataJSON = JSON.parse(messageData.toString())
    } catch (error) {
        sendChatMessageError(
            senderWS,
            'Invalid WS message. Be sure to send stringified JSON.',
            messageData)
        return
    }

    messagesTypesHandlers.find(handlers => handlers.type === messageDataJSON.type)
        ?.handler(messageDataJSON.data, senderWS)
}
