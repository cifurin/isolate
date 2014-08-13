package com.example.isolate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button newGame = (Button) findViewById(R.id.bNew);
		Button level = (Button) findViewById(R.id.bLevel);
		Button exit = (Button) findViewById(R.id.bExit);
		newGame.setOnClickListener(this);
		level.setOnClickListener(this);
		exit.setOnClickListener(this);
		//ImageView image = (ImageView) findViewById(R.id.imageView1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Log.d("Main Activity", "Settings");
			Intent settingsIntent = new Intent(this, UserSettingActivity.class);
			startActivity(settingsIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View view) {
		// check which button was clicked with its id
		switch (view.getId()) {
		case R.id.bNew:
			Toast.makeText(this, "Start Solving !!", Toast.LENGTH_LONG) .show();
			Intent i = new Intent(MainActivity.this, GameActivity.class);
			startActivity(i);
			break;
		case R.id.bLevel:
			startActivity(new Intent(this, UserSettingActivity.class));
			break;
		case R.id.bInstructions:
			break;
		case R.id.bExit:
			finish();
			break;
		}
	}

}
