package com.example.newbdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
public class HandleJson 
{
	private Context context;
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json="";
	public JSONObject getJSONFromUrl(String url) throws IOException
	{
		try
		{
			//code for connection estd.
		    DefaultHttpClient httpClient = new DefaultHttpClient(); //httpClient OPENS connection 
			
		    HttpPost httpPost = new HttpPost(url); //embed the url into httpPost object
		    
		    HttpResponse httpResponse = httpClient.execute(httpPost);	//the result of search query (after embedding the URL) comes here
		    
		    HttpEntity httpEntity = httpResponse.getEntity();	//getEntity() holds the entire result from httpResponse to httpEntity object 
		    
		    is = httpEntity.getContent(); //all contents are converted to IS after retrieving them from httpEntity's getContent() 			
		}
		catch (UnsupportedEncodingException e)	//Thrown when a program asks for a particular character converter that is unavailable. 
		{
		      e.printStackTrace(); // useful tool for diagnosing an Exception. It tells you what happened and where in the code this happened.
		}
		catch (ClientProtocolException e) //Signals an error in the HTTP protocol
		{
		      e.printStackTrace();
		} 
		catch (IOException e) 
		{
		      e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is)); //Bufferedreader is used to read from IS
		StringBuilder sb = new StringBuilder();//to convert IS into string form --> using "A modifiable sequence of characters for use in creating strings."
		String line = null;	
		
		while ((line = reader.readLine()) != null) 
		{
			sb.append(line + "n");
		}
		is.close();
		
		json = sb.toString(); //convert sb(string builder) into string and assign it to json string 
		
		try
		{
			jObj=new JSONObject(json); //convert json (string) into jobj(a JSONObject) 
		}
		catch(JSONException e	)
		{}
		
		return jObj; //returns to main_activity.java --> doInbackg()
	}
}
