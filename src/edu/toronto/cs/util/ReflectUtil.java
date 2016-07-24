package edu.toronto.cs.util;

import java.lang.reflect.*;


/****
 **** Reflection utility class
 ****/
public class ReflectUtil
{
  public static Object callStaticMethod (Class clazz, String methodName, 
					 Class[] params, Object[] args)
    throws IllegalAccessException, 
	   InvocationTargetException, 
	   NoSuchMethodException
  {
    Method method = clazz.getDeclaredMethod (methodName, params);
    return method.invoke (null, args);
  }
  
}
