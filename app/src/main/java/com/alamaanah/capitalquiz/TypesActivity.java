package com.alamaanah.capitalquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TypesActivity extends Activity implements OnClickListener{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //opening transition animations
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
      
        setContentView(R.layout.activity_types);
        
        Button b1 = (Button) findViewById(R.id.button1);
        Button b2 = (Button) findViewById(R.id.button2);
        //Button b3 = (Button) findViewById(R.id.button3);
        
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        //b3.setOnClickListener(this);
        
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ObelixPro.ttf");
        b1.setTypeface(font);
        b2.setTypeface(font);
        //b3.setTypeface(font);
        
    }
	@Override
	  protected void onPause()
	  {
	    super.onPause();
	    //closing transition animations
	    overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
	  }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(TypesActivity.this, LevelsActivity.class);
		Button b = (Button) findViewById(v.getId());
		intent.putExtra("type", b.getText());
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.types, menu);
		return true;
	}

}
