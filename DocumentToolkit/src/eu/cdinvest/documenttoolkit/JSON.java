package eu.cdinvest.documenttoolkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;


public class JSON {
	
	JSONObject json = null;
	
	// Constructors
	// ------------
	public JSON() {
		this.json = new JSONObject();
	}
	
	public JSON(String jsondata) {
		try {
		this.json = new JSONObject(jsondata);
		} catch (JSONException e) {
			this.json = new JSONObject();
		}
	}
	
	public JSON(File jsonfile) {
		try {
		StringBuilder jsontext = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(jsonfile));
		String s;
		while ((s = br.readLine()) != null) jsontext.append(s);
		this.json = new JSONObject(jsontext.toString());
		br.close();
		} catch (IOException e) {
			this.json = new JSONObject();
		}
	}
	
	public JSON(JSONObject jsonobject) {
		this.json = jsonobject;
	}
	
	// Getters and setters
	// -------------------
	public void setInt(String key, int value) {
		this.json.put(key, value);
	}
	
	public int getInt(String key) {
		int value;
		
		try {
			value = this.json.getInt(key);	
		} catch (JSONException e) {
			value = 0;
		}
				
		return value;
		
	}

	public void setDouble(String key, Double value) {
		this.json.put(key, value);
	}
	
	public double getDouble(String key) {
		double value;
		
		try {
			value = this.json.getDouble(key);	
		} catch (JSONException e) {
			value = 0;
		}
				
		return value; 	
	}
	
	public void setFloat(String key, Float value) {
		this.json.put(key, value);
	}
	
	public double getFloat(String key) {
		float value;
		
		try {
			value = this.json.getFloat(key);	
		} catch (JSONException e) {
			value = 0;
		}
				
		return value; 	
	}
	
	public void setString(String key, String value) {
		this.json.put(key, value);
	}
	
	public String getString(String key) {
		String value;
		
		try {
			value = this.json.getString(key);	
		} catch (JSONException e) {
			value = null;
		}
				
		return value;
	}
	
	public void setBoolean(String key, boolean value) {
		this.json.put(key, value);
	}
	
	public boolean getBoolean(String key) {
		boolean value;
		
		try {
			value = this.json.getBoolean(key);
		} catch (JSONException e) {
			value = false;
		}
				
		return value;
		
	}
	
	// Methods
	// -------
	public String toString() {
		return this.json.toString();
	}
	
	public String[] getNames() {
		return JSONObject.getNames(this.json);
	}
	
	public JSONObject getJSONObject() {
		return json;
	}
	
	public JSONObject getJSONArray() {
		return json;
	}
	
//	public void getType(String key) {
//		Object value = this.json.get(key);
//		System.out.println("Type = " + value.getClass());
//	}

}
