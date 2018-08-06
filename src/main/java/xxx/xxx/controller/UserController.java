package xxx.xxx.controller;

import javax.annotation.PostConstruct;

import spark.Spark;
import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;
import xxx.xxx.service.DatabaseService;
import xxx.xxx.service.PropertyService;

@Controller
public class UserController {

	@AutoWired
	private PropertyService propertyService;
	
	@AutoWired
	private DatabaseService databaseService;
	
	@PostConstruct
	public void init() {
		
		Spark.get("/user/:key", (req, res) -> {

			String key = req.params(":key");
			String entry = databaseService.getEntry(key);
			
			return entry;			
		});
		
		Spark.delete("/user/:key", (req, res) -> {

			String key = req.params(":key");
			Boolean success = databaseService.delete(key);
			
			return success.toString();			
		});
		
		Spark.put("/user/:key/:value", (req, res) -> {

			String key = req.params(":key");
			String value = req.params(":value");
			
			Boolean success = databaseService.saveEntry(key, value);
			
			return success.toString(); 			
		});
	}
}
