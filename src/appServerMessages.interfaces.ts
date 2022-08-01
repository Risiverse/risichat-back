export interface chatMessage {
    timestamp: number,
    username: string,
    content: string
}

export interface connectionMessage {
    message: string
}

export interface errorMessage {
    status: number,
    message: string,
    data: any
}

export interface clientMessage {
    userSSOID: number,
    type: string,
    data: chatMessage
}

export interface serverMessage {
    type: string,
    data: chatMessage | errorMessage | connectionMessage
}