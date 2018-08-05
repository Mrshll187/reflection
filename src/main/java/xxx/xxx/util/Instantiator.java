package xxx.xxx.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;

import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;

public class Instantiator {

	private static Logger logger = Logger.getLogger(Instantiator.class.getName());
	
	private static HashMap<Class<?>, Object> instantiatedClasses = new HashMap<>();
	
	private static void checkClassDepenecies(Class<?> clazz) {
	
		for(Field field : clazz.getDeclaredFields()) {
			
			if(field.isAnnotationPresent(AutoWired.class)) {
				
				try {
					
					field.setAccessible(true);
					
					Object autowiredObject = field.getType().newInstance();
					
					//WHY CANT I SET THIS?
					field.set(clazz, autowiredObject);
					
					field.setAccessible(false);
					
				}
				catch(Exception e) {
					
					logger.severe("Failure invoking class " + clazz.getName() + " Problem : " + e.getMessage());
					System.exit(1);
				}
			}
		}
		
		instantiateComponent(clazz);
	}
	
	private static void instantiateComponent(Class<?> clazz) {
	
		if(instantiatedClasses.containsKey(clazz))
			return;
		
    	for(Method method : clazz.getMethods()) {
    		
    		if(method.isAnnotationPresent(PostConstruct.class)) {
    			
    			try {
        			
    				Constructor<?> constructor = clazz.getConstructors()[0];
        			constructor.setAccessible(true);
        			Object object = constructor.newInstance();
        			
        			method.invoke(object);
        			constructor.setAccessible(false);
        			
        			instantiatedClasses.put(clazz, object);
    			}
    			catch(Exception e) {
    				
    				logger.severe("Failure invoking class " + clazz.getName() + " Problem : " + e.getMessage());
    				System.exit(1);
    			}
    		}
    	}
	}
	
	public static void start() {
		
		Reflections reflections = new Reflections("xxx");
		
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Controller.class))
        	checkClassDepenecies(clazz);
	}
}
