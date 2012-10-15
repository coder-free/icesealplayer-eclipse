package com.zbf.iceseal.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class BaseBean {
	protected JSONObject jsonObject = new JSONObject();
	public BaseBean(){
	}
	public BaseBean(JSONObject jsonObject) throws Exception{
		setJsonObject(jsonObject);
	}
	public String toString() {
		try {
			jsonObject = getJsonObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	public JSONObject getJsonObject() throws Exception{
		Field[] fields = this.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			if(!fields[i].getName().equals("jsonObject") && !fields[i].getName().equals("CREATOR")){
				fields[i].setAccessible(true);
				jsonObject.put(fields[i].getName(), fields[i].get(this));
			}
		}
		return jsonObject;
	}
	public JSONObject getFieldsJsonObject() throws Exception{
		JSONObject jsonObject = new JSONObject();
		Field[] fields = this.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			if(!fields[i].getName().equals("jsonObject") && !fields[i].getName().equals("CREATOR")){
				fields[i].setAccessible(true);
				jsonObject.put(fields[i].getName(), fields[i].get(this));
			}
		}
		return jsonObject;
	}
	public void setJsonObject(JSONObject jsonObject) throws Exception{
		this.jsonObject = jsonObject;
		Field[] fields = this.getClass().getDeclaredFields();
		@SuppressWarnings("unchecked")
		Iterator<String> it = jsonObject.keys();
		List<String> keys =new ArrayList<String>();
		while(it.hasNext()){
			keys.add(it.next());
		}
		for(int i=0;i<fields.length;i++){
			if(!fields[i].getName().equals("jsonObject") && keys.contains(fields[i].getName())){
				fields[i].setAccessible(true);
				if(fields[i].getType().toString().equals("class java.lang.Integer")){
					fields[i].set(this, jsonObject.getInt(fields[i].getName()));
				} else if(fields[i].getType().toString().equals("class java.lang.Long")){
					fields[i].set(this, jsonObject.getLong(fields[i].getName()));
				} else {
					fields[i].set(this, jsonObject.get(fields[i].getName()));
				}
			}
		}
	}
}
