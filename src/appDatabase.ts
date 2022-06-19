import { MongoClient } from "mongodb"
import 'dotenv/config'


if (!process.env['MONGO_HOST'] || !process.env['MONGO_DB'] || !process.env['MONGO_COLLECTION']) {
    throw new Error("Error during DB connection initialization, check .env variables.");
}


const mongoClient = new MongoClient(process.env['MONGO_HOST'])
const database = mongoClient.db(process.env['MONGO_DB'])
const collection = database.collection(process.env['MONGO_COLLECTION'])


export async function initDatabase(): Promise<void> {
    console.log('Connection to MongoDB...')
    await mongoClient.connect()
    console.log('Connection with MongoDB established !')
}


export async function insertMessageIntoDB(message: string): Promise<void> {
    await collection.insertOne(JSON.parse(message))
}


export function closeDatabase(): void {
    mongoClient.close()
}
