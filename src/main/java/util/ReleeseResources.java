package util;

import com.mongodb.client.MongoClient;

public class ReleeseResources {
	
	public static MongoClient mongoClient;
	
	public static void releeseResources() {
		
		if(mongoClient!=null) {
			mongoClient.close();
		}
	}

}
