package io.github.sbfanton.modeltestutils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Testing utility class.
 * <p>
 * Utility class for testing constructors, getter, setter, equals, hash code 
 * and clone methods of models or entities.
 * </p>
 * 
 * @author Sol Fanton
 * @version 1.0.0
 * @since 2025
 */

public class ModelTestUtils {
	
	/**
     * Tests correct object instantiation with parameterless constructor.
     *
     * @param <T> The type of the values.
     * @param clazz Object class.
     */
	public static <T> void testNoArgsConstructor(Class<T> clazz) throws Exception {
		if (clazz == null) {
	        throw new NullPointerException("type must not be null");
	    }
        Constructor<T> constructor = clazz.getConstructor();
        T instance = constructor.newInstance();
        assertNotNull(instance);
    }

	/**
     * Tests correct object instantiation with class all attributes constructor.
     *
     * @param <T> The type of the values.
     * @param clazz Object class.
     * @param testData Map with attributes of the class. Key: attribute name, Value: attribute value.
     */
    public static <T> void testAllArgsConstructor(Class<T> clazz, Map<String, Object> testData) throws Exception {
    	if (clazz == null || testData == null) {
    		throw new NullPointerException("Parameters must not be null");
	    }
    	
    	Object[] attrs = testData.values().toArray();
    	Class<?>[] parameterTypes = ReflectionUtils.getParametersType(attrs);
        Constructor<T> constructor = clazz.getConstructor(parameterTypes);
        T instance = constructor.newInstance(attrs);
        assertNotNull(instance);
    }
    
    /**
     * Tests correct object instantiation with all declared constructors.
     *
     * @param <T> The type of the values.
     * @param clazz Object class.
     */
    public static <T> void testAllConstructors(Class<T> clazz) throws Exception {
    	if (clazz == null) {
	        throw new NullPointerException("type must not be null");
	    }
    	Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
        	T instance = ReflectionUtils.getInstanceByConstructor(constructor);
        	assertNotNull(instance);
        }
    }
    
    /**
     * Tests object getter and setter methods.
     * 
     * @param <T> The type of the values.
     * @param clazz Object class.
     */
    @SuppressWarnings("unchecked")
	public static <T> void testGettersAndSetters(Class<T> clazz) throws Exception {
    	if (clazz == null) {
	        throw new NullPointerException("type must not be null");
	    }
    	
    	T instance = (T) (ReflectionUtils.hasNoArgConstructor(clazz) ?
					 clazz.getConstructor().newInstance() :
					 ReflectionUtils.getInstanceByConstructor(clazz.getDeclaredConstructors()[0]));
    	
        Field[] fields = clazz.getDeclaredFields();
        List<String> attrsNames = new ArrayList<String>();
        
        for(Field field: fields) {
        	String name = field.getName();
        	if(!name.equals(Constants.JACOCO_ATTR))
        		attrsNames.add(field.getName());
        }

        // loop through array with class attributes
        for (String attr: attrsNames) {
            String setterName = "set" + capitalize(attr);
            String getterName = "get" + capitalize(attr); 
            Method[] methods = clazz.getMethods();
            
            for(Method method: methods) {
            	
            	if(method.getName().equals(setterName)) {
            	
	            	Parameter[] parameters = method.getParameters();
	            	List<Object> argsToPassList = new ArrayList<Object>();
	            	
	            	for(Parameter param: parameters) {
	            		Object obj = null;
	            		
	            		while(obj == null) {
	            			obj = ReflectionUtils.generateRandomValue(param.getType());
	            		}
	                	argsToPassList.add(obj);
	                }
	            	
	            	method.invoke(instance, argsToPassList.toArray());
	            	
	            	if(ReflectionUtils.existsMethodByName(methods, getterName)) {
		            	Method getterMethod = clazz.getMethod(getterName); 
		                Object actualValue = getterMethod.invoke(instance);
		                if(actualValue == null)
		                	System.out.println(actualValue + " " + getterName);
		                assertNotNull(actualValue);
		                //TODO: add assertion with value set by setter
	            	}
            	}
            	
            }
        }
    }
    
    /**
     * Tests object {@code equals} and {@code hashCode} methods.
     *
     * @param <T> The type of the values.
     * @param clazz Object class.
     * @param testData Map with attributes of the class. Key: attribute name, Value: attribute value.
     */
    @SuppressWarnings("rawtypes")
 	public static <T> void testEqualsAndHashCode(Class<T> clazz, Map<String, Object> testData) throws Exception {
    	if (clazz == null || testData == null) {
    		throw new NullPointerException("Parameters must not be null");
	    }
    	
    	try {
	    	Object[] attrs = testData.values().toArray();
	 		Class<?>[] parameterTypes = ReflectionUtils.getParametersType(attrs);
	 		Constructor<T> constructor = clazz.getConstructor(parameterTypes);
	 		
	 		Object[] clonedArgs = attrs.clone();
	 	    
	 		Class<?> argType = clonedArgs[0].getClass();
	 		Object oriObj = clonedArgs[0];
	 	    Object randomValue = ReflectionUtils.generateRandomValueDifferentFromOriginal(argType, oriObj);
	 	    Object[] modifiedArgs = clonedArgs;
	 	    modifiedArgs[0] = randomValue;
	 		
	 		T instance1 = constructor.newInstance(attrs);
	 		T instance2 = constructor.newInstance(attrs);
	 		T instance3 = constructor.newInstance(modifiedArgs);
	 		
	 		// Test reflexivity
	         assertTrue(instance1.equals(instance1));
	
	         // Test symmetry
	         assertTrue(instance1.equals(instance2));
	         assertTrue(instance2.equals(instance1));
	
	         // Test null case
	         assertFalse(instance1.equals(null));
	
	         // Test different class case
	         assertFalse(instance1.equals(ReflectionUtils.generateRandomValue(String.class)));
	
	         // Test different values
	         assertFalse(instance1.equals(instance3));
	         
	         //Test HashCode
	         assertEquals(instance1.hashCode(), instance2.hashCode());
	         assertNotEquals(instance1.hashCode(), instance3.hashCode());
	         
	         List<T> listInstances = new ArrayList<T>();
	         
	         for(int i = 0; i < clonedArgs.length; i++) {
	         	Object[] argsIt = attrs.clone();
	         	Class<?> type = ReflectionUtils.getParametersType(argsIt[i])[0];
	         	
	         	if(type.equals(List.class)) {
	         		List list = new ArrayList();
	         		argsIt[i] = list;
	         	}
	         	else if(type.equals(Map.class)) {
	         		Map map = new HashMap();
	         		argsIt[i] = map;
	         	}
	         	else if(type.equals(Boolean.class)) {
	         		argsIt[i] = !(Boolean)argsIt[i];
	         	}
	         	else {
	         		argsIt[i] = ReflectionUtils.generateRandomValue(type);
	         	}
	         	T itInstanceWithOutNull = constructor.newInstance(argsIt);
	         	argsIt[i] = null;
	         	T itInstanceNull = constructor.newInstance(argsIt);
	         	listInstances.add(itInstanceNull);
	         	listInstances.add(itInstanceWithOutNull);
	         }
	         
	         for(int i = 0; i < listInstances.size(); i++) {
	         	assertTrue(listInstances.get(i).equals(listInstances.get(i)));
	         	assertEquals(listInstances.get(i).hashCode(), listInstances.get(i).hashCode());
	         }
         
    	} catch (NoSuchMethodException e) {
            throw new Exception("No constructor found with the specified parameter types", e);
        }
 	}
 	
    /**
     * Tests object {@code clone} method.
     *
     * @param <T> The type of the values.
     * @param clazz Object class.
     */
 	@SuppressWarnings("unchecked")
	public static <T> void testClone(Class<T> clazz) throws Exception {
 		if (clazz == null) {
	        throw new NullPointerException("type must not be null");
	    }
 		
 		T instance = (T) (ReflectionUtils.hasNoArgConstructor(clazz) ?
 					 clazz.getConstructor().newInstance() :
 					 ReflectionUtils.getInstanceByConstructor(clazz.getDeclaredConstructors()[0]));
         
         Field[] fields = clazz.getDeclaredFields();
         List<String> attrsNames = new ArrayList<String>();
         
         for(Field field: fields) {
         	String name = field.getName();
         	if(!name.equals(Constants.JACOCO_ATTR))
         		attrsNames.add(field.getName());
         }

         // loop through array with class attributes
         for (String attr: attrsNames) {
             String setterName = "set" + capitalize(attr);
             Method[] methods = clazz.getMethods();
             
             for(Method method: methods) {
             	
             	if(method.getName().contains(setterName)) {
             	
 	            	Parameter[] parameters = method.getParameters();
 	            	List<Object> argsToPassList = new ArrayList<Object>();
 	            	
 	            	for(Parameter param: parameters) {
 	                	argsToPassList.add(ReflectionUtils.generateRandomValue(param.getType()));
 	                }
 	            	
 	            	method.invoke(instance, argsToPassList.toArray());
             	}
             }
         }

 		T clonedInstance = (T) clazz.getDeclaredMethod("clone").invoke(instance);
         
         assertNotSame(instance, clonedInstance);
 	}

    // Capitalizes a string
    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
