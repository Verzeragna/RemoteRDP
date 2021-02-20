package util;

import java.io.FileInputStream;
import java.util.Properties;

public class Util implements IUtil{

	@Override
	public Properties getProperties(String path) throws Exception {
		// TODO Auto-generated method stub
		Properties properties = new Properties();
        FileInputStream fileInputStream = null;
		fileInputStream = new FileInputStream(path);
		properties.load(fileInputStream);
        return properties;
	}
}
