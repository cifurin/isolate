package com.example.isolate;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	private static final String TAG = "ISOLATE";
	private Paint paint = new Paint();
	private Paint background = new Paint();

	private int[][] Wall;
	private int[] ms;
	private int[] S;
	private Game game;

	private float scalex;
	private float scaley;
	
	private ArrayList<WallPosition> wps = new ArrayList<WallPosition>();
	
	private class WallPosition extends RectF {
		public WallPosition(float left, float top, float right, float bottom) {
			super(left, top, right, bottom);
		}

		private int x;
		private int y;
	}

	public GameView(Context context) {
		super(context);
	}

	public GameView(Context context, Game game) {
		super(context);
		this.game = game;
	}

	private void set() {
		wps.clear();
		for (int y = 1; y < 6; y++) {
			for (int x = 1; x < 5; x++) {
				int i = (y - 1) * 5 + x;
				if (Wall[i][i + 1] != 1) {
					float left = (float) ((x + 0.70) * scalex);
					float top = (float) ((y + .20) * scalex);
					float right = (float) ((x + 1.30) * scalex);
					float bottom = (float) ((y + 0.80) * scalex);

					WallPosition wp = new WallPosition(left, top, right, bottom);
					wp.x = i;
					wp.y = i + 1;
					wps.add(wp);
				}
			}
		}

		for (int y = 1; y < 5; y++) {
			for (int x = 1; x < 6; x++) {
				int i = (y - 1) * 5 + x;
				if (Wall[i][i + 5] != 1) {
					float left = (float) ((x + 0.20) * scalex);
					float top = (float) ((y + 0.70) * scalex);
					float right = (float) ((x + 0.80) * scalex);
					float bottom = (float) ((y + 1.30) * scalex);

					WallPosition wp = new WallPosition(left, top, right, bottom);
					wp.x = i;
					wp.y = i + 5;
					wps.add(wp);
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Wall = game.getWall();
		S = game.getS();
		ms = game.getMs();
		
		scalex = getWidth() / 7;
		scaley = getHeight() / 10;
		set();

		paint.setAntiAlias(true);
		paint.setColor(Color.MAGENTA);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(5f);

		background.setColor(Color.CYAN);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);

		Log.d(TAG, "Scale X " + scalex);
		Log.d(TAG, "Scale Y " + scaley);
		Log.d(TAG, "CANVAS WIDTH IS " + canvas.getWidth());
		Log.d(TAG, "CANVAS HEIGHT IS " + canvas.getHeight());

		paint.setColor(Color.BLUE);
		canvas.drawRect(scalex, scalex, 6 * scalex, 6 * scalex, paint);

		// DRAW PUZZLE GRID
		for (int y = 1; y < 6; y++) {
			for (int x = 1; x < 5; x++) {
				int i = (y - 1) * 5 + x;

				if (Wall[i][i + 1] == 1) {
					paint.setColor(Color.BLUE);
					canvas.drawLine((x + 1) * scalex, y * scalex, (x + 1)
							* scalex, (y + 1) * scalex, paint);
				} else if ((Wall[i][i + 1] == 3) || (Wall[i][i + 1] == 4)) {
					paint.setColor(Color.MAGENTA);
					canvas.drawLine((x + 1) * scalex, y * scalex, (x + 1)
							* scalex, (y + 1) * scalex, paint);
				} else if ((Wall[i][i + 1] == 5) || (Wall[i][i + 1] == 6)) {
					paint.setColor(Color.DKGRAY);
					canvas.drawLine((x + 1) * scalex, y * scalex, (x + 1)
							* scalex, (y + 1) * scalex, paint);
				}
			}
		}
		for (int y = 1; y < 5; y++) {
			for (int x = 1; x < 6; x++) {
				int i = (y - 1) * 5 + x;
				if (Wall[i][i + 5] == 1) {
					paint.setColor(Color.BLUE);
					canvas.drawLine((x) * scalex, (y + 1) * scalex, (x + 1)
							* scalex, (y + 1) * scalex, paint);
				} else if ((Wall[i][i + 5] == 3) || (Wall[i][i + 5] == 4)) {
					paint.setColor(Color.MAGENTA);
					canvas.drawLine((x) * scalex, (y + 1) * scalex, (x + 1)
							* scalex, (y + 1) * scalex, paint);
				} else if ((Wall[i][i + 5] == 5) || (Wall[i][i + 5] == 6)) {
					paint.setColor(Color.DKGRAY);
					canvas.drawLine((x) * scalex, (y + 1) * scalex, (x + 1)
							* scalex, (y + 1) * scalex, paint);
				}
			}
		}
		// DRAW CROSSES
		paint.setColor(Color.BLUE);
		for (int y = 2; y < 6; y++) {
			for (int x = 2; x < 6; x++) {
				float offset = 0.1f;
				canvas.drawLine((x - offset) * scalex, y * scalex, (x + offset)
						* scalex, y * scalex, paint);
				canvas.drawLine(x * scalex, (y - offset) * scalex, x * scalex,
						(y + offset) * scalex, paint);
			}
		}
		// DRAW MARKED SQUARES
		paint.setColor(Color.BLUE);
		paint.setStyle(Style.FILL);
		for (int y = 1; y < 6; y++) {
			for (int x = 1; x < 6; x++) {
				if (ms[x + (y - 1) * 5 - 1] == 1) {
					canvas.drawRect((x + 0.4f) * scalex, (y + 0.4f) * scalex,
							(x + 0.6f) * scalex, (y + 0.6f) * scalex, paint);
				}
			}
		}

		paint.setColor(Color.BLUE);
		paint.setTextSize(40 * scalex / 100);
		paint.setTextAlign(Paint.Align.CENTER);

		String s = String.valueOf(S[0]);
		for (int x = 2; x < S.length; x++) {
			if (S[x - 1] > 0) {
				s = s + " " + "." + " " + String.valueOf(S[x - 1]);
			}
		}
		canvas.drawText(s, getWidth() / 2, 7.5f * scaley, paint);

/*		if (game.isComplete()) {
			canvas.drawText("Puzzle has been solved", getWidth() / 2,
					8.5f * scaley, paint);
		}*/
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventX = (int) event.getX();
		int eventY = (int) event.getY();
		Log.d(TAG, "eventX is " + eventX);
		Log.d(TAG, "eventY is " + eventY);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			for (WallPosition w : wps) {
				if (w.contains(eventX, eventY)) {
					Log.d(TAG, "x is " + w.x);
					Log.d(TAG, "y is " + w.y);
					game.toggleWall(w.x, w.y);
				}
			}
			invalidate();
			break;
		default:
			return false;
		}
		return true;
	}
}
