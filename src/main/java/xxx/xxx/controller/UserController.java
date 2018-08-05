package xxx.xxx.controller;

import javax.annotation.PostConstruct;

import spark.Spark;
import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;
import xxx.xxx.service.PropertyService;

@Controller
public class UserController {

	@AutoWired
	private PropertyService propertyService;
	
	@PostConstruct
	public void init() {
		
		Spark.get("/user", (req, res) -> {

			return propertyService.getEntry("Test!");			
		});
	}
}
