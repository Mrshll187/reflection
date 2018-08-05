package xxx.xxx.controller;

import java.util.Date;

import javax.annotation.PostConstruct;

import spark.Spark;
import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;
import xxx.xxx.service.DatabaseService;
import xxx.xxx.util.SSLUtil;
import xxx.xxx.util.SoundInvoker;

@Controller
public class BaseController {

	@AutoWired
	private DatabaseService databaseService;
	
	@PostConstruct
	public void initRoute() {
				
		Spark.get("/", (req, res) -> {

			System.out.println("Testing " + databaseService.getEntry("test"));
			
			System.out.println("Request : "+req.ip());
			System.out.println(SSLUtil.getUser(req.raw()));
			
			SoundInvoker.playSound();
			
			return "Running : "+new Date();
			
		});
	}
}
