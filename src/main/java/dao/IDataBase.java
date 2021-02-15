package dao;

import java.io.IOException;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lock.Lock;

public interface IDataBase {

	boolean connect(Properties properties) throws Exception;
	void disconnect();
	Lock checkLock() throws JsonMappingException, JsonProcessingException;
	void openKontur(String path) throws IOException;
	void lockConnection();
	
}
