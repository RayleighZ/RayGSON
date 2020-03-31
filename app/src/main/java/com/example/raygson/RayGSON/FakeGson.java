package com.example.raygson.RayGSON;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FakeGson {
	  public <T> T fromJson(T arrowObject, String jsonData) throws Exception {
			//1 反射获取field

			Class totalClass = arrowObject.getClass();
			Field[] totalFields = totalClass.getDeclaredFields();
			Log.d("NUM", totalFields.length + "");

			//2 对同步送过来的jsonData进行解析
			JSONObject totalJsonObject = new JSONObject(jsonData);
			//开始对每一块filed进行定型和解析

			for (Field sonField :
					totalFields) {
				  //1 定型
				  switch (check(totalJsonObject, sonField.getName())) {
						case BASE_TYPE: {
							  Type sonFieldType = sonField.getGenericType();
							  //Class clazz = sonField.getClass();
									/*if (sonFieldType instanceof ParameterizedType) {
										  ParameterizedType pt = (ParameterizedType) sonFieldType;
										  Type[] types = pt.getActualTypeArguments();
										  if(types.length ==1){
												Class sonClass = (Class<?>)types[0];
												Log.d("true class",sonClass.getName());
												Method setObjectMethod = getSetMethod(sonField,totalClass);
												solveBasicType(sonField,setObjectMethod,sonClass.newInstance(),totalJsonObject.getString(types[0].getTypeName()));
										  }
							  }*/
							  Method totalSetMethod = getSetMethod(sonField, totalClass);
							  solveBasicType(sonField, totalSetMethod, arrowObject, totalJsonObject.getString(sonField.getName()));
							  break;
						}
						case OBJECT: {
							  Log.d("sonClass", sonField.getType().getName());
							  Type sonFieldType = sonField.getGenericType();
							  if (sonFieldType instanceof ParameterizedType) {
									ParameterizedType pt = (ParameterizedType) sonFieldType;
									Type[] types = pt.getActualTypeArguments();
									if(types.length ==1){
										  Class sonClass = (Class<?>)types[0];
										  Method setObjectMethod = getSetMethod(sonField,totalClass);
										  solveBasicType(sonField,setObjectMethod,sonClass.newInstance(),totalJsonObject.getString(types[0].getTypeName()));
									}
							  }
							  Class sonClass = Class.forName(sonField.getType().getName());
							  Method setObjectMethod = getSetMethod(sonField, totalClass);
							  setObjectMethod.invoke(arrowObject,
									  solveObject(sonClass.newInstance(),
											  totalJsonObject.getJSONObject(sonField.getName())));
							  break;
						}
						case ARRAY: {
							  //先判断数组类型，是基本类型数组还是对象数组
							  JSONArray sonJsonArray = totalJsonObject.getJSONArray(sonField.getName());
							  if(sonJsonArray.get(0).toString().charAt(0) == '{'){
							  	  solveObjectArray(arrowObject,sonField.getClass().newInstance(),getSetMethod(sonField,totalClass),sonJsonArray.toString());
							  } else {
							  	  solveBasicArray(arrowObject,sonField,getSetMethod(sonField,totalClass),sonJsonArray.toString());
							  }
						}
				  }
			}
			return arrowObject;
	  }

	  private final static int BASE_TYPE = 1;
	  private final static int OBJECT = 2;
	  private final static int ARRAY = 3;

	  private int check(JSONObject jsonObject, String fieldName) throws JSONException {
			String data = jsonObject.getString(fieldName);
			if (data.charAt(0) == '[') {
				  return ARRAY;
			} else if (data.charAt(0) == '{') {
				  return OBJECT;
			} else return BASE_TYPE;
	  }

	  //处理复合数据类型，思来想去，决定用一手递归函数
	  //最终解析为基本数据类型
	  //这样可以解析多层数据，对象中套对象的情况
	  private <T> T solveObject(T sonObject, JSONObject sonJsonObject) throws Exception {
	  	  /*fieldName = fieldName.substring(0,1).toLowerCase() + fieldName.substring(1);
	  	  JSONObject grandsonJsonObject = sonJsonObject.getJSONObject(fieldName);*/
			Class sonClass = sonObject.getClass();
			Field[] fields = sonClass.getDeclaredFields();
			for (Field grandsonField :
					fields) {
				  switch (check(sonJsonObject, grandsonField.getName())) {
						case BASE_TYPE: {
							  Method setMethod = getSetMethod(grandsonField, sonClass);
							  //JSONObject grandsonJsonObject = sonJsonObject.getJSONObject(grandsonField.getName());
							  solveBasicType(grandsonField, setMethod, sonObject, sonJsonObject.getString(grandsonField.getName()));
							  break;
						}
						case OBJECT: {
							  Class grandsonClass = Class.forName(grandsonField.getType().getName());
							  Method setMethod = getSetMethod(grandsonField, sonClass);
							  setMethod.invoke(sonObject,
									  solveObject(
											  grandsonClass.newInstance(),
											  sonJsonObject.getJSONObject(grandsonField.getName())));
							  break;
						}
				  }
			}
			return sonObject;
	  }

	  private void solveBasicType(Field field, Method setMethod, Object arrowObject, String data) throws InvocationTargetException, IllegalAccessException {
			Class clazz = field.getType();
			String type;
			Log.d("Type", clazz.getName());
			if (clazz.getName().equals("int")) {
				  type = "Integer";
			} else if (clazz.getName().equals("java.lang.String")) {
				  type = clazz.getName();
			} else {
				  type = clazz.getName().substring(0, 1).toUpperCase() + clazz.getName().substring(1);
			}

			Log.d("FakeType", type);
			switch (type) {
				  case "Integer": {
						setMethod.invoke(arrowObject, Integer.parseInt(data));
						break;
				  }
				  case "java.lang.String": {
						setMethod.invoke(arrowObject, data);
						break;
				  }
				  case "Boolean": {
						setMethod.invoke(arrowObject, Boolean.parseBoolean(data));
						break;
				  }
				  case "Double": {
						setMethod.invoke(arrowObject, Double.parseDouble(data));
						break;
				  }
				  case "Long": {
						setMethod.invoke(arrowObject, Long.parseLong(data));
						break;
				  }
			}
	  }

	  private Object solveBasicType(Class clazz, Method setMethod, Object arrowObject, String data) throws InvocationTargetException, IllegalAccessException {
			String type;
			Log.d("Type", clazz.getName());
			if (clazz.getName().equals("int")) {
				  type = "Integer";
			} else if (clazz.getName().equals("java.lang.String")) {
				  type = clazz.getName();
			} else {
				  type = clazz.getName().substring(0, 1).toUpperCase() + clazz.getName().substring(1);
			}

			Log.d("FakeType", type);
			switch (type) {
				  case "Integer": {
						setMethod.invoke(arrowObject, Integer.parseInt(data));
						break;
				  }
				  case "java.lang.String": {
						setMethod.invoke(arrowObject, data);
						break;
				  }
				  case "Boolean": {
						setMethod.invoke(arrowObject, Boolean.parseBoolean(data));
						break;
				  }
				  case "Double": {
						setMethod.invoke(arrowObject, Double.parseDouble(data));
						break;
				  }
				  case "Long": {
						setMethod.invoke(arrowObject, Long.parseLong(data));
						break;
				  }
			}
			return arrowObject;
	  }

	  //解决数组
	  //需要的参数：泛型类型 invoke的父类 json数据
	  private void solveObjectArray(Object father , Field field ,Method setMethod, String jsonData) {
			try {
				  ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
				  Type[]types = parameterizedType.getActualTypeArguments();
				  Class clazz = Class.forName(types[0].toString().substring(6));
				  ArrayList<Object>arrayList = new ArrayList<>();
				  JSONArray jsonArray = new JSONArray(jsonData);
				  for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						arrayList.add(solveObject(clazz.newInstance(),jsonObject));
				  }
				  setMethod.invoke(father,arrayList);
			} catch (Exception e) {
				  e.printStackTrace();
			}
	  }

	  private void solveBasicArray(Object father,Field field,Method setMethod, String jsonData ){
			try {
				  ArrayList<Object>arrayList = new ArrayList<>();
				  ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
				  Type[]types = parameterizedType.getActualTypeArguments();
				  Class clazz = Class.forName(types[0].toString().substring(6));
				  JSONArray jsonArray = new JSONArray(jsonData);
				  for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						arrayList.add(clazz.cast(jsonObject.toString()));
				  }
				  setMethod.invoke(father,arrayList);
			} catch (Exception e) {
				  e.printStackTrace();
			}
	  }

	  public static class TypeToken <T>{
	  	  final Type type;

	  	  private TypeToken(){
	  	  	  this.type = getSuperclassTypeParameter(getClass());
		  }

		  private Type getType(){
	  	  	  return type;
		  }

		  Type getSuperclassTypeParameter(Class<?> subclass){
	  	  	  Type superclass = subclass.getGenericSuperclass();
	  	  	  if(superclass instanceof Class){
	  	  	  	  //稍作改动，预备直接把所有的类型都过一遍
	  	  	  	  return superclass;
			  }
	  	  	  ParameterizedType parameterizedType = (ParameterizedType) superclass;
	  	  	  assert parameterizedType != null;
	  	  	  return parameterizedType.getActualTypeArguments()[0];
		  }
	  }

	  private Method getSetMethod(Field field, Class<?> clazz) throws Exception {
			StringBuilder setMethodName = new StringBuilder();
			setMethodName.append("set");
			String fileName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
			setMethodName.append(fileName);
			Log.d("MethodName", setMethodName.toString());
			Log.d("Class", clazz.getName());
			return clazz.getMethod(setMethodName.toString(), field.getType());
	  }
	  private Method getSetMethod(String name, Class type,Class<?> clazz) throws Exception {
			StringBuilder setMethodName = new StringBuilder();
			setMethodName.append("set");
			String fileName = name.substring(0, 1).toUpperCase() + name.substring(1);
			setMethodName.append(fileName);
			Log.d("MethodName", setMethodName.toString());
			Log.d("Class", clazz.getName());
			return clazz.getMethod(setMethodName.toString(), type);
	  }
}
