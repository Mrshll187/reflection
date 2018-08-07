package xxx.xxx.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;

import xxx.xxx.App;
import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;
import xxx.xxx.annotation.Service;
import xxx.xxx.service.PropertyService;

public class Instantiator {

	private static Logger logger = Logger.getLogger(App.class.getName());

	private static ConcurrentHashMap<Class<?>, Object> instantiatedClasses = new ConcurrentHashMap<>();

	static { instantiateClass(PropertyService.class);}
	
	public static <T> T getInstace(Class<T> clazz){
		return clazz.cast(instantiatedClasses.get(clazz));
	}
	
	private static void instantiateClass(Class<?> clazz) {

		if (instantiatedClasses.containsKey(clazz))
			return;

		Object parent = null;
		
		Constructor<?> constructor = clazz.getConstructors()[0];
		constructor.setAccessible(true);
		
		try {
			
			parent = constructor.newInstance();
			instantiatedClasses.put(clazz, parent);
			constructor.setAccessible(false);
		}
		catch(Exception e) {
			
			logger.severe("Failure instantiating class " + clazz.getName() + " Problem : " + e.getMessage());
			System.exit(1);
		}
		
		for (Field field : clazz.getDeclaredFields()) {

			if (field.isAnnotationPresent(AutoWired.class)) {

				try {

					field.setAccessible(true);
					
					Class<?> autowiredClass = field.getType();
					
					Object autoWiredObj = null;
					
					if(instantiatedClasses.containsKey(autowiredClass))
						autoWiredObj = instantiatedClasses.get(autowiredClass);
					else
						autoWiredObj = autowiredClass.newInstance();
					
					field.set(parent, autoWiredObj);
					field.setAccessible(false);
					
					instantiatedClasses.put(autowiredClass, autoWiredObj);
					
					logger.info("Instantiated class : " + autowiredClass.getName());
				} 
				catch (Exception e) {

					logger.severe("Failure invoking @AutoWired field " + field.getName()+ " in class " + clazz.getName());
					System.exit(1);
				}
			}
		}
		
		for (Method method : clazz.getMethods()) {

			if (method.isAnnotationPresent(PostConstruct.class)) {

				try {

					if(method.getParameterTypes().length != 0) 
						throw new Exception("Remove parameters from @PostContruct method in "+clazz.getName());
					
					if(method.getReturnType() != Void.TYPE)
						throw new Exception("Please set return type to 'void' for @PostContruct method in "+clazz.getName());
					
					method.invoke(parent);
					
					logger.info("Instantiated class : " + clazz.getName());
				} 
				catch (Exception e) {

					logger.severe("Failure invoking @PostContruct method " + method.getName()+ " in class " + clazz.getName());
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
