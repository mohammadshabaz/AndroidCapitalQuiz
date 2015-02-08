package com.alamaanah.capitalquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//opening transition animations
	    overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
	    
		setContentView(R.layout.activity_home);
		DatabaseHandler db = new DatabaseHandler(this);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(!preferences.getBoolean("isDbSetup", false)){
			db.clearDB();
			preferences.edit().putBoolean("isDbSetup", true).commit();
		}
		Button b1 = (Button) findViewById(R.id.button1);
        Button b2 = (Button) findViewById(R.id.button2);
        Button b3 = (Button) findViewById(R.id.button3);
        
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ObelixPro.ttf");
        b1.setTypeface(font);
        b2.setTypeface(font);
        b3.setTypeface(font);
        
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			case R.id.button1:
				Intent intent = new Intent(HomeActivity.this, TypesActivity.class);
				startActivity(intent);
				break;
			case R.id.button2:
				final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
				try {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
				}
			case R.id.button3:
				this.finish();
				break;
		}
	}
	@Override
	  protected void onPause()
	  {
	    super.onPause();
	    //closing transition animations
	    overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
	  }

	@Override
	public void onBackPressed(){
		
		//Open Dialog Box
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
		
			// set title
		//alertDialogBuilder.setTitle("Quit Quiz?");
 
			// set dialog message
		alertDialogBuilder
			.setMessage("Are you sure you want to quit?")
			.setCancelable(true);
		
		alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int id) {
				HomeActivity.this.finish();
			}
		  });
		alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int id) {
				dialog.dismiss();
			}
		  });
		alertDialogBuilder.create().show();
		return;
	    
	}

}
