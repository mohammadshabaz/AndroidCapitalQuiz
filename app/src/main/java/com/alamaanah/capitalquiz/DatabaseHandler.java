package com.alamaanah.capitalquiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class DatabaseHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "quiz";
    private static final String DATABASE_FILENAME = "init.sql";
    private static final String JSON_FILENAME = "questions.json";
 
    // Contacts table name
    private static final String TABLE_QUESTION = "question";
    private static final String TABLE_STATS = "stats";
    private Context contextLocal;
    SQLiteDatabase db;
 
    public DatabaseHandler(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	contextLocal = context.getApplicationContext();
    	db = this.getWritableDatabase();
        
    }
    public DatabaseHandler(Context context, boolean read) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	contextLocal = context.getApplicationContext();
    	db = this.getReadableDatabase();
        //onCreate(db);

    	//Log.d("DBCREATE", "onCreate");
    }
    public void clearDB()
    {
    	db.execSQL("drop table IF EXISTS "+TABLE_QUESTION);
    	db.execSQL("drop table IF EXISTS " + TABLE_STATS);
    	onCreate(db);
    }
    // Creating Tables
    
    @Override
    public void onCreate(SQLiteDatabase db) {
    	//TESTING CODE
    	//this.db = db;
    	Log.d("DBCREATE", "onCreate");
    	InputStream in;
    	BufferedReader reader;
    	String line;
    	try {
    		//read db file
			in = contextLocal.getAssets().open(DATABASE_FILENAME);
	        reader = new BufferedReader(new InputStreamReader(in));
	        line = reader.readLine();
	        while(line !=null )
	        {	
	        	db.execSQL(line);
	        	line = reader.readLine();
	        }
	        reader.close();
	        in.close();
	        
	        addEntriesFromJSON();
    	
	        
	        Log.d("db","Queries executed from file");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("DBError", e.toString());
		}
    	
    }
    
    public void addEntriesFromJSON() throws IOException, JSONException{
    	//read entries json file
        InputStream in = contextLocal.getAssets().open(JSON_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String jsonData = "";
        String line = reader.readLine();
        while(line !=null )
        {	
        	jsonData += line;
        	line = reader.readLine();
        }
        reader.close();
        in.close();
        
        JSONArray jsonArray = new JSONArray(jsonData);
        for(int i = 0; i < jsonArray.length(); i++){
        	JSONObject jsonObject = jsonArray.getJSONObject(i);
        	Question question = new Question(jsonObject.getInt("qId"),
        			jsonObject.getString("qText"),
        			jsonObject.getString("op1"),
        			jsonObject.getString("op2"),
        			jsonObject.getString("op3"),
        			jsonObject.getString("op4"),
        			jsonObject.getInt("answerIndex"),
        			jsonObject.getString("type"),
        			jsonObject.getInt("level")
        			);
        	int result = addQuestion(question);
        	if(result < 0){
        		Log.d("AddQuestion", "Error adding !");
        	}
        }
    }
	// Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
 
        // Create tables again
        onCreate(db);
    }
    
    public LevelItem[] getLevels(String type){
    	// Select All Query
        String selectQuery = "SELECT distinct question.level, stats.score from question left outer join stats " +
        		" on question.level = stats.level and question.type = stats.type" +
        		" where question.type = ?" ;
 
        Cursor cursor = db.rawQuery(selectQuery, new String[]{type});
        LevelItem levelList[] = new LevelItem[cursor.getCount()];
        boolean isLocked = false;
        int index = 0;
        if (cursor.moveToFirst()) {
            do {
            	LevelItem item = new LevelItem(cursor.getInt(0),
            			type,
            			cursor.getInt(1),
            			isLocked);
                levelList[index++] = item;
                
                if(!isLocked && (item.score == 0 || item.score > Integer.parseInt(contextLocal.getResources().getString(R.string.time_limit))))
                	isLocked = true;
                

                Log.d("Level", "Level " + item.level + " score " + item.score);
            } while (cursor.moveToNext());
        };
        cursor.close();
        
        return levelList;
    }
    
    public Question[] getQuestions(String type, int level) {
        // Select All Query
        String selectQuery = "SELECT * from "+ TABLE_QUESTION + " where type='" + type + "' and level = " + level + " ORDER BY RANDOM()" ;
 
        Cursor cursor = db.rawQuery(selectQuery, null);// new String[]{type, level + ""});
        Question qList[] = new Question[cursor.getCount()];
        int i = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	Question item = new Question(cursor.getInt(0),
            			cursor.getString(1),
            			cursor.getString(2),
            			cursor.getString(3),
            			cursor.getString(4),
            			cursor.getString(5),
            			cursor.getInt(6),
            			cursor.getString(7),
            			cursor.getInt(8)
				);
                qList[i++] = item;
                //noteList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        return qList;
    }
    
    public int addScore(String type, int level, float score){
    	// check if score exists
    	int timeLimit = Integer.parseInt(contextLocal.getResources().getString(R.string.time_limit));
    	String selectQuery = "Select score from stats where type = ? and level = ?";
    	Cursor cursor = db.rawQuery(selectQuery, new String[]{type, level + ""});
    	if(cursor.getCount() != 0){
    		//get score
    		cursor.moveToFirst();
    		float oldScore = cursor.getFloat(0);
    		if(score < oldScore){
    			String predicate = "type = ? and level = ?";
    			String predicateArgs[] = new String[]{ type, level + "" };
    			ContentValues newValues = new ContentValues();
    			newValues.put("score", String.format("%.1f", score));
    			db.update(TABLE_STATS, newValues, predicate, predicateArgs);
    			if(oldScore > timeLimit)
    				return 3; //UNLOCK + BEAT
    			else
    				return 1; //update
    		}
    		else
    			return 0; //no update
    	}
    	else{
    		//insert new record
    		ContentValues values = new ContentValues();
    		values.put("type", type);
    		values.put("level", level);
    		values.put("score", score);
    		db.insert(TABLE_STATS, null, values);
    		return 2; //new insert
    	}
    }
   
   public int getMaxLevelForType(String type){
	   	String selectQuery = "Select max(level) from question where type=?";
   		Cursor cursor = db.rawQuery(selectQuery, new String[]{type});
   		cursor.moveToFirst();
   		return cursor.getInt(0);
   }
   public int addQuestion(Question question)
    {
    	try
    	{
    		if(db == null){
    			//Toast.makeText(contextLocal, "", Toast.LENGTH_LONG).show();
    			return -1;
    		}
    		ContentValues values = new ContentValues();
    		values.put("qId", question.qId);
    		values.put("qText", question.qText);
    		values.put("op1", question.op1);
    		values.put("op2", question.op2);
    		values.put("op3", question.op3);
    		values.put("op4", question.op4);
    		values.put("answerIndex", question.answerIndex);
    		values.put("type", question.type);
    		values.put("level", question.level);
    	    int result = (int)db.insert(TABLE_QUESTION, null, values);
    		return result;
    	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		Log.e("DB ERROR", e.toString());
    		String error = e.toString();
    		//Toast.makeText(contextLocal, error, Toast.LENGTH_LONG).show();
    		return -1;
    	}
    }
 
	
	@Override 
	public void close(){
		db.close();
	}
	
	public void clearAllEntries() {
		db.delete(TABLE_QUESTION, null, null);
	}
   

}