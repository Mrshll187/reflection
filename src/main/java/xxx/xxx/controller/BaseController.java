package xxx.xxx.controller;

import javax.annotation.PostConstruct;

import spark.Spark;
import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;
import xxx.xxx.service.DatabaseService;

@Controller
public class BaseController {

	@AutoWired
	private DatabaseService databaseService;
	
	@PostConstruct
	public void initRoute() {
				
		Spark.get("/", (req, res) -> {

			return "Running...";			
		});
	}
}