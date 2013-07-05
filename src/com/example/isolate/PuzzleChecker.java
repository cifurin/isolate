package com.example.isolate;

public class PuzzleChecker {
	private int[][] Wall;
	private int group;
	private int[] g = new int[26];
	private int[] grp = new int[26];
	private int[][] s = new int[13][2];
	private int[][] Tg = new int[26][2];
	private int[] ms;

	public int[] getG() {
		return g;
	}

	public int[] getGrp() {
		return grp;
	}

	public PuzzleChecker() {
	}

	private boolean valid(int x, int y) {
		if ((x > 0) && (x < 26) && !(x == y)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean contains(int n) {
		// Check if the solution contains a group of size n
		for (int x = 1; x < 13; x++) {
			if (s[x][0] == n) {
				return true;
			}
		}
		return false;
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

	private int count(int x) {
		// counts how many marked squares are in group x
		int count = 0;
		for (int y = 1; y < 26; y++) {
			if ((g[y] == x) && (ms[y - 1] == 1)) {
				count++;
			}
		}
		return count;
	}

	private void findgroups(int x, int y) {
		if (g[x] == 0) {
			g[x] = group;
			if (valid(x + 1, y) && !((x % 5) == 0)) {
				if ((Wall[x][x + 1] == 0) || (Wall[x][x + 1] == 2)) {
					findgroups(x + 1, x);
				}
			}
			if (valid(x + 5, y)) {
				if ((Wall[x][x + 5] == 0) || (Wall[x][x + 5] == 2)) {
					findgroups(x + 5, x);
				}
			}
			if (valid(x - 1, y) && !(((x - 1) % 5) == 0)) {
				if ((Wall[x - 1][x] == 0) || (Wall[x - 1][x] == 2)) {
					findgroups(x - 1, x);
				}
			}
			if (valid(x - 5, y)) {
				if ((Wall[x - 5][x] == 0) || (Wall[x - 5][x] == 2)) {
					findgroups(x - 5, x);
				}
			}
		}
	}

	private static int count(int[] array) {
		int count = 0;
		for (int i : array) {
			if (i > 0) {
				count++;
			}
		}
		return count;
	}

	public boolean check(int p, int S[], int Wall[][], int ms[], int mode) {

		this.Wall = Wall;
		this.ms = ms;
		int Ng = count(S);

		for (int x = 1; x < 13; x++) {
			s[x][0] = 0;
			Tg[x][0] = 0;
		}

		for (int x = 1; x < (Ng + 1); x++) {
			s[x][0] = S[x - 1];
			Tg[s[x][0]][0] = Tg[s[x][0]][0] + 1;
		}

		for (int x = 1; x < 26; x++) {
			g[x] = 0;
			Tg[x][1] = 0;
			grp[x] = 0;
		}

		group = 1;
		for (int x = 1; x < 26; x++) {
			if (g[x] == 0) {
				findgroups(x, 0);
				group = group + 1;
			}
			grp[g[x]] = grp[g[x]] + 1;
		}

		for (int x = 1; x < group; x++) {

			if (grp[x] == 1) {
				return false;
			}
			switch (mode) {
			case 0:
				for (int s = 2; s < 8; s++) {
					if ((grp[x] == s)) {
						if (contains(grp[x])) {
							Tg[grp[x]][1] = Tg[grp[x]][1] + 1;
							if (Tg[grp[x]][1] > Tg[grp[x]][0]) {
								return false;
							}
						} else {
							return false;
						}
					}
				}
				break;
			case 1:
				for (int s = 2; s < 8; s++) {
					if ((grp[x] == s)) {
						if ((contains(grp[x])) && (count(x) == 2)) {
							Tg[grp[x]][1] = Tg[grp[x]][1] + 1;
							if (Tg[grp[x]][1] > Tg[grp[x]][0]) {
								return false;
							}
						} else {
							switch (s) {
							case 2:
							case 3:
								return false;
							case 4:
							case 5:
								if (!(count(x) == 4)) {
									return false;
								}
								break;
							case 6:
							case 7:
								if (!((count(x) == 4) || (count(x) == 6))) {
									return false;
								}
								break;
							}
						}
					}
				}
				break;
			}
		}

		if (p == 16) {
			if (group == (Ng + 1)) {

				for (int x = 1; x < 13; x++) {
					s[x][1] = 0;
				}

				for (int x = 1; x < group; x++) {
					if (grp[x] > 1) {
						int y = 1;
						boolean found = false;
						do {
							if ((s[y][0] == grp[x]) && (s[y][1] == 0)) {
								s[y][1] = 1;
								found = true;
							}
							y++;
						} while ((y < group) && (found == false));
					}
				}

				for (int x = 1; x < group; x++) {
					if (s[x][1] == 0) {
						return false;
					}
				}

			} else {
				return false;
			}
		}
		return true;
	}
}