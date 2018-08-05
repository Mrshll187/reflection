package xxx.xxx;

import spark.Spark;
import xxx.xxx.util.Instantiator;

public class App {
    
	public static void main(String[] args) throws Exception {

		String keyPath = App.class.getResource("/keystore.p12").getFile();
		String trustPath = App.class.getResource("/truststore.jks").getFile();
		
		Spark.port(8080);
		Spark.secure(keyPath, "phuctme1", trustPath, "phuctme1");
		
		Instantiator.start();
    }
}