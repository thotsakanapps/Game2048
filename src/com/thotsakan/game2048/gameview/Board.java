package com.thotsakan.game2048.gameview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Canvas;

final class Board {

	private int gameMilestone;

	private int score;

	private Tile[] tiles;

	public Board() {
		resetGame();
	}

	private void addTile() {
		List<Tile> slots = availableSlots();
		if (!slots.isEmpty()) {
			int index = (int) (Math.random() * slots.size());
			slots.get(index).setValue(Math.random() < 0.9 ? 2 : 4);
		}
	}

	private boolean areSame(Tile[] line1, Tile[] line2) {
		if (line1 == line2) {
			return true;
		}
		if (line1.length != line2.length) {
			return false;
		}
		for (int i = 0; i < line1.length; i++) {
			if (!line1[i].isSame(line2[i])) {
				return false;
			}
		}
		return true;
	}

	private List<Tile> availableSlots() {
		final List<Tile> slots = new ArrayList<Tile>();
		for (Tile tile : tiles) {
			if (tile.isEmpty()) {
				slots.add(tile);
			}
		}
		return slots;
	}

	public boolean canMove() {
		if (!availableSlots().isEmpty()) {
			return true;
		}

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Tile thisTile = tileAt(i, j);
				if (i < 3 && thisTile.isSame(tileAt(i + 1, j))) {
					return true;
				}
				if (j < 3 && thisTile.isSame(tileAt(i, j + 1))) {
					return true;
				}
			}
		}
		return false;
	}

	public void down() {
		tiles = rotate(270);
		left();
		tiles = rotate(90);
	}

	public void draw(Canvas canvas, Resources resources, float originX, float originY, float width, float height) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				tiles[i * 4 + j].draw(canvas, resources, originX, originY, i, j, width, height);
			}
		}
	}

	private void ensureListSize(List<Tile> list, int size) {
		int toAdd = size - list.size();
		while (toAdd-- > 0) {
			list.add(new Tile());
		}
	}

	public int getGameMilestone() {
		return gameMilestone;
	}

	private Tile[] getLine(int index) {
		Tile[] line = new Tile[4];
		for (int i = 0; i < 4; i++) {
			line[i] = tileAt(index, i);
		}
		return line;
	}

	public int getScore() {
		return score;
	}

	public void left() {
		boolean addNewTile = false;
		for (int i = 0; i < 4; i++) {
			Tile[] line = getLine(i);
			Tile[] mergedLine = mergeLine(moveLine(line));
			System.arraycopy(mergedLine, 0, tiles, i * 4, 4);
			if (!addNewTile && !areSame(line, mergedLine)) {
				addNewTile = true;
			}
		}
		if (addNewTile) {
			addTile();
		}
	}

	private Tile[] mergeLine(Tile[] line) {
		List<Tile> list = new LinkedList<Tile>();
		for (int i = 0; i < 4 && !line[i].isEmpty(); i++) {
			int val = line[i].getValue();
			if (i < 3 && line[i].isSame(line[i + 1])) {
				val *= 2;
				score += val;
				if (val % gameMilestone == 0) {
					gameMilestone *= 2;
				}
				i++;
			}
			list.add(new Tile(val));
		}
		if (list.isEmpty()) {
			return line;
		}
		ensureListSize(list, 4);
		Tile[] mergedLine = new Tile[4];
		return list.toArray(mergedLine);
	}

	private Tile[] moveLine(Tile[] line) {
		List<Tile> list = new LinkedList<Tile>();
		for (Tile tile : line) {
			if (!tile.isEmpty()) {
				list.add(tile);
			}
		}
		if (list.isEmpty()) {
			return line;
		}
		ensureListSize(list, 4);
		Tile[] movedLine = new Tile[4];
		return list.toArray(movedLine);
	}

	private void resetGame() {
		score = 0;
		gameMilestone = 2048;
		tiles = new Tile[16];
		for (int i = 0; i < 16; i++) {
			tiles[i] = new Tile();
		}
		addTile();
		addTile();
	}

	public void right() {
		tiles = rotate(180);
		left();
		tiles = rotate(180);
	}

	// counter clock wise rotate
	private Tile[] rotate(int angle) {
		int offsetX = 3, offsetY = 3;
		if (angle == 90) {
			offsetY = 0;
		} else if (angle == 270) {
			offsetX = 0;
		}

		double rad = Math.toRadians(angle);
		int cos = (int) Math.cos(rad);
		int sin = (int) Math.sin(rad);

		Tile[] newTiles = new Tile[4 * 4];
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				int newX = (x * cos) - (y * sin) + offsetX;
				int newY = (x * sin) + (y * cos) + offsetY;
				newTiles[newX * 4 + newY] = tileAt(x, y);
			}
		}
		return newTiles;
	}

	private Tile tileAt(int x, int y) {
		return tiles[x * 4 + y];
	}

	public void up() {
		tiles = rotate(90);
		left();
		tiles = rotate(270);
	}
}
