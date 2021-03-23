package util;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class ReleeseResources {
	
	private static MongoClient mongoClient;
	private static MongoDatabase db;

	public static void closeConnection(){
		BasicDBObject document = new BasicDBObject();
		db.getCollection("lock").deleteMany(document);
		mongoClient.close();
	}

	public static void setMongoClient(MongoClient mongoClient) {
		ReleeseResources.mongoClient = mongoClient;
	}

	public static void setDb(MongoDatabase db) {
		ReleeseResources.db = db;
	}

	public static MongoClient getMongoClient() {
		return mongoClient;
	}
}
