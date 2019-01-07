package sc.ustc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DaoUtil {
	public static String getGetterName(String propertyName) {
		char[] buf = propertyName.toCharArray();
		buf[0] -= 32;
		return "get" + String.valueOf(buf);
	}
	
	public static String getSetterName(String propertyName) {
		char[] buf = propertyName.toCharArray();
		buf[0] -= 32;
		return "set" + String.valueOf(buf);
	}
	
	public static <T> Object getPropertyValue(T obj, String propertyName) {
		try {
			Class<T> clazz = (Class<T>) obj.getClass();
			Method method = clazz.getMethod(getGetterName(propertyName));
			return method.invoke(obj);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> void setPropertyValue(T obj, String propertyName, Object value, Class<?> type) {
		try {
			Class<T> clazz = (Class<T>) obj.getClass();
			Method method = clazz.getMethod(getSetterName(propertyName), type);
			method.invoke(obj, value);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Class<?> getType(String typeName){
		if (typeName.equalsIgnoreCase("int")) {
			return int.class;
		}
		if (typeName.equalsIgnoreCase("string")) {
			return String.class;
		}
		
		return null;
	}
}
