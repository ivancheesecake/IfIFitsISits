/*
 * http://www.wikihow.com/Execute-HTTP-POST-Requests-in-Android
 * */

package com.cheesecake.ififitsisits;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;


public class HttpPostHelper {

	private String url;
	
	public HttpPostHelper(String url){
		
		this.url = url;
	}
	
	public boolean post(ArrayList<NameValuePair> pairs){
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(this.url);
		String response;
		try{
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
		}catch(UnsupportedEncodingException u){
			return false;
			}
		
		try{
			response = client.execute(post, new BasicResponseHandler());
			Log.d("response",response);
		}catch(ClientProtocolException e){
			
			Log.e("ClientProtocolException","FAIL!");
			return false;
		}catch(IOException e){
			Log.e("IOException","FAIL!");
			return false;
		}
		
		if(response.equals("ERR"))
			return false;
		
		return true;
	}
	
public String post_string(ArrayList<NameValuePair> pairs){
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(this.url);
		String response;
		try{
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
		}catch(UnsupportedEncodingException u){
			return "ERR";
			}
		
		try{
			response = client.execute(post, new BasicResponseHandler());
			Log.d("response",response);
		}catch(ClientProtocolException e){
			
			Log.e("ClientProtocolException","FAIL!");
			return "ERR";
		}catch(IOException e){
			Log.e("IOException","FAIL!");
			return "ERR";
		}
		
		return response;
	}
	
	
}
