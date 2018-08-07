package xxx.xxx.service;

import java.util.Date;

import javax.annotation.PostConstruct;

import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Service;
import xxx.xxx.util.Notifier;

@Service
public class PropertyService {

	private Date startTime = new Date();
	
	@PostConstruct
	public void init() {
		System.out.println("I was started");
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public String getDatabasePath() {
		return System.getProperty("user.home") + "/database";
	}
}
