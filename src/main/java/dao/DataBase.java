package dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import lock.Lock;

public class DataBase implements IDataBase {

	public MongoClient mongoClient;
	public MongoDatabase db;

	public boolean connect(Properties properties) throws Exception {
		// TODO Auto-generated method stub
		Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.SEVERE);
		String strURL = "mongodb://" + properties.getProperty("user") + ":" + properties.getProperty("password") + "@"
				+ properties.getProperty("host") + ":" + properties.getProperty("port") + "/"
				+ properties.getProperty("dbname");
		mongoClient = MongoClients.create(strURL);
		db = mongoClient.getDatabase(properties.getProperty("dbname"));
		return true;
	}

	public void disconnect() {
		// TODO Auto-generated method stub

	}

	public Lock checkLock() throws JsonMappingException, JsonProcessingException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		MongoCollection<Document> col = db.getCollection("lock");
		FindIterable<Document> fi = col.find();
		MongoCursor<Document> cursor = fi.iterator();
		Lock lock = null;
		while (cursor.hasNext()) {
			lock = mapper.readValue(cursor.next().toJson(), Lock.class);
		}
		cursor.close();
		if (lock == null) {
			return new Lock("none");
		} else {
			return lock;
		}
	}

	public void openKontur(String path) throws IOException {
		// TODO Auto-generated method stub
		Process process = new ProcessBuilder(path,"/v:10.177.112.170","/f").start();
	}

	public void lockConnection() {
		// TODO Auto-generated method stub
		MongoCollection<Document> col = db.getCollection("lock");
		Document doc = new Document();
		doc.put("user", getUserName());
        doc.put("datestart", getCurrentDate());
		col.insertOne(doc);
	}

	private String getUserName() {

		String userName = System.getProperty("user.name");

		return userName;
	}

	private String getCurrentDate() {
		Date dateNow = new Date();
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MMMM.yyyy");
        String today = simpleDate.format(dateNow);
        return today;
	}
}
