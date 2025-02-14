package io.github.sbfanton.modeltestutils;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Object reflection utility class.
 * <p>
 * Utility class to generate object instances, 
 * with attribute value setting according to their type, in a random manner.
 * </p>
 * 
 * @author Sol Fanton
 * @version 1.0.0
 * @since 2025
 */
public class ReflectionUtils {
	
	 /**
     * Attribute used to generate random values.
     */
	private static final SecureRandom random = new SecureRandom();

	/**
     * Generates an instance of an object, with all its attributes initialized.
     *
     * @param <T> The type of the values.
     * @param clazz Object class.
     * @param testData Map with attributes of the object class. Key: attribute name, Value: attribute value.
     * @return The instantiated object.
     */
    public static <T> T getAllInitilizedAttrsInstance(Class<T> clazz, Map<String, Object> testData) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
    	Constructor<T> constructor = clazz.getConstructor();
        T instance = constructor.newInstance();
        
        for (Entry<String, Object> entry : testData.entrySet()) {
            String fieldName = entry.getKey();  
            Object value = entry.getValue(); 
            
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            
            if(field.get(instance) == null) {
            	field.set(instance, value);
            }
        }
        
        return instance;
    }
    
    /**
     * Generates an instance of an object by using the particular constructor passed as a parameter.
     *
     * @param <T> The type of the values.
     * @param constructor Object particular constructor.
     * @return The instantiated object.
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	public static <T> T getInstanceByConstructor(Constructor<?> constructor) throws Exception {
    	constructor.setAccessible(true);
    	Parameter[] parameters = constructor.getParameters();
    	List<Object> argsToPassList = new ArrayList<Object>();
    	T instance = null;
    	
    	for(Parameter param: parameters) {
        	argsToPassList.add(generateRandomValue(param.getType()));
        }
        if(argsToPassList.size() > 0) {
        	Object[] attrs = argsToPassList.toArray();
        	instance = (T) constructor.newInstance(attrs);
        }
        else {
        	instance = (T) constructor.newInstance();
        }
        
        return instance;
    }
    
    
    /**
     * Retrieves the runtime types of the given arguments.
     *
     * @param args The arguments whose types will be determined.
     * @return An array of {@code Class<?>} representing the types of the given arguments.
     *         If no arguments are provided, returns an empty array.
     */
    public static Class<?>[] getParametersType(Object... args) {
    	Class<?>[] parameterTypes = new Class<?>[args.length];

    	for (int i = 0; i < args.length; i++) {
    	    Object obj = args[i];
    	    if (obj instanceof List) {
    	        parameterTypes[i] = List.class;
    	    } else if (obj instanceof Map) {
    	        parameterTypes[i] = Map.class;
    	    } else {
    	        parameterTypes[i] = obj.getClass();
    	    }
    	}
    	
    	return parameterTypes;
    }

    
    /**
     * Instantiates an inner class of another class, specifying the constructor number to use 
     * from the list of available constructors.
     *
     * @param <T> The type of the values.
     * @param innerClassName The inner class name.
     * @param constructorNum Inner class constructor numeric position from constructor list
     * @return The instantiated inner class object.
     * @throws Exception 
     */
	@SuppressWarnings("unchecked")
	public static <T> T instantiateInnerClass(String innerClassName, int constructorNum) throws Exception {
		
		Class<?> innerClass = Class.forName(innerClassName);
		Constructor<?> constructor = innerClass.getConstructors()[constructorNum];
        constructor.setAccessible(true);
    	Parameter[] parameters = constructor.getParameters();
    	List<Object> argsToPassList = new ArrayList<Object>();
    	T instance = null;
    	
    	for(Parameter param: parameters) {
        	argsToPassList.add(generateRandomValue(param.getType()));
        }
        if(argsToPassList.size() > 0) {
        	Object[] attrs = argsToPassList.toArray();
        	instance = (T) constructor.newInstance(attrs);
        }
        return instance;
        
	}
	
	/**
	 * Checks if a method with the given name exists in the provided array of methods.
	 *
	 * @param methods An array of {@code Method} objects to search within.
	 * @param methodName The name of the method to look for.
	 * @return {@code true} if a method with the specified name exists in the array, {@code false} otherwise.
	 * @throws NullPointerException if {@code methods} or {@code methodName} is {@code null}.
	 */
	public static boolean existsMethodByName(Method[] methods, String methodName) {
	    if (methods == null || methodName == null) {
	        throw new NullPointerException("methods and methodName must not be null");
	    }
	    for (Method method : methods) {
	        if (method.getName().equals(methodName)) {
	            return true;
	        }
	    }
	    return false;
	}
	

	/**
	 * Generates a random value of the specified type.
	 *
	 * @param <T> The type of object to generate.
	 * @param type The {@code Class} representing the type of object to create.
	 * @param args Optional additional class parameters, if needed for instantiation.
	 * @return A randomly generated object of the specified type.
	 * @throws Exception If an error occurs during instantiation or value generation.
	 */
	public static <T> Object generateRandomValue(Class<T> type, Class<?>... args) throws Exception {
        if (type == Integer.class || type == int.class) {
            return random.nextInt(1000) + 100; // Range: 100 to 1099
        } else if (type == Double.class || type == double.class) {
            return random.nextDouble() * 1000 + 100;
        } else if (type == Float.class || type == float.class) {
            return random.nextFloat() * 1000 + 100;
        } else if (type == Long.class || type == long.class) {
            return random.nextLong() % 1000 + 100;
        } else if (type == Boolean.class || type == boolean.class) {
        	return random.nextBoolean();
        } else if (type == String.class) {
            int length = 10; // Random string length
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                char c = (char) (random.nextInt(26) + 'a');
                sb.append(c);
            }
            return sb.toString();
        } else if (type == Timestamp.class) {
            long currentTime = System.currentTimeMillis();
            long randomTime = currentTime + random.nextInt(10000000);
            return new Timestamp(randomTime);
        } else if(type == List.class || hasListInterface(type)) {
        	if (args != null && args.length > 0) {
                Class<?> elementType = args[0]; // Type of elements in the list
                int listSize = 1;
                List<Object> list = new ArrayList<Object>();

                // Generate random items for the list
                for (int i = 0; i < listSize; i++) {
                    Object element = generateRandomValue(elementType);
                    list.add(element);
                }

                return convertList(list, elementType);
        	}
        	else {
        		return new ArrayList<Object>();
        	}
        	
        } else if(type.getPackage() != null &&
        		type.getPackage().getName() != null &&
        		!type.getPackage().getName().startsWith("java") && 
        		hasNoArgConstructor(type)){
        	Constructor<T> constructor = type.getConstructor();
            T instance = constructor.newInstance();
            for (Field field : type.getDeclaredFields()) {
            	if(!field.getName().equals(Constants.JACOCO_ATTR) && 
            	   !isStatic(field.getModifiers()) &&
            	   !isFinal(field.getModifiers())) {
	                field.setAccessible(true);
	                field.set(instance, generateRandomValue(
	                		field.getType(),
	                		checkGenericType(field)));
            	}
            }
            return instance != null ? instance : new Object();
        } else if (type == Class.class) {
        	return type;
        } else if (type == Throwable.class) {
        	return new Throwable("Cause");
        }
        
        return mock(type);
    }
	
	/**
	 * Checks if the given class has a no-argument constructor.
	 *
	 * @param <T> The type of the class being checked.
	 * @param clazz The {@code Class} object representing the type to inspect.
	 * @return {@code true} if the class has a no-argument constructor, {@code false} otherwise.
	 * @throws NullPointerException if {@code clazz} is {@code null}.
	 */
	public static <T> boolean hasNoArgConstructor(Class<T> clazz) {
	    if (clazz == null) {
	        throw new NullPointerException("clazz must not be null");
	    }
	    try {
	        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

	        for (Constructor<?> constructor : constructors) {
	            if (constructor.getParameterCount() == 0) {
	                return true;
	            }
	        }
	        return false;
	    } catch (Exception e) {
	        return false;
	    }
	}
    
	/**
	 * Retrieves the generic type(s) of a given field.
	 *
	 * @param field The {@code Field} object whose generic type(s) will be checked.
	 * @return An array of {@code Class<?>} representing the generic type(s) of the field.
	 *         If the field is not parameterized, returns an empty array.
	 * @throws NullPointerException if {@code field} is {@code null}.
	 */
	public static Class<?>[] checkGenericType(Field field) {
	    if (field == null) {
	        throw new NullPointerException("field must not be null");
	    }	Type tipoGenerico = field.getGenericType();

        if (tipoGenerico instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) tipoGenerico;
            Type[] tipoArgumentos = parameterizedType.getActualTypeArguments();
            List<Class<?>> listaClases = new ArrayList<Class<?>>();

            for(Type tipo: tipoArgumentos) {
	            if (tipo instanceof Class<?>) {
	                Class<?> claseElemento = (Class<?>) tipo;
	                listaClases.add(claseElemento);
	               
	            }
            }
           
            Class<?>[] clases = listaClases.toArray(new Class<?>[0]);
            return clases;
        } 
        
        return new Class<?>[] {};
    }
    
	/**
	 * Converts a list of objects to a list of a specific type.
	 *
	 * @param <T> The target type of the list elements.
	 * @param list The list of objects to be converted.
	 * @param type The {@code Class} representing the target type.
	 * @return A list containing elements of the specified type.
	 * @throws NullPointerException if {@code list} or {@code type} is {@code null}.
	 * @throws ClassCastException if an element in the list cannot be cast to the specified type.
	 */
	@SuppressWarnings("finally")
	public static <T> List<T> convertList(List<Object> list, Class<T> type) {
	    if (list == null || type == null) {
	        throw new NullPointerException("list and type must not be null");
	    }
        List<T> resultList = new ArrayList<T>();

        try {
	        for (Object obj : list) {
	            resultList.add(type.cast(obj));
	        }
        } catch(Exception e) {
        	System.out.println("Casting could not be performed");
        }
        finally {
        	return resultList;
        }
    }
    
	
	/**
	 * Checks if the given class implements the {@code List} interface.
	 *
	 * @param <T> The type of the class to check.
	 * @param type The {@code Class} object representing the type to inspect.
	 * @return {@code true} if the class implements the {@code List} interface, {@code false} otherwise.
	 * @throws NullPointerException if {@code type} is {@code null}.
	 */
	public static <T> boolean hasListInterface(Class<T> type) {
	    if (type == null) {
	        throw new NullPointerException("type must not be null");
	    }
	    return List.class.isAssignableFrom(type);
	}

    
	/**
	 * Generates a random value of the specified type that is different from the given original object.
	 *
	 * @param <T> The type of object to generate.
	 * @param type The {@code Class} representing the type of object to create.
	 * @param oriObj The original object to ensure the generated value is different from.
	 * @return A randomly generated object of the specified type, different from {@code oriObj}.
	 * @throws Exception 
	 * @throws IllegalArgumentException if {@code oriObj} is {@code null} or the generated value cannot be created.
	 */
	public static <T> Object generateRandomValueDifferentFromOriginal(Class<T> type, Object oriObj) throws Exception {
	    if (oriObj == null) {
	        throw new IllegalArgumentException("Original object must not be null");
	    }
    	Object newObj = new Object();
    	
    	 do {
    		newObj = generateRandomValue(type);
    	} while(newObj.equals(oriObj));
    	
    	return newObj;
    }
    
	
	/**
	 * Generates a {@code LinkedHashMap} where the keys are the names of the fields of the given class
	 * and the values are random values assigned to those fields.
	 *
	 * @param <T> The type of the class whose fields will be used to generate the map.
	 * @param clazz The {@code Class} object representing the class whose field names and values will be used.
	 * @return A {@code LinkedHashMap} with field names as keys and randomly generated values as values.
	 * @throws IllegalArgumentException if {@code clazz} is {@code null}.
	 * @throws Exception If an error occurs while generating the random values for the fields.
	 */
	@SuppressWarnings("unchecked")
	public static <T> LinkedHashMap<String, Object> generateClassFieldsRandomValuesMap(Class<T> clazz) throws Exception {
	    if (clazz == null) {
	        throw new IllegalArgumentException("Class must not be null");
	    }
	    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
    	
    	while (clazz != null && clazz != Object.class) {
    		LinkedHashMap<String, Object> map2 = new LinkedHashMap<String, Object>();
    		map2.putAll(map);
    		map.clear();
    		
    		Field[] fields = clazz.getDeclaredFields();
	    	for(Field field: fields) {
	    		if(!field.getName().equals(Constants.JACOCO_ATTR)) {	
	    			map.put(field.getName(), generateRandomValue(
	    										field.getType(), 
	    										checkGenericType(field)));
	    		}
	    	}
	    	clazz = (Class<T>) clazz.getSuperclass();
	    	
	    	map.putAll(map2);
    	}

    	return map;
    }
}
