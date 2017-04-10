/*
 * Copyright 2017 Atypon Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atypon.wayf.xform;
 
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
 
import org.apache.commons.lang3.text.StrBuilder;
import org.dozer.CustomConverter;
import org.dozer.MappingException;
 
public class EnumStringBiDirectionalDozerConverter implements CustomConverter{
 
	@Override
 
	public Object convert(Object destination, Object source, Class destinationClass, Class sourceClass) {
 
		if(source == null)
 
			return null;
 
		if(destinationClass != null){
 
			if(destinationClass.getSimpleName().equalsIgnoreCase("String")){
 
				return this.getString(source);
 
			}else if( destinationClass.isEnum()){
 
				return this.getEnum(destinationClass, source);
 
			}else{
 
				throw new MappingException(new StrBuilder("Converter ").append(this.getClass().getSimpleName())
 
						   .append(" was used incorrectly. Arguments were: ")
 
						   .append(destinationClass.getClass().getName())
 
						   .append(" and ")
 
						   .append(source).toString());
 
			}
 
		}
 
		return null;
 
	}
 
 
 
	private Object getString(Object object){
 
		String value = object.toString();
 
		return value;
 
	}
 
	private Object getEnum(Class destinationClass, Object source){
 
		Object enumeration = null;
 
 
 
		Method [] ms = destinationClass.getMethods();
 
		for(Method m : ms){
 
			if(m.getName().equalsIgnoreCase("valueOf")){
 
				try {
 
					enumeration = m.invoke( destinationClass.getClass(), (String)source);
 
				}
 
				catch (IllegalArgumentException e) {
 
					e.printStackTrace();
 
				}
 
				catch (IllegalAccessException e) {
 
					e.printStackTrace();
 
				}
 
				catch (InvocationTargetException e) {
 
					e.printStackTrace();
 
				}
 
				return enumeration;
 
			}
 
		}
 
		return null;
 
	}
 
 
 
}