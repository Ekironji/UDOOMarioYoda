/*
 * Created by ekirei
 * UDOO Team
 */

package org.udoo.udoodroidcondemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.udoo.udoodroidcondemo.sounds.Effect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import me.palazzetti.adktoolkit.AdkManager;

public class MainActivity extends Activity {

	private final String TAG = "UDOOMario";
	private final String PREFS_NAME = "MarioPrefs";
	
	// ADK
	private AdkManager mAdkManager;
	
	// command to arduino	
	private final String FORWARD_SENDSTRING = "0";
	private final String BACK_SENDSTRING 	= "1";
	private final String RIGHT_SENDSTRING 	= "2";
	private final String LEFT_SENDSTRING 	= "3";
	private final String GOODBOY_SENDSTRING = "4";
	private final String BADBOY_SENDSTRING 	= "5";
	private final String CUTEBOY_SENDSTRING = "6";
	private final String HELLO_SENDSTRING 	= "7";
	private final String STARWARS_SENDSTRING = "8";
	
	// speech variables
	SpeechRecognizer mSpeechRecognizer;
	
	// speech key
	private ArrayList<String> goodboy_strings = new ArrayList<String>(Arrays.asList("good boy", 
			"well done", "very good", "good work", "good guy", "great"));
	private ArrayList<String> badboy_strings = new ArrayList<String>(Arrays.asList("vaffanculo", "bad boy",
			"fuck you", "bad guy", "cattivo", "brutto", "merda", "culo"));
	private ArrayList<String> cuteboy_strings = new ArrayList<String>(Arrays.asList("cute boy", "cute", 
			"so cute", "nice"));	
	private ArrayList<String> forward_strings = new ArrayList<String>(Arrays.asList("go forward", 
			"go straight on", "forward", "avanti", "diritto", "dritto"));
	private ArrayList<String> backward_strings = new ArrayList<String>(Arrays.asList("go backward", 
			"go back", "back", "indietro", "dietro"));
	private ArrayList<String> right_strings = new ArrayList<String>(Arrays.asList("turn right", 
			"look right", "right", "destra"));
	private ArrayList<String> left_strings = new ArrayList<String>(Arrays.asList("turn left", 
			"look left", "left", "sinistra"));
	private ArrayList<String> pizza_strings = new ArrayList<String>(Arrays.asList("pizza", "like pizza",
			"do you like pizza"));
	private ArrayList<String> hi_strings = new ArrayList<String>(Arrays.asList("ciao" , "hi", "saluti"));
	private ArrayList<String> name_strings = new ArrayList<String>(Arrays.asList("name", "your name",
			"who are you", "what's your name", "chi sei", "nome", "chiami"));
	private ArrayList<String> hello_strings = new ArrayList<String>(Arrays.asList("hello"));
	private ArrayList<String> comefrom_strings = new ArrayList<String>(Arrays.asList("come from", " are you from"));
	private ArrayList<String> goodbye_strings = new ArrayList<String>(Arrays.asList("goodbye")); 
	private ArrayList<String> starwars_strings = new ArrayList<String>(Arrays.asList("star wars", "fight",
			"combatti", "Jedi", "gedi", "jedi", "guerre stellari" ));
	private ArrayList<String> robinhood_strings = new ArrayList<String>(Arrays.asList("foreste illegali"));
	private ArrayList<String> fare_strings = new ArrayList<String>(Arrays.asList("puoi fare", "fare", "can you do", "do something"));
	private ArrayList<String> spada_strings = new ArrayList<String>(Arrays.asList("spada", "light", "saber", "sword", "taglia", "cut"));
	private ArrayList<String> allwords = new ArrayList<String>();
	
	// gui
	ImageView shape;
	TextView debug_tv;
	ImageButton voiceButton;
	ImageView faceImage;
	Animation animationFadeIn;	
	Animation animationFadeOut;

	TextToSpeech tts;
	
	Effect cuteEffect; 
	Effect goodEffect;
	Effect badEffect;
	Effect star_wars_theme;
	Effect lightsaber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAdkManager = new AdkManager((UsbManager) getSystemService(Context.USB_SERVICE));
		registerReceiver(mAdkManager.getUsbReceiver(), mAdkManager.getDetachedFilter());
		
		debug_tv = (TextView) findViewById(R.id.textView);
//	    debug_tv.setVisibility(View.GONE);
		shape = (ImageView) findViewById(R.id.face_imageView);
		faceImage = (ImageView) findViewById(R.id.expression_imageView);
		
	    voiceButton = (ImageButton) findViewById(R.id.voice_imageButton);
	    voiceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startVoiceRecognitionActivity();
			}
		});
/*

	    ImageButton helloButton = (ImageButton) findViewById(R.id.imageButtonHello);
	    helloButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendHello();
			}
		});

	    ImageButton moonWalkButton = (ImageButton) findViewById(R.id.ImageButtonMoonWalk);
	    moonWalkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				starWarsCase();
			}
		});
*/
	    allwords.addAll(hello_strings);
        allwords.addAll(hi_strings);
	    allwords.addAll(name_strings);
	    allwords.addAll(goodboy_strings);
        allwords.addAll(badboy_strings);
        allwords.addAll(cuteboy_strings);
        allwords.addAll(forward_strings);
        allwords.addAll(backward_strings);
        allwords.addAll(right_strings);
        allwords.addAll(left_strings);        
        allwords.addAll(starwars_strings);
        allwords.addAll(pizza_strings);
        allwords.addAll(comefrom_strings);
        allwords.addAll(goodbye_strings);
		allwords.addAll(robinhood_strings);
		allwords.addAll(fare_strings);
		allwords.addAll(spada_strings);
        
        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        
        cuteEffect = new Effect(this, R.raw.thanku); 
    	goodEffect = new Effect(this, R.raw.whohoo);
    	badEffect = new Effect(this, R.raw.no);
    	star_wars_theme = new Effect(this, R.raw.star_wars_theme_basso);
		lightsaber = new Effect(this, R.raw.lightsaber);

        faceImage.setImageResource(R.drawable.yoda_noespressione);
        
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        MyRecognitionListener listener = new MyRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
               
        // Get last stored values
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        tts = new TextToSpeech(getApplicationContext(), 
        	      new TextToSpeech.OnInitListener() {
	            @Override
	            public void onInit(int status) {
	            	if (status == TextToSpeech.SUCCESS) {
	       			 
//	                    int result = tts.setLanguage(Locale.ITALIAN);
						int result = tts.setLanguage(Locale.ENGLISH);
	                    tts.setPitch(0.5F);
						tts.setSpeechRate(0.9F);
	         
	                    if (result == TextToSpeech.LANG_MISSING_DATA) {
	                        Log.e("TTS", "Lang missing data");
	                            // missing data, install it
	                            Intent installIntent = new Intent();
	                            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	                            startActivity(installIntent);          
	                    } else {
//	                    	tts.speak("Che la forza sia con te!!", TextToSpeech.QUEUE_FLUSH, null);
							tts.speak("May the force be with u do!!", TextToSpeech.QUEUE_FLUSH, null);

						}
	         
	                } else {
	                    Log.e("TTS", "Initilization Failed!");          
	                }
	            }
            });
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    if(mSpeechRecognizer!=null){
	    	mSpeechRecognizer.stopListening();
	    	mSpeechRecognizer.cancel();
	    	mSpeechRecognizer.destroy();              

	    }
	    if(tts !=null){
	         tts.stop();
	         tts.shutdown();
	    }
	    mSpeechRecognizer = null;
	    tts = null;
	    mAdkManager.close();    
	}
	
    @Override
    protected void onStop() {
        super.onStop();
        /*SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
		editor.putString("lastId", mLastFetchedId);
		editor.commit();*/
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
		cuteEffect.release();
		goodEffect.release();
		badEffect.release();
		star_wars_theme.release();
        unregisterReceiver(mAdkManager.getUsbReceiver());
    }

	@Override
	protected void onResume() {
	    super.onResume();
	    if(mSpeechRecognizer == null){
        	mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            MyRecognitionListener listener = new MyRecognitionListener();
            mSpeechRecognizer.setRecognitionListener(listener);
        }
	    mAdkManager.open();
	}
		 
	private boolean searchCommands (ArrayList<String> results) {
        boolean found = false;
		for (String result : results) {
			String stringFounded = "";
			for (String string : allwords) {
				if (result.toLowerCase(Locale.ITALIAN).contains(string.toLowerCase(Locale.ITALIAN))) {
					stringFounded = string;
					found = true;

					if (hi_strings.contains(stringFounded)) sendHello();
					else if (hello_strings.contains(stringFounded)) sendHello();
					else if (name_strings.contains(stringFounded))
//						tts.speak("Yoda il mio nome è. Con iuduu fatto io sono.", TextToSpeech.QUEUE_FLUSH, null);
						tts.speak("Yoda my name is. Powered with u do i am.", TextToSpeech.QUEUE_FLUSH, null);
					else if (comefrom_strings.contains(stringFounded))
						tts.speak("I come from Siena in Italy", TextToSpeech.QUEUE_FLUSH, null);
					else if (goodboy_strings.contains(stringFounded)) sendGoodCase();
					else if (fare_strings.contains(stringFounded)) sendGoodCase();
					else if (badboy_strings.contains(stringFounded)) fuckyouCase();
					else if (cuteboy_strings.contains(stringFounded)) sendCuteCase();
					else if (forward_strings.contains(stringFounded))
						mAdkManager.writeSerial(FORWARD_SENDSTRING);
					else if (backward_strings.contains(stringFounded))
						mAdkManager.writeSerial(BACK_SENDSTRING);
					else if (right_strings.contains(stringFounded))
						mAdkManager.writeSerial(RIGHT_SENDSTRING);
					else if (left_strings.contains(stringFounded))
						mAdkManager.writeSerial(LEFT_SENDSTRING);
					else if (starwars_strings.contains(stringFounded)) starWarsCase();
					else if (pizza_strings.contains(stringFounded))
						tts.speak("Yes, I love pizza, but unfortunately I cannot eat it.", TextToSpeech.QUEUE_FLUSH, null);
					else if (goodbye_strings.contains(stringFounded)) sendGoodby();
					else if (robinhood_strings.contains(stringFounded))
						tts.speak("Maialino uccisamente sapete selvatici, in, un, a, del", TextToSpeech.QUEUE_FLUSH, null);
					else if (spada_strings.contains(stringFounded)) spadaCase();

					break;
				}
			}
			if (found) break;
		}
		return found;
    }

	private void spadaCase() {
		Log.i(TAG, "star_wars_theme case");
		setNewFace(R.drawable.yoda_triste);
//		tts.speak("In guardia Pàdwan", TextToSpeech.QUEUE_FLUSH, null);
		tts.speak("on guard Pàdwan", TextToSpeech.QUEUE_FLUSH, null);
		mAdkManager.writeSerial(CUTEBOY_SENDSTRING);
		lightsaber.play();
		returnToNormalState(6000);
	}

    private void starWarsCase() {
    	Log.i(TAG, "star_wars_theme case");
    	setNewFace(R.drawable.yoda_triste);
//		tts.speak("Potente tu sei diventato, Il Lato Oscuro percepisco in te", TextToSpeech.QUEUE_FLUSH, null);
		tts.speak("Powerful you have become, the dark side I sense in you", TextToSpeech.QUEUE_FLUSH, null);
		mAdkManager.writeSerial(STARWARS_SENDSTRING);
		star_wars_theme.play();
		lightsaber.play();
		returnToNormalState(6000);
    }

	private void fuckyouCase() {
		Log.i(TAG, "star_wars_theme case");
		setNewFace(R.drawable.yoda_triste);
//		tts.speak("Potente tu sei diventato, Il Lato Oscuro percepisco in te", TextToSpeech.QUEUE_FLUSH, null);
		tts.speak("Feel the force!", TextToSpeech.QUEUE_FLUSH, null);
		mAdkManager.writeSerial(STARWARS_SENDSTRING);
		star_wars_theme.play();
		lightsaber.play();
		returnToNormalState(6000);
	}
    
    private void sendHello() {
    	Log.i(TAG, "hello case");  
    	setNewFace(R.drawable.yoda_felice);
//    	tts.speak("Che la forza sia con te", TextToSpeech.QUEUE_FLUSH, null);
		tts.speak("May the force be with u do!!", TextToSpeech.QUEUE_FLUSH, null);
		mAdkManager.writeSerial(HELLO_SENDSTRING);
		returnToNormalState(5000);
    }
    
    private void sendGoodby() {
    	Log.i(TAG, "hello case");  
    	setNewFace(R.drawable.yoda_felice);
    	tts.speak("Goodbye and thank you", TextToSpeech.QUEUE_FLUSH, null);
		mAdkManager.writeSerial(HELLO_SENDSTRING);
		returnToNormalState(5000);
    }
    
    private void sendGoodCase() {
    	Log.i(TAG, "good case");  
    	setNewFace(R.drawable.yoda_felice);
//		tts.speak("Fare o non fare, non c'è provare!", TextToSpeech.QUEUE_FLUSH, null);
		tts.speak("Do or do not: there is no try!", TextToSpeech.QUEUE_FLUSH, null);
		mAdkManager.writeSerial(GOODBOY_SENDSTRING);
		returnToNormalState(5000);
    }
    
    private void sendBadCase() {
    	Log.i(TAG, "bad case");  
    	setNewFace(R.drawable.yoda_triste);
		badEffect.play();
		mAdkManager.writeSerial(BADBOY_SENDSTRING);	
		returnToNormalState(5000);
    }
    
    private void sendCuteCase() {
    	Log.i(TAG, "cute case");  		
		setNewFace(R.drawable.yoda_felice);
		cuteEffect.play();
		mAdkManager.writeSerial(CUTEBOY_SENDSTRING);		
		returnToNormalState(5000);
    }
    
    private void setNewFace(int resourceID){
    	faceImage.startAnimation(animationFadeOut);
		faceImage.setVisibility(View.INVISIBLE);
		faceImage.setImageResource(resourceID);
		faceImage.startAnimation(animationFadeIn);
		faceImage.setVisibility(View.VISIBLE);
    }
    
    private void returnToNormalState(int millis) {
    	new CountDownTimer(millis, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	Log.i(TAG, "returnToNormalState");
            	setNewFace(R.drawable.yoda_noespressione);
            }
         }.start();
    }
      
    @SuppressWarnings("unused")
	private void showToastMessage(String message){
    	  Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
	/**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
		intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
//		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it_IT");
    	
        mSpeechRecognizer.startListening(intent);        
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                //do nothing, just let it tick
            }

            public void onFinish() {
            	Log.i(TAG, "stop countdown");
            	mSpeechRecognizer.stopListening();
            	shape.setImageResource(R.drawable.yoda_shape);
            }
         }.start();
    }
	
	class MyRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
                Log.d("Speech", "onBeginningOfSpeech");
        }

		@Override
        public void onBufferReceived(byte[] buffer) {
                Log.d("Speech", "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
                Log.d("Speech", "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
                Log.d("Speech", "onError: " + error);
//                if (error == 7 ){
//                	tts.speak("Sorry, but I didn't understand", TextToSpeech.QUEUE_FLUSH, null);
//                }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
                Log.d("Speech", "onEvent");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
                Log.d("Speech", "onPartialResults");
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
                Log.d("Speech", "onReadyForSpeech");
                shape.setImageResource(R.drawable.yoda_shape_light);
        }
        

        @Override
        public void onResults(Bundle results) {
                Log.d("Speech", "onResults");
                ArrayList<String> resultsArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		        debug_tv.setText("Received text: " + resultsArray.get(0));
		        
		        if (!searchCommands(resultsArray)) {
//		        	tts.speak("Molto da apprendere ancora tu hai!", TextToSpeech.QUEUE_FLUSH, null);
					tts.speak(" Much to learn you still have my young padawan", TextToSpeech.QUEUE_FLUSH, null);
		        	//showToastMessage("Sentence is not recognized");
		        }
		        
                for (int i = 0; i < resultsArray.size();i++ ) {
                        Log.d("Speech", "result=" + resultsArray.get(i));           
                }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
//               Log.d("Speech", "onRmsChanged");
        }

	}

}
