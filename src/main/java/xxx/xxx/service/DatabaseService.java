package xxx.xxx.service;

import xxx.xxx.annotation.Service;
import xxx.xxx.util.Notifier;

@Service
public class DatabaseService {

	public DatabaseService() {}
	
	public String getEntry(String key) throws Exception {
		
		Notifier.playSound();
		
		return "I'm working! " + key;
	}
}
