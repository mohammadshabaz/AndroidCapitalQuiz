package com.alamaanah.capitalquiz;

import java.util.Arrays;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class QuizActivity extends Activity implements OnClickListener {

	//intent variables
	String type;
	int level;
	String TAG = "chartboost";
	//Ads Code
	AdView adView;
	private Chartboost cb;
	
	//score related variables
	final Handler handler = new Handler();
	int timerDelayInMillis = 1000;
	int penalty, penaltyPerMistake = 3;
	float score;
	int timeLimit;
	
	//UI Variables
	TextView txtScore;
	//TextView txtPenalty;
	TextView txtQuestion;
	TextView txtLevel, txtType;
	Button op1, op2, op3, op4;
	ProgressBar scoreBar;
	int currentColor = 0;
	private ChartboostDelegate chartBoostDelegate = new ChartboostDelegate() {

		/*
		 * Chartboost delegate methods
		 * 
		 * Implement the delegate methods below to finely control Chartboost's behavior in your app
		 * 
		 * Minimum recommended: shouldDisplayInterstitial()
		 */
	
		
		/* 
		 * shouldDisplayInterstitial(String location)
		 *
		 * This is used to control when an interstitial should or should not be displayed
		 * If you should not display an interstitial, return false
		 *
		 * For example: during gameplay, return false.
		 *
		 * Is fired on:
		 * - showInterstitial()
		 * - Interstitial is loaded & ready to display
		 */
		@Override
		public boolean shouldDisplayInterstitial(String location) {
			Log.i(TAG, "SHOULD DISPLAY INTERSTITIAL '"+location+ "'?");
			return true;
		}
		
		/*
		 * shouldRequestInterstitial(String location)
		 * 
		 * This is used to control when an interstitial should or should not be requested
		 * If you should not request an interstitial from the server, return false
		 *
		 * For example: user should not see interstitials for some reason, return false.
		 *
		 * Is fired on:
		 * - cacheInterstitial()
		 * - showInterstitial() if no interstitial is cached
		 * 
		 * Notes: 
		 * - We do not recommend excluding purchasers with this delegate method
		 * - Instead, use an exclusion list on your campaign so you can control it on the fly
		 */
		@Override
		public boolean shouldRequestInterstitial(String location) {
			Log.i(TAG, "SHOULD REQUEST INSTERSTITIAL '"+location+ "'?");
			return true;
		}
		
		/*
		 * didCacheInterstitial(String location)
		 * 
		 * Passes in the location name that has successfully been cached
		 * 
		 * Is fired on:
		 * - cacheInterstitial() success
		 * - All assets are loaded
		 * 
		 * Notes:
		 * - Similar to this is: cb.hasCachedInterstitial(String location) 
		 * Which will return true if a cached interstitial exists for that location
		 */
		@Override
		public void didCacheInterstitial(String location) {
			Log.i(TAG, "INTERSTITIAL '"+location+"' CACHED");
		}

		/*
		 * didFailToLoadInterstitial(String location)
		 * 
		 * This is called when an interstitial has failed to load for any reason
		 * 
		 * Is fired on:
		 * - cacheInterstitial() failure
		 * - showInterstitial() failure if no interstitial was cached
		 * 
		 * Possible reasons:
		 * - No network connection
		 * - No publishing campaign matches for this user (go make a new one in the dashboard)
		 */
		@Override
		public void didFailToLoadInterstitial(String location) {
		    // Show a house ad or do something else when a chartboost interstitial fails to load

			Log.i(TAG, "INTERSTITIAL '"+location+"' REQUEST FAILED");
			//Toast.makeText(QuizActivity.this, "Interstitial '"+location+"' Load Failed",
				//	Toast.LENGTH_SHORT).show();
			endOfLevel();
		}

		/*
		 * didDismissInterstitial(String location)
		 *
		 * This is called when an interstitial is dismissed
		 *
		 * Is fired on:
		 * - Interstitial click
		 * - Interstitial close
		 *
		 * #Pro Tip: Use the delegate method below to immediately re-cache interstitials
		 */
		@Override
		public void didDismissInterstitial(String location) {
			
			// Immediately re-caches an interstitial
			cb.cacheInterstitial(location);
			
			Log.i(TAG, "INTERSTITIAL '"+location+"' DISMISSED");
			//Toast.makeText(QuizActivity.this, "Dismissed Interstitial '"+location+"'",
				//	Toast.LENGTH_SHORT).show();
			endOfLevel();
		}

		/*
		 * didCloseInterstitial(String location)
		 *
		 * This is called when an interstitial is closed
		 *
		 * Is fired on:
		 * - Interstitial close
		 */
		@Override
		public void didCloseInterstitial(String location) {
			Log.i(TAG, "INSTERSTITIAL '"+location+"' CLOSED");
			//Toast.makeText(QuizActivity.this, "Closed Interstitial '"+location+"'",
				//	Toast.LENGTH_SHORT).show();
		}

		/*
		 * didClickInterstitial(String location)
		 *
		 * This is called when an interstitial is clicked
		 *
		 * Is fired on:
		 * - Interstitial click
		 */
		@Override
		public void didClickInterstitial(String location) {
			Log.i(TAG, "DID CLICK INTERSTITIAL '"+location+"'");
			//Toast.makeText(QuizActivity.this, "Clicked Interstitial '"+location+"'",
				//	Toast.LENGTH_SHORT).show();
		}

		/*
		 * didShowInterstitial(String location)
		 *
		 * This is called when an interstitial has been successfully shown
		 *
		 * Is fired on:
		 * - showInterstitial() success
		 */
		@Override
		public void didShowInterstitial(String location) {
			Log.i(TAG, "INTERSTITIAL '" + location + "' SHOWN");
		}
		
		/*
		 * didFailToLoadURL(String location)
		 * 
		 * This is called when a url after a click has failed to load for any reason
		 *
		 * Is fired on:
		 * - Interstitial click
		 * - More-Apps click
		 * 
		 * Possible reasons:
		 * - No network connection
		 * - no valid activity to launch
		 * - unable to parse url
		 */
		@Override
		public void didFailToLoadUrl(String url) {
		    // Show a house ad or do something else when a chartboost interstitial fails to load

			Log.i(TAG, "URL '"+url+"' REQUEST FAILED");
			//Toast.makeText(QuizActivity.this, "URL '"+url+"' Load Failed",
				//	Toast.LENGTH_SHORT).show();
		}

		/*
		 * More Apps delegate methods
		 */
		
		/*
		 * shouldDisplayLoadingViewForMoreApps()
		 *
		 * Return false to prevent the pretty More-Apps loading screen
		 *
		 * Is fired on:
		 * - showMoreApps()
		 */
		@Override
		public boolean shouldDisplayLoadingViewForMoreApps() {
			return true;
		}

		/*
		 * shouldRequestMoreApps()
		 * 
		 * Return false to prevent a More-Apps page request
		 *
		 * Is fired on:
		 * - cacheMoreApps()
		 * - showMoreApps() if no More-Apps page is cached
		 */
		@Override
		public boolean shouldRequestMoreApps() {

			return true;
		}

		/*
		 * shouldDisplayMoreApps()
		 * 
		 * Return false to prevent the More-Apps page from displaying
		 *
		 * Is fired on:
		 * - showMoreApps() 
		 * - More-Apps page is loaded & ready to display
		 */
		@Override
		public boolean shouldDisplayMoreApps() {
			Log.i(TAG, "SHOULD DISPLAY MORE APPS?");
			return true;
		}

		/*
		 * didFailToLoadMoreApps()
		 * 
		 * This is called when the More-Apps page has failed to load for any reason
		 * 
		 * Is fired on:
		 * - cacheMoreApps() failure
		 * - showMoreApps() failure if no More-Apps page was cached
		 * 
		 * Possible reasons:
		 * - No network connection
		 * - No publishing campaign matches for this user (go make a new one in the dashboard)
		 */
		@Override
		public void didFailToLoadMoreApps() {
			Log.i(TAG, "MORE APPS REQUEST FAILED");
			//Toast.makeText(QuizActivity.this, "More Apps Load Failed",
				//	Toast.LENGTH_SHORT).show();
		}

		/*
		 * didCacheMoreApps()
		 * 
		 * Is fired on:
		 * - cacheMoreApps() success
		 * - All assets are loaded
		 */
		@Override
		public void didCacheMoreApps() {
			Log.i(TAG, "MORE APPS CACHED");
		}

		/*
		 * didDismissMoreApps()
		 *
		 * This is called when the More-Apps page is dismissed
		 *
		 * Is fired on:
		 * - More-Apps click
		 * - More-Apps close
		 */
		@Override
		public void didDismissMoreApps() {
			Log.i(TAG, "MORE APPS DISMISSED");
			//Toast.makeText(QuizActivity.this, "Dismissed More Apps",
				//	Toast.LENGTH_SHORT).show();
		}

		/*
		 * didCloseMoreApps()
		 *
		 * This is called when the More-Apps page is closed
		 *
		 * Is fired on:
		 * - More-Apps close
		 */
		@Override
		public void didCloseMoreApps() {
			Log.i(TAG, "MORE APPS CLOSED");
			//Toast.makeText(QuizActivity.this, "Closed More Apps",
				//	Toast.LENGTH_SHORT).show();
		}

		/*
		 * didClickMoreApps()
		 *
		 * This is called when the More-Apps page is clicked
		 *
		 * Is fired on:
		 * - More-Apps click
		 */
		@Override
		public void didClickMoreApps() {
			Log.i(TAG, "MORE APPS CLICKED");
			//Toast.makeText(QuizActivity.this, "Clicked More Apps",
				//	Toast.LENGTH_SHORT).show();
		}

		/*
		 * didShowMoreApps()
		 *
		 * This is called when the More-Apps page has been successfully shown
		 *
		 * Is fired on:
		 * - showMoreApps() success
		 */
		@Override
		public void didShowMoreApps() {
			Log.i(TAG, "MORE APPS SHOWED");
		}

		/*
		 * shouldRequestInterstitialsInFirstSession()
		 *
		 * Return false if the user should not request interstitials until the 2nd startSession()
		 * 
		 */
		@Override
		public boolean shouldRequestInterstitialsInFirstSession() {
			return true;
		}
	};
	
	
	//questionRelated
	int currentQuestion;
	int currentAnswer = 1;
	boolean isComplete = false;
	Question qList[];
	
	// Database
	DatabaseHandler dbHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//opening transition animations
	    overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
	    setContentView(R.layout.activity_quiz);
		
		level = getIntent().getIntExtra("level", 1);
		type = getIntent().getStringExtra("type");
		timeLimit = Integer.parseInt(getResources().getString(R.string.time_limit));
		
		dbHandler = new DatabaseHandler(this);
		qList = dbHandler.getQuestions(type, level);
		
		getUiElements();
		
		setupAds();
		
		score = 0;
		
		showInstruction();
		
		currentQuestion = 0;
		
		changeQuestion();
		
		
		
		
	}

	private void setupAds() {
		//Ads Implementation
		// Configure Chartboost
		this.cb = Chartboost.sharedChartboost();
		String appId = getResources().getString(R.string.chartboost_app_id);
		String appSignature = getResources().getString(R.string.chartboost_app_signature);
		this.cb.onCreate(this, appId, appSignature, chartBoostDelegate);
		
		adView = new AdView(QuizActivity.this, AdSize.SMART_BANNER, getResources().getString(R.string.ad_unit_id));
	    LinearLayout layout = (LinearLayout)findViewById(R.id.adContainer);
	    layout.addView(adView);
	    adView.loadAd(new AdRequest());
	}

	/* Ads Code */
	@Override
	protected void onStart() {
	    super.onStart();

	    this.cb.onStart(this);

	    // Notify the beginning of a user session. Must not be dependent on user actions or any prior network requests.
	    this.cb.startSession();
	    
	    
	}   
	@Override
	protected void onStop() {
	    super.onStop();

	    this.cb.onStop(this);
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();

	    this.cb.onDestroy(this);
	}

	
	
	private void showInstruction() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(preferences.getBoolean("show_instructions", true)){
			//Open Dialog Box
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	 
			
				// set title
			alertDialogBuilder.setTitle("Instructions");
	 
				// set dialog message
			alertDialogBuilder
				.setMessage(Html.fromHtml("You must complete the quiz in less than <b>" + timeLimit + "</b> seconds to win this level.<br><br>" +
						"For every wrong answer, <b>"+penaltyPerMistake+ " seconds</b> will be added to your timer.<br /><br />" +
								"All the best!"))
				.setCancelable(true);
			
			alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
					startTimer();
					dialog.dismiss();
				}
			  });
			alertDialogBuilder.setNegativeButton("Do not show this again", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(QuizActivity.this);
				  SharedPreferences.Editor editor = preferences.edit();
				  editor.putBoolean("show_instructions", false);
				  editor.commit();
					dialog.dismiss();
					startTimer();
				}
			  });
			alertDialogBuilder.create().show();
		}
		else{
			startTimer();
		}
		
	}
	
	private void changeQuestion() {
		currentQuestion++;
		if(currentQuestion > qList.length){
			isComplete = true;
			showAdAndEndOfLevel();
		}
		else{
			//revert colors of buttons
			op1.setTextColor(Color.BLACK);
			op2.setTextColor(Color.BLACK);
			op3.setTextColor(Color.BLACK);
			op4.setTextColor(Color.BLACK);
			
			op1.setBackgroundResource(R.drawable.main_button);
			op2.setBackgroundResource(R.drawable.main_button);
			op3.setBackgroundResource(R.drawable.main_button);
			op4.setBackgroundResource(R.drawable.main_button);
			
			
			int questionIndex = currentQuestion - 1;
			txtQuestion.setText("Q"+ currentQuestion + ". " + qList[questionIndex].qText);
			
			String options[] = {
				qList[questionIndex].op1,
				qList[questionIndex].op2,
				qList[questionIndex].op3,
				qList[questionIndex].op4
			};
			
			currentAnswer = randomizeOptions(options, qList[questionIndex].answerIndex - 1);
			
			int i = 0;
			op1.setText(options[i++]);
			op2.setText(options[i++]);
			op3.setText(options[i++]);
			op4.setText(options[i++]);
			
			op1.setOnClickListener(this);
			op2.setOnClickListener(this);
			op3.setOnClickListener(this);
			op4.setOnClickListener(this);
		}
	}
	
	private void showAdAndEndOfLevel() {
		// TODO Auto-generated method stub
		// Show an interstitial
	    this.cb.showInterstitial(); 
	    //endOfLevel();
	}

	public int randomizeOptions(String options[], int answerIndex){
		String correctAnswer = options[answerIndex];
		shuffleArray(options);
		answerIndex = Arrays.asList(options).indexOf(correctAnswer);
		return answerIndex + 1; //because its 1 indexed
	}
	
	// Implementing Fisher–Yates shuffle
	  static void shuffleArray(String[] ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      String a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	
	@Override
	protected void onPause()
	{
		super.onPause();
	    //closing transition animations
	    overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
	}
	private class ImageGetter implements Html.ImageGetter {

	    public Drawable getDrawable(String source) {
	        int id;
	        
	    	
	    	id = R.drawable.logo;
	        Drawable d = getResources().getDrawable(id);
	        d.setBounds(0,0,100,70);
	        return d;
	    }
	};
	private void endOfLevel() {
		

		float finalScore = score;
		//float realTime = score - penalty;
		int timeLimit = Integer.parseInt(getResources().getString(R.string.time_limit));
		int scoreResult = dbHandler.addScore(type, level, finalScore);
		
		//show score
		String message;
		boolean goToNextLevel = true;
		if(finalScore > timeLimit){
			message = "You failed to complete the quiz in less than " + timeLimit + " seconds";
			goToNextLevel = false;
		}
		else if(scoreResult == 3 || scoreResult == 2){ //scoreResult = Unlock + beat or just new insert + Unlock
			//inserted a new record < timeLimit
			message = "You unlocked the next level.";
		}
		else{ //Beat old unlocked record
			message = "You beat your previous record!";
		}
		
		
		String finalScoreMessage = "Your final score is <b>" + Math.round(finalScore) + "</b>";
		if(penalty > 0)
			finalScoreMessage += " after a penalty of <b>" + penalty + "s</b>";
		
		
		
		//check if nextLevel exists
		int maxLevel = dbHandler.getMaxLevelForType(type);
		boolean nextLevelExists = true;
		if(level == maxLevel && goToNextLevel){
			nextLevelExists = false;
			message = "You've completed all the levels.";
		}
		
		String color = "red";
		if(goToNextLevel)
			color = "green";
		
		message = "<font color='" + color + "'>" + message + "</div>";
		
		//Open Dialog Box
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
		
			// set title
			alertDialogBuilder.setTitle("End Of Quiz");
			
			if(goToNextLevel)
				alertDialogBuilder.setIcon(R.drawable.cleared);
			else
				alertDialogBuilder.setIcon(android.R.drawable.ic_delete);
			
			// set dialog message
			alertDialogBuilder
				.setMessage(Html.fromHtml(finalScoreMessage + "<br /><br />" + message))
				.setCancelable(false);
			
			if(goToNextLevel && nextLevelExists){	
				alertDialogBuilder.setPositiveButton("Go To Next Level",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						Intent intent = new Intent(QuizActivity.this, QuizActivity.class);
						intent.putExtra("type", type);
						intent.putExtra("level", level + 1);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						
					}
				  });
			}
			else if(goToNextLevel && !nextLevelExists){
				alertDialogBuilder.setPositiveButton("Choose another level",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						Intent intent = new Intent(QuizActivity.this, TypesActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						
					}
				  });

			}
			
			if(!goToNextLevel){
				alertDialogBuilder.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						Intent intent = new Intent(QuizActivity.this, QuizActivity.class);
						intent.putExtra("type", type);
						intent.putExtra("level", level);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						
					}
				  });
				
			  alertDialogBuilder.setNegativeButton("Choose another level",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
						Intent intent = new Intent(QuizActivity.this, LevelsActivity.class);
						intent.putExtra("type", type);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});
			  
			}
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
				
				
		
	}

	public void getUiElements(){
		txtScore = (TextView) findViewById(R.id.txtScore);
		//txtPenalty = (TextView) findViewById(R.id.txtPenalty);
		txtQuestion = (TextView) findViewById(R.id.txtQuestion);
		txtLevel = (TextView) findViewById(R.id.tvLevel);
		txtType = (TextView) findViewById(R.id.tvType);
		scoreBar = (ProgressBar) findViewById(R.id.scoreBar);
		
		
		
		op1 = (Button) findViewById(R.id.op1);
		op2 = (Button) findViewById(R.id.op2);
		op3 = (Button) findViewById(R.id.op3);
		op4 = (Button) findViewById(R.id.op4);
		
		//Defaults
		//txtPenalty.setText("Penalty: 0s");
		txtLevel.setText("LEVEL " + level);
		txtType.setText(type);
		scoreBar.setMax(Integer.parseInt(getResources().getString(R.string.time_limit)));
		
		Typeface fontObelix = Typeface.createFromAsset(getAssets(), "fonts/ObelixPro.ttf");
		txtLevel.setTypeface(fontObelix);
		txtType.setTypeface(fontObelix);
		txtScore.setTypeface(fontObelix);
		
		Typeface fontGabriela = Typeface.createFromAsset(getAssets(), "fonts/Exo.ttf");
		txtQuestion.setTypeface(fontGabriela);
		
		Typeface fontAcme = Typeface.createFromAsset(getAssets(), "fonts/Exo.ttf");
		
		
		op1.setTypeface(fontAcme);
		op2.setTypeface(fontAcme);
		op3.setTypeface(fontAcme);
		op4.setTypeface(fontAcme);
		
	}
	
	@Override
	public void onBackPressed(){
		// If an interstitial is on screen, close it. Otherwise continue as normal.
	    if (this.cb.onBackPressed())
	        return;
	    else{
		//Open Dialog Box
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
		
			// set title
		//alertDialogBuilder.setTitle("Quit Quiz?");
 
			// set dialog message
		alertDialogBuilder
			.setMessage("Are you sure you want to quit this level?")
			.setCancelable(true);
		
		alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int id) {
				Intent intent = new Intent(QuizActivity.this, LevelsActivity.class);
				intent.putExtra("type", type);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
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
	
	/** Play the sound using android.media.MediaPlayer */
	public void playSound(int soundID){		
	 	MediaPlayer mp = MediaPlayer.create(QuizActivity.this, soundID); 
	 	mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
	 	mp.start();
	}
	
	public void startTimer(){
		handler.postDelayed(new Runnable() {
			  @Override
			  public void run() {
				 if(!isComplete){
					 score += timerDelayInMillis/1000.0f;
					 int percentage = Math.round(score/timeLimit * 100);
					 txtScore.setText("Time: " + Math.round(score));
					 scoreBar.setProgress(Math.round(score));
					  
					 if(currentColor == 0 && percentage > 40){
						 currentColor++;
						 scoreBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_orange));
					 }
					 else if(currentColor == 1 && percentage > 80){
							 currentColor++;
							 scoreBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_red));
					 }
					 handler.postDelayed(this, timerDelayInMillis);
				 }
		  }
		}, timerDelayInMillis);
	}

	@Override
	public void onClick(View v) {
		int selectedOption = 1;
		Button b = (Button) findViewById(v.getId());
		switch(v.getId()){
			case R.id.op1: selectedOption = 1; break;
			case R.id.op2: selectedOption = 2; break;
			case R.id.op3: selectedOption = 3; break;
			case R.id.op4: selectedOption = 4; break;
		}
		if(selectedOption != currentAnswer){
			playSound(R.raw.error);
			b.setTextColor(Color.WHITE);
			b.setBackgroundResource(R.drawable.button_red);
			penalty += penaltyPerMistake;
			score += penaltyPerMistake;
			//txtPenalty.setText("Penalty: " + penalty + "s");
		}
		else{
			playSound(R.raw.correct);
			b.setBackgroundResource(R.drawable.button_green);
			handler.postDelayed(new Runnable() {
				  @Override
				  public void run() {
					 changeQuestion();
			  }
			}, 500);
		}
		
	}
	
	
}
