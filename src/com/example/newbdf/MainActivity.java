package com.example.newbdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity
{
	private EditText urlProduct;//To fetch string from EditText box for URL
	
	private Button search,best,back;	//For Search the product
	
	private static String url,url1;		//url-For original Link ,url1-For Product name
	
	private AlertDialog alertDialog;	//To show the best deal from the list to the user
	
	private String BestProduct,BestSource;	// found the best prodcut & best src
	
	private ProgressDialog progressDialog;	//exhibits the spinning bar to indicate processing...
	
	int max=999999999;						//Used to act as sentinel var
	
	int  k=0;								// acts as the page index from data weave					
		
	JSONArray data;							//data is array name from JSON Object
	
	ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();	//jsonlist is used to feed data as got from json object
	
	ListView lv;							//declare a  list view for displaying search-results 
	
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_main); //the layout file name here - > "activity_mail.xml"
		
	
		urlProduct=(EditText)findViewById(R.id.Product);	//define widgets Product->EditText
		search=(Button)findViewById(R.id.Search);			//
		best=(Button)findViewById(R.id.button1);
		back=(Button)findViewById(R.id.back);
		
        best.setEnabled(false); //prevent click - fire events as we don't have any search results to operate upon
        back.setEnabled(false); //prevent click - fire events

		//count.setVisibility(View.GONE);
		search.setOnClickListener(new View.OnClickListener() //
		{
			@Override
			public void onClick(View arg0)
			{
				
			progressDialog = new ProgressDialog(MainActivity.this, R.style.Theme_MyDialog); //apply the customized PD using Theme_MyDialog in style.xml
			progressDialog.setMessage("Loading...");	//sets the caption of the PD
			progressDialog.show();						//set enable to show the PD 
			Log.e("tag","outside");						//used to check the till now running status
				
				
				new Thread()
				{
					public void run() 
					{
						try
						{
							sleep(10000);
						}
						catch (Exception e) 
						{
							Log.e("tag", e.getMessage());
						}
				
					}
				}.start();

				url1 = urlProduct.getText().toString();	//extract string entered by user in editText(urlProduct) var and assingn to url1 
				String newUrl1="";						//newUrl1
				char[] charSeq=url1.toCharArray();
				for(int i=0;i<charSeq.length;i++)
				{
					if(charSeq[i]==' ')
							charSeq[i]='+';
					newUrl1+=charSeq[i]; //append all chars to newUrl1
				}
				url1=newUrl1; //url1 is the final search string 
				if(url1=="") //if no string enterd then throw alert
				{
					Toast.makeText(MainActivity.this, "Invalid Product...Enter a valid product name", Toast.LENGTH_SHORT).show();
					//finish();
					progressDialog.dismiss();
					return;
				}
                
				k=k+1; //increment the Page_Index_count
				
				if(k==2) //if 2 pages read then... 
				{
			        back.setEnabled(true);  // ...set enable to true for the back button
			        back.setText("<<Prev"); //set caption as <<prev
				}
				
				if(k>4) //if more than 4 pgs of dataweave are read then app crashes... so limit it to 4
				{
					Toast.makeText(MainActivity.this, "No matching products found", Toast.LENGTH_SHORT).show();
					progressDialog.dismiss(); //make the PD disappear
					return;
				}
				max=999999999;//always re-initialize max for the new page of result-list
				
				//URL to work with with prodname-> url1, page index->k and per_pg_limit->10
				url="http://api.dataweave.in/v1/price_intelligence/findProduct/?api_key=e674f53e13b536a07bf0adcb9cde0c67518b3036&product="+url1+"&page="+k+"&per_page=10";
				
				new JSONParse().execute(); //				
			}
		});
		best.setOnClickListener(new View.OnClickListener() 
		{			
			public void onClick(View arg0) 
			{
				alertDialog=new AlertDialog.Builder(MainActivity.this,R.style.Theme_MyDialog).create();
				alertDialog.setTitle("Best Deal in the List");
				
				alertDialog.setMessage("Product:- "+BestProduct+"\nPrice:- "+max+"\nSource:- "+BestSource);
				alertDialog.show();
				
			}
		});
		
		
		back.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg0) 
			{
				progressDialog = new ProgressDialog(MainActivity.this, R.style.Theme_MyDialog);
				progressDialog.setMessage("Loading...");
				progressDialog.show();
				max=999999999;
				k=k-1;
				url="http://api.dataweave.in/v1/price_intelligence/findProduct/?api_key=e674f53e13b536a07bf0adcb9cde0c67518b3036&product="+url1+"&page="+k+"&per_page=10";
				new JSONParse().execute();	
			}
		});
		
	}
	private class JSONParse extends AsyncTask<String,Integer,JSONObject>
	{	
		protected JSONObject doInBackground(String... args)
		{
			HandleJson jParser=new HandleJson();
			JSONObject json = null;
			try 
			{
				json = jParser.getJSONFromUrl(url); //sends control to HandleJSON.java 
				
				try 
				{
					data=json.getJSONArray("data"); //fetch array "data" from json object to estimate the length reqd. to make list clear upto that length  					
					for(int i=0;i<data.length();i++)
						jsonlist.clear();	//to make the jsonlist clear to populate next page
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			return json; //gets returned to onPostExecute() 
		}
		
		protected void onPostExecute(JSONObject json)
		{
			progressDialog.dismiss(); // make the PD disappear now
			
			// checking status code 
			
			try 
			{
				//status code 200 indicates Success... anything apart from 200 indicates error
				if(json.getInt("status_code")!=200)// we fetch the status code from getInt()
				{
                    Toast.makeText(MainActivity.this, "Network Problem ! ", Toast.LENGTH_SHORT).show(); 
                    return; //makes us remain in the app	                                        
				}
			}
			
			catch (JSONException e1) 
			{			
				e1.printStackTrace();
			}
		
			try
			{	            
	            data = json.getJSONArray("data"); //fetch data here for displaying purpose
	            
	            for(int i = 0; i < data.length(); i++) //
	            {
		            JSONObject c = data.getJSONObject(i); //pick obj_data from ith_index of data[] array
		            
		            // Storing  JSON item in a Variable
		            String SOURCE = c.getString("source"); //assigning value from keys e.g. SOURCE(value)<-source(key)
		            String PRICE = c.getString("mrp");
		            String TITLE=c.getString("title");
		            String URL=c.getString("url");
		            
		            HashMap<String, String> map = new HashMap<String, String>(); 
		            
		            map.put("source",SOURCE);
		            map.put("mrp","Rs. "+PRICE);
		            map.put("title",TITLE);
		            map.put("url", URL);
		            
		            jsonlist.add(map);// adds(appends for every new object) the map containging all four_items of a single object_index into the jsonlist
		            
		            //finding the min. price, and its corresp src and value
		            int price=Integer.parseInt(PRICE);
		            if(price!=-1 && price<max)
		            {
		            	max=price;
		            	BestProduct=TITLE;
		            	BestSource=SOURCE;		            	
		            }
	            }   
	            
		        lv=(ListView)findViewById(R.id.list);
/*
		Adapter banakar List.xml ke through jsonlist ko populate kr rhe hai jisme key(source) ki value sourceLayout se uthkr aa rhi hai..and so on.......*/		          
ListAdapter adapter = new SimpleAdapter(MainActivity.this, jsonlist,R.layout.list,new String[] { "source","title","mrp","url" }, new int[] {  R.id.SourceLayout,R.id.TitleLayout,R.id.PriceLayout,R.id.UrlLayout});

		            
		        lv.setAdapter(adapter); //puts the adapter in the listview
		            	  
		            best.setEnabled(true);//since we have the list now populated... so we enable the "best" button to find the best deal
		        	search.setText("Next>>");
		        	
		            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() //to open a particular deal present in the listview 
		            {		            
		                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
		                    {
		                        Toast.makeText(MainActivity.this, "You Clicked at "+jsonlist.get(+position).get("title"), Toast.LENGTH_SHORT).show();
		                        
		                        String Title=jsonlist.get(+position).get("title");//position tells the index of particular item in the list
		                        String Source=jsonlist.get(+position).get("source");
		                        String Price=jsonlist.get(+position).get("mrp");
		                        String URL=jsonlist.get(+position).get("url");
		                        
		                        Intent intent=new Intent(MainActivity.this,NewIntent.class); //created a new intent for displaying details about a particular deal
		                        
		                        intent.putExtra("Product", Title);
		                        intent.putExtra("Source", Source);
		                        intent.putExtra("Price", Price);
		                        intent.putExtra("Url", URL);
		                        
		                        startActivity(intent); //launch the new intent 
		                    	//int pos=jsonlist.get(position);
		                    }
		            });
	            
	        }
			
			catch (JSONException e) 
			{
	          e.printStackTrace();
	        }
			
	       }
			
		}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) 
	{
    	switch(item.getItemId())
    	{
    	case R.id.share:
    		Intent shareIntent = new Intent(Intent.ACTION_SEND);
    		shareIntent.setType("text/plain");
    		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
    		shareIntent.putExtra(Intent.EXTRA_TEXT, "body here");
    		startActivity(Intent.createChooser(shareIntent, "Share Via"));
    		break;
    	default:
    		break;
    	
    	}
    	return true;
    }


//Read more: http://www.javaexperience.com/how-to-add-share-button-in-android-title-bar/#ixzz3XdXHq456


}