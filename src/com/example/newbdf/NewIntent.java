package com.example.newbdf;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewIntent extends Activity
{
	String Source,Price,Product,Url;
	TextView txtSource,txtPrice,txtProduct,txtUrl;
	Button bt1;
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_new);
		
		//
		txtProduct=(TextView)findViewById(R.id.textView1);
		txtSource=(TextView)findViewById(R.id.textView2);
		txtPrice=(TextView)findViewById(R.id.textView3);
		txtUrl=(TextView)findViewById(R.id.textView4);
		
		bt1=(Button)findViewById(R.id.button1);
		
		
		Bundle it=getIntent().getExtras();
		
		Source=it.getString("Source");
		Price=it.getString("Price");
		Product=it.getString("Product");
		Url=it.getString("Url");
		
		txtProduct.setText("Product: "+Product);
		txtPrice.setText(Price);
		txtSource.setText("Source:  "+Source);
		txtUrl.setText(Url);
		
		bt1.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg0) 
			{
				
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(Url));
					startActivity(intent);
				
			}
		});
		
	}
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

