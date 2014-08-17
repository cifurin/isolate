package com.example.isolate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class InstructionsActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_instructions);
	    Log.d("ISOLATE", "Layout??");
	}

}
