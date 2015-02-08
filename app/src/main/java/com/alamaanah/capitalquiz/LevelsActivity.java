	package com.alamaanah.capitalquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class LevelsActivity extends Activity {
	ListView levelsListView;
	DatabaseHandler dbHandler;
	String type;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//opening transition animations
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        
        setContentView(R.layout.activity_levels);
		dbHandler = new DatabaseHandler(this);
        type = getIntent().getStringExtra("type");
		
		
        LevelItemAdapter adapter = new LevelItemAdapter(this, 
                R.layout.listview_item_row, dbHandler.getLevels(type));
        
        
        levelsListView = (ListView)findViewById(R.id.levelsListView);
        levelsListView.setAdapter(adapter);
        
        levelsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				LevelItem item = (LevelItem) levelsListView.getItemAtPosition(arg2);
				if(!item.isLocked){
					Intent intent = new Intent(LevelsActivity.this, QuizActivity.class);
					intent.putExtra("type", type);
					intent.putExtra("level", item.level);
					startActivity(intent);
				}
				else{
					Toast.makeText(LevelsActivity.this, "You have to complete the previous levels to unlock this level",Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	  protected void onPause()
	  {
	    super.onPause();
	    //closing transition animations
	    overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.levels, menu);
		return true;
	}

}
