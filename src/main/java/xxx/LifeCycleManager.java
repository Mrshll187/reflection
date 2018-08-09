package xxx;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;

import spark.Spark;
import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Controller;
import xxx.xxx.annotation.Property;
import xxx.xxx.annotation.Service;
import xxx.xxx.service.DatabaseService;
import xxx.xxx.service.PropertyService;

public class LifeCycleManager {

	private static Logger logger = Logger.getLogger(App.class.getName());

	private static Reflections reflections = new Reflections(App.class.getPackage().getName());
	private static ConcurrentHashMap<Class<?>, Object> instantiatedClasses = new ConcurrentHashMap<>();

	static { 
		
		instantiateComponents(PropertyService.class);
		postConstructInstances();
	}
	
	public static void start() {
		
		PropertyService propertyService = getInstace(PropertyService.class);
		String port = propertyService.getProperty("webapp.port");
		
		Spark.port(Integer.parseInt(port));
		
		for (Class<?> clazz : reflections.getTypesAnnotatedWith(Service.class))
			instantiateComponents(clazz);
		
		for (Class<?> clazz : reflections.getTypesAnnotatedWith(Controller.class))
			instantiateComponents(clazz);
		
		bindProperties();
		
		postConstructInstances();
		
		logger.info("Webapp Started : "+ propertyService.getStartTime());
	}
	
	public static void shutDown(String message, Level level) {
		
		logger.log(level, message);
		
		if(instantiatedClasses.containsKey(DatabaseService.class))
			getInstace(DatabaseService.class).shutDown();
		
		Spark.stop();
		
		if(level == Level.INFO)
			System.exit(0);
		else
			System.exit(1);
	}
	
	public static void postConstructInstances() {
		
		for(Class<?> clazz : instantiatedClasses.keySet()) {
			for (Method method : clazz.getMethods()) {
				if (method.isAnnotationPresent(PostConstruct.class)) {
	
					try {
	
						if(method.getParameterTypes().length != 0) 
							throw new Exception("Remove parameters from @PostContruct method in "+clazz.getName());
						
						if(method.getReturnType() != Void.TYPE)
							throw new Exception("Please set return type to 'void' for @PostContruct method in "+clazz.getName());
						
						method.invoke(instantiatedClasses.get(clazz));
						
						logger.info("Instantiated class : " + clazz.getName());
					} 
					catch (Exception e) {
	
						logger.severe("Failure invoking @PostContruct method " + method.getName()+ " in class " + clazz.getName());
						System.exit(1);
					}
				}
			}
		}
	}
	
	public static void bindProperties() {
		
		PropertyService propertyService = getInstace(PropertyService.class);
		
		for(Class<?> clazz : instantiatedClasses.keySet()) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(Property.class)) {

					try {

						String propertyKey = field.getAnnotation(Property.class).value();
						
						field.setAccessible(true);
						Type declaredFieldType = field.getGenericType();
						
						String propertyValue = propertyService.getProperty(propertyKey);
						
						if(field.getType().isAssignableFrom(String.class))
							field.set(instantiatedClasses.get(clazz), new String(propertyValue));
						else if(declaredFieldType == Integer.TYPE)
							field.set(instantiatedClasses.get(clazz), Integer.parseInt(propertyValue));
						else if(declaredFieldType == Double.TYPE)
							field.set(instantiatedClasses.get(clazz), Double.parseDouble(propertyValue));
						else 
							field.set(instantiatedClasses.get(clazz), new String(propertyValue));
						
						field.setAccessible(false);
					} 
					catch (Exception e) {

						logger.severe("Failure invoking binding field " + field.getName()+ " in class " + clazz.getName());
						System.exit(1);
					}
				}
			}	
		}
	}
	
	public static <T> T getInstace(Class<T> clazz){
		return clazz.cast(instantiatedClasses.get(clazz));
	}
	
	private static void instantiateComponents(Class<?> clazz) {

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
	}
}
