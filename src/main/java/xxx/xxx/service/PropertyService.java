package xxx.xxx.service;

import xxx.xxx.annotation.Service;
import xxx.xxx.util.Notifier;

@Service
public class PropertyService {

	public String getEntry(String key) throws Exception {
		return "I'm alive in Property Service : " + key;
	}
}
