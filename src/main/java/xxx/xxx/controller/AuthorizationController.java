package xxx.xxx.controller;

import javax.annotation.PostConstruct;

import spark.Spark;
import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;
import xxx.xxx.service.PropertyService;
import xxx.xxx.util.Notifier;

@Controller
public class AuthorizationController {

	@AutoWired
	private PropertyService propertyService;
	
	@PostConstruct
	public void init() {
		
		Spark.before((requet, response) ->{
		
			Notifier.playSound();
			System.out.println("Caught .....");
		});
	}
}
