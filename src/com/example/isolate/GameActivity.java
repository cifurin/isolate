package com.example.isolate;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements gameListener {
	private static final String TAG = "SettingsAcitivity";
	private static final int RESULT_SETTINGS = 1;
	private Thread timer, solver;
	private static ProgressDialog dialog;
	public static Game game;
	public static GameView gameView;
	public static String level = "5";
	private static Handler threadHandler = new Handler() {
		public void handleMessage(Message msg) {
			gameView.invalidate();
			dialog.dismiss();
			// Toast.makeText(GameActivity.this, "Puzzle Completed ..",
			// Toast.LENGTH_LONG).show();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		level = (String) sharedPrefs.getString("prefPuzzleDifficulty", "NULL");
		Log.d("ISOLATE", "Preferences Set !!" + level);

		game = new Game();
		game.registerListener(this);
		game.newGame(Integer.parseInt(level.toString()));
		gameView = new GameView(this, game);

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.activity_menu, null);

		RelativeLayout myLayout = new RelativeLayout(this);
		myLayout.addView(gameView);
		myLayout.addView(v);
		setContentView(myLayout);

		TextView tv = (TextView) findViewById(R.id.level);
		tv.setText("Difficulty Level = " + level);

		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				Log.d("ISOLATE", "Long Click Detected");
				return true;
			}
		});

	}

	// make a Toast that we can position more prominently
	private void makeToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 100); // x offset , y offset
		toast.show();
	}

	@Override
	public void onPuzzleCompleted() {
		// showDialog();
		// Toast.makeText(this, "Puzzle Completed ..",
		// Toast.LENGTH_LONG).show();
		makeToast("Puzzle Completed ..");
	}

	public void ResetOnClick(View v) {
		game.reset();
		gameView.invalidate();
	}

	public void SolveOnClick(View v) {
		game.reset();
		gameView.invalidate();

		dialog = ProgressDialog.show(this, "APP has 10 seconds to solve",
				"trying .. please wait");

		solver = new Thread() {
			@Override
			public void run() {
				try {
					// delay execution of this thread to allow scheduled redraw
					// on UI thread to occur first
					Thread.sleep(100);
					game.solve(1, 1, threadHandler);
				} catch (Exception e) {
					System.out
							.println("Somebody Interrupted the Solution Thread");
					e.printStackTrace();
				}
				threadHandler.sendEmptyMessage(0);
				runOnUiThread(new Runnable() {
					public void run() {
						makeToast("Puzzle Solved by Computer ..");
					}
				});
				timer.interrupt();
			}
		};

		timer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(10000);
				} catch (Exception e) {
					System.out
							.println("Somebody Interrupted the Timeout Thread");
					e.printStackTrace();
				}
				solver.interrupt();
			}
		};

		solver.start();
		timer.start();
	}

	public void NewPuzzleOnClick(View v) {
		TextView tv = (TextView) findViewById(R.id.level);
		tv.setText("Difficulty Level = " + level);
		game.newGame(Integer.parseInt(level.toString()));
		gameView.invalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected  " + item.getTitle());
		switch (item.getItemId()) {
		case R.id.action_settings:
			Log.d(TAG, "Settings");
			Intent settingsIntent = new Intent(this, UserSettingActivity.class);
			startActivityForResult(settingsIntent, RESULT_SETTINGS);
			break;
		case R.id.item1:
			// increase difficulty level
			if (Integer.parseInt(level) < 10) {
				level = Integer.toString(Integer.parseInt(level) + 1);
				SharedPreferences sharedPrefs = PreferenceManager
						.getDefaultSharedPreferences(this);
				Editor editor = sharedPrefs.edit();
				editor.putString("prefPuzzleDifficulty", level).commit();
				NewPuzzleOnClick(gameView);
			}

			break;
		case R.id.item2:
			// decrease difficulty level
			if (Integer.parseInt(level) > 1) {
				level = Integer.toString(Integer.parseInt(level) - 1);
				SharedPreferences sharedPrefs = PreferenceManager
						.getDefaultSharedPreferences(this);
				Editor editor = sharedPrefs.edit();
				editor.putString("prefPuzzleDifficulty", level).commit();
				NewPuzzleOnClick(gameView);
			}
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult called");
		switch (requestCode) {
		case RESULT_SETTINGS:
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			level = (String) sharedPrefs.getString("prefPuzzleDifficulty",
					"NULL");
			break;

		}
	}

	public void showDialog() {
		DialogFragment newFragment = new ConfirmDialog();
		newFragment.show(getFragmentManager(), "dialog");
	}
}
