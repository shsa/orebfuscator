package Orebfuscator;

import java.lang.reflect.Field;

public class Fields 
{
	public static int findFieldIndex(Class<?> clazz, String fieldName)
	{
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			if (fields[i].getName().equals(fieldName))
				return i;
		}
		return -1;
	}
	
	public static Object getValue(Object instance, int index)
	{
		try {
			Field field = instance.getClass().getDeclaredFields()[index];
            field.setAccessible(true);
			return field.get(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getValue(Object instance, String name)
	{
		try {
			Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
			return field.get(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getValue(Object instance, Field field)
	{
		try {
			return field.get(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Field getField(Object instance, String name)
	{
		try {
			Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setValue(Object instance, int index, Object value)
	{
		try {
			Field field = instance.getClass().getDeclaredFields()[index];
            field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setValue(Object instance, Field field, Object value)
	{
		try 
		{
			field.setAccessible(true);
			field.set(instance, value);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static class NetworkManager
	{
		public static String getOutboundPacketsQueueName()
		{
			return "outboundPacketsQueue";
		}
		
		public static int getReceivedPacketsQueueIndex()
		{
			return 10;
		}
		
		public static int getOutboundPacketsQueueIndex()
		{
			return 11;
		}
		
		public static int getChannelIndex()
		{
			return 12;
		}
	}

	
	public static class InboundHandlerTuplePacketListener
	{
		public static int getPacketIndex()
		{
			return 0;
		}
	}
}
