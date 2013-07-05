package com.example.isolate;

import java.util.Arrays;
import java.util.Random;
import android.os.Handler;
import android.util.Log;

public class Game {
	private gameListener listener;

	public void registerListener(gameListener listener) {
		this.listener = listener;
	}

	private static final String TAG = "ISOLATE";
	private boolean complete = false;
	private int[][] Wall = new int[26][26];
	private int[] ms = new int[26];
	private int[] S = new int[12];
	private Handler h;
	private int Ng;
	private PuzzleChecker checker;

	public Game() {
		checker = new PuzzleChecker();
	}

	public void newGame(int level) {
		complete = false;
		clearwalls();
		gensol();
		solve(1, 0, h);
		markers();
		remove(level);
	}

	public void gensol() {
		int t = 0;
		Random r = new Random();
		Ng = 0;
		// clear previous solution
		for (int x = 1; x < 13; x++) {
			S[x - 1] = 0;
		}

		do {
			int rn = r.nextInt(7 - 2 + 1) + 2;
			if (!((t + rn) == 24) && ((t + rn) < 26)) {
				Log.d(TAG, "random number chosen " + rn);
				S[Ng] = rn;
				t = t + rn;
				Ng++;
			}
		} while (!(t == 25));
		// sort the numbers into ascending order
		Arrays.sort(S, 0, Ng);
	}

	private void clearwalls() {

		for (int y = 1; y < 6; y++) {
			for (int x = 1; x < 5; x++) {
				int i = (y - 1) * 5 + x;
				Wall[i][i + 1] = 0;
			}
		}

		for (int y = 1; y < 5; y++) {
			for (int x = 1; x < 6; x++) {
				int i = (y - 1) * 5 + x;
				Wall[i][i + 5] = 0;
			}
		}
	}

	public void reset() {

		for (int y = 1; y < 6; y++) {
			for (int x = 1; x < 5; x++) {
				int i = (y - 1) * 5 + x;
				if (Wall[i][i + 1] == 3) {
					Wall[i][i + 1] = 2;
				} else if (Wall[i][i + 1] == 4) {
					Wall[i][i + 1] = 0;
				} else if (Wall[i][i + 1] == 5) {
					Wall[i][i + 1] = 2;
				} else if (Wall[i][i + 1] == 6) {
					Wall[i][i + 1] = 0;
				}
			}
		}

		for (int y = 1; y < 5; y++) {
			for (int x = 1; x < 6; x++) {
				int i = (y - 1) * 5 + x;
				if (Wall[i][i + 5] == 3) {
					Wall[i][i + 5] = 2;
				} else if (Wall[i][i + 5] == 4) {
					Wall[i][i + 5] = 0;
				} else if (Wall[i][i + 5] == 5) {
					Wall[i][i + 5] = 2;
				} else if (Wall[i][i + 5] == 6) {
					Wall[i][i + 5] = 0;
				}
			}
		}
		complete = false;
	}

	private void remove(int level) {
		Random r = new Random();
		for (int y = 1; y < 6; y++) {
			for (int x = 1; x < 5; x++) {
				int i = (y - 1) * 5 + x;
				if (Wall[i][i + 1] == 1) {
					if (r.nextInt(10) < level) {
						Wall[i][i + 1] = 2;
					}
				}
			}
		}
		for (int y = 1; y < 5; y++) {
			for (int x = 1; x < 6; x++) {
				int i = (y - 1) * 5 + x;
				if (Wall[i][i + 5] == 1) {
					if (r.nextInt(10) < level) {
						Wall[i][i + 5] = 2;
					}
				}
			}
		}
		complete = false;
	}

	private void markers() {
		int g[] = checker.getG();
		int grp[] = checker.getGrp();
		Random r = new Random();
		int r2;
		int count = 0;

		for (int m = 0; m < 25; m++) {
			ms[m] = 0;
		}

		for (int x = 1; x < (Ng + 1); x++) {
			// find 2 random positions in each group
			int r1 = r.nextInt(grp[x]) + 1;
			do {
				r2 = r.nextInt(grp[x]) + 1;
			} while (r2 == r1);
			count = 0;
			for (int y = 1; y < 26; y++) {
				if (g[y] == x) {
					count++;
					if ((r1 == count) || (r2 == count)) {
						ms[y - 1] = 1;
					}
				}
			}
		}
	}

	private int Walls(int p) {
		int n = 0;
		for (int s = 1; s < 5; s++) {
			int t = Wall[Transform.map[p - 1][(s - 1) * 2]][Transform.map[p - 1][(s - 1) * 2 + 1]];
			if ((t == 1) || (t == 3) || (t == 4) || (t == 5) || (t == 6)) {
				n = n + 1;
			}
		}
		return n;
	}

	public boolean checkWalls() {
		for (int p = 1; p < 17; p++) {
			if (Walls(p) < 2) {
				return false;
			}
		}
		return true;
	}

	public void solve(int p, int mode, Handler handler) {
		int[] t = new int[5];
		int x;
		int y;
		int n;
		int s;
		Random r = new Random();
		int ni;
		int offset;

		// Log.d(TAG, "Thread Name is " + Thread.currentThread().getName());

		if (Thread.currentThread().isInterrupted()) {
			System.out.println("Thread's Interrupt flag has been set !!");
			return;
		}

		if (p < 17) {
			// store existing solution(P) as a constraint
			for (n = 1; n < 5; n++) {
				x = Transform.map[p - 1][(n - 1) * 2];
				y = Transform.map[p - 1][(n - 1) * 2 + 1];
				t[n - 1] = Wall[x][y];
			}

			offset = r.nextInt(11);
			// try and find a partial solution at P
			n = 1;
			do {
				boolean match = true;
				s = 1;
				ni = n + offset;
				if (ni > 11) {
					ni = ni - 11;
				}
				do {
					x = Transform.map[p - 1][(s - 1) * 2];
					y = Transform.map[p - 1][(s - 1) * 2 + 1];
					if (((Wall[x][y] == 1) || (Wall[x][y] == 5) || (Wall[x][y] == 6))
							&& (Transform.ps[ni - 1][s - 1] == 0)) {
						match = false;
					}
					s++;
				} while ((s < 5) && (match == true));
				//

				if ((p == 1)
						&& ((ni == 3) || (ni == 8) || (ni == 9) || (ni == 11))) {
					match = false;
				}

				if ((p == 4)
						&& ((ni == 1) || (ni == 7) || (ni == 8) || (ni == 11))) {
					match = false;
				}

				if ((p == 13)
						&& ((ni == 6) || (ni == 9) || (ni == 10) || (ni == 11))) {
					match = false;
				}

				if ((p == 16)
						&& ((ni == 4) || (ni == 7) || (ni == 10) || (ni == 11))) {
					match = false;
				}

				if (match == true) {

					// Log.d(TAG, "Pillar is  " + p + "  Partial Solution is  "
					// + ni);

					add(p, ni, mode);
					/*
					 * if (mode == 1) { handler.sendEmptyMessage(0); }
					 */
					if (checker.check(p, S, Wall, ms, mode) == true) {
						/*
						 * Log.d(TAG, "Partial Solution VALID at  " + p); if
						 * (mode == 1) { handler.sendEmptyMessage(0); }
						 */
						solve(p + 1, mode, handler);
						if (complete == false) {
							remove(p, t);
							/*
							 * if (mode == 1) { handler.sendEmptyMessage(0); }
							 */
						} else {
							return;
						}
					} else {
						remove(p, t);
					}
				}
				n++;
			} while (n < 12);
		} else {
			Log.d(TAG, "SOLUTION FOUND !!");
			complete = true;
		}
	}

	private void add(int p, int n, int mode) {
		for (int s = 1; s < 5; s++) {
			int x = Transform.map[p - 1][(s - 1) * 2];
			int y = Transform.map[p - 1][(s - 1) * 2 + 1];
			if (mode == 0) {
				Wall[x][y] = Transform.ps[n - 1][s - 1];
			}
			if (mode == 1) {
				if ((Wall[x][y] == 0) && (Transform.ps[n - 1][s - 1] == 1)) {
					Wall[x][y] = 6;
				} else if ((Wall[x][y] == 2)
						&& (Transform.ps[n - 1][s - 1] == 1)) {
					Wall[x][y] = 5;
				} else if ((Wall[x][y] == 5)
						&& (Transform.ps[n - 1][s - 1] == 0)) {
					Wall[x][y] = 2;
				} else if ((Wall[x][y] == 6)
						&& (Transform.ps[n - 1][s - 1] == 0)) {
					Wall[x][y] = 0;
				}
			}
		}
	}

	private void remove(int p, int t[]) {
		for (int s = 1; s < 5; s++) {
			int x = Transform.map[p - 1][(s - 1) * 2];
			int y = Transform.map[p - 1][(s - 1) * 2 + 1];
			Wall[x][y] = t[s - 1];
		}
	}

	public int[][] getWall() {
		return Wall;
	}

	public void toggleWall(int x, int y) {
		if (Wall[x][y] == 0) {
			Wall[x][y] = 4;
		} else if (Wall[x][y] == 4) {
			Wall[x][y] = 0;
		} else if (Wall[x][y] == 2) {
			Wall[x][y] = 3;
		} else if (Wall[x][y] == 3) {
			Wall[x][y] = 2;
		} else if (Wall[x][y] == 5) {
			Wall[x][y] = 2;
		} else if (Wall[x][y] == 6) {
			Wall[x][y] = 0;
		}
		complete = checker.check(16, S, Wall, ms, 1) && checkWalls();
		if (listener != null && complete) {
			listener.onPuzzleCompleted();
		}
	}

	public int[] getMs() {
		return ms;
	}

	public void setMs(int[] ms) {
		this.ms = ms;
	}

	public int[] getS() {
		return S;
	}

	public void setS(int[] s) {
		S = s;
	}

	public int getNg() {
		return Ng;
	}

	public void setNg(int ng) {
		Ng = ng;
	}

	public boolean isComplete() {
		return complete;
	}
}