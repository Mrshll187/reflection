package xxx.xxx.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;

import xxx.xxx.App;
import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;
import xxx.xxx.annotation.Service;

public class Instantiator {

	private static Logger logger = Logger.getLogger(Instantiator.class.getName());

	private static ConcurrentHashMap<Class<?>, Object> instantiatedClasses = new ConcurrentHashMap<>();

	private static void instantiateClass(Class<?> clazz) {

		if (instantiatedClasses.containsKey(clazz))
			return;

		Object parent = null;
		
		Constructor<?> constructor = clazz.getConstructors()[0];
		constructor.setAccessible(true);
		
		try {
			
			parent = constructor.newInstance();
		}
		catch(Exception e) {
			
			logger.severe("Failure instantiating class " + clazz.getName() + " Problem : " + e.getMessage());
			System.exit(1);
		}
		
		for (Method method : clazz.getMethods()) {

			if (method.isAnnotationPresent(PostConstruct.class)) {

				try {

					method.invoke(parent);
					constructor.setAccessible(false);

					instantiatedClasses.put(clazz, parent);
					
					logger.info("Instantiated class : " + clazz.getName());
				} 
				catch (Exception e) {

					logger.severe("Failure invoking @PostContruct method " + method.getName()+ " in class " + clazz.getName());
					System.exit(1);
				}
			}
		}
		
		for (Field field : clazz.getDeclaredFields()) {

			if (field.isAnnotationPresent(AutoWired.class)) {

				try {

					Class<?> autowiredClass = field.getType();
					
					field.setAccessible(true);
					field.set(parent, autowiredClass.newInstance());
					field.setAccessible(false);
					
					logger.info("Instantiated class : " + autowiredClass.getName());
				} 
				catch (Exception e) {

					logger.severe("Failure invoking @AutoWired field " + field.getName()+ " in class " + clazz.getName());
					System.exit(1);
				}
			}
		}
	}
	
	public static void start() {
		
		Reflections reflections = new Reflections(App.class.getPackage().getName());
		
		for (Class<?> clazz : reflections.getTypesAnnotatedWith(Service.class))
			instantiateClass(clazz);
		
		for (Class<?> clazz : reflections.getTypesAnnotatedWith(Controller.class))
			instantiateClass(clazz);
	}
}
