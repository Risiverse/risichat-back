import { MongoClient } from "mongodb"


const mongoClient = new MongoClient(process.env.MONGO_HOST)
const database = mongoClient.db(process.env.MONGO_DB)
const collection = database.collection(process.env.MONGO_COLLECTION)

export async function initDatabase() {
    await mongoClient.connect()
    console.log('Connection with MongoDB established.')
}


export async function insertMessageIntoDB(message) {
    const insertResult = await collection.insertOne(JSON.parse(message))
    console.log('Inserted documents =>', insertResult)
}


export function closeDatabase() {
    mongoClient.close()
}
