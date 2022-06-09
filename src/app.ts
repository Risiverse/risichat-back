import { serverListeningHandler, serverConnectionHandler, serverDisconnectHandler, serverErrorHandler } from './appServer'
import { WebSocketServer } from 'ws'


const host: string = process.env['WS_HOST'] ? process.env['WS_HOST'] : 'locahost'
const port: number = process.env['WS_PORT'] ? parseInt(process.env['WS_PORT']) : 9999

console.log('Starting WS server...')

export const WS = new WebSocketServer({
    host,
    port,
})

WS.on('listening', serverListeningHandler)

WS.on('connection', serverConnectionHandler)

WS.on('close', serverDisconnectHandler)

WS.on('error', serverErrorHandler)
