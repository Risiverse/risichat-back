import { serverListeningHandler, serverConnectionHandler, serverDisconnectHandler, serverErrorHandler } from './appServer.js'
import { WebSocketServer } from 'ws'


export const WS = new WebSocketServer({
    host: process.env.WS_HOST,
    port: parseInt(process.env.WS_PORT),
})


WS.on('listening', serverListeningHandler)

WS.on('connection', serverConnectionHandler)

WS.on('close', serverDisconnectHandler)

WS.on('error', serverErrorHandler)
