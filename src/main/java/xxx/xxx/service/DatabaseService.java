package xxx.xxx.service;

import xxx.xxx.annotation.Service;

@Service
public class DatabaseService {

	public DatabaseService() {}
	
	public String getEntry(String key) {
		
		return "I'm working! " + key;
	}
}
