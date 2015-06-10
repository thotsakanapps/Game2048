package com.thotsakan.game2048.gameview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.LruCache;

import com.thotsakan.game2048.MainActivity;

final class Tile {

	private static final LruCache<Integer, Bitmap> bitmapCache = new LruCache<Integer, Bitmap>(10);

	private static final Paint paintTile;

	static {
		paintTile = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintTile.setColor(Color.GRAY);
		paintTile.setStrokeWidth(0);
		paintTile.setStyle(Style.STROKE);
	}

	private int value;

	public Tile() {
		this(0);
	}

	public Tile(int value) {
		this.value = value;
	}

	public void draw(Canvas canvas, Resources resources, float originX, float originY, int x, int y, float width, float height) {
		Bitmap bitmap = bitmapCache.get(value);
		if (bitmap == null) {
			int resourceId = resources.getIdentifier("ic_tile_" + value, "drawable", MainActivity.class.getPackage().getName());
			bitmap = BitmapFactory.decodeResource(resources, resourceId);
			bitmapCache.put(value, bitmap);
		}
		RectF rect = new RectF(y * height, x * width, (y * height) + height, (x * width) + width);
		rect.offset(originX, originY);
		rect.inset(3, 3);
		canvas.drawBitmap(bitmap, null, rect, paintTile);
		canvas.drawRect(rect, paintTile);
	}

	public int getValue() {
		return value;
	}

	public boolean isEmpty() {
		return value == 0;
	}

	public boolean isSame(Tile tile) {
		return value == tile.value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
