package com.thotsakan.game2048.gameview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.thotsakan.game2048.MainActivity;
import com.thotsakan.game2048.R;

public class GameView extends View {

	private static final int _BOARD_SIZE = 4;

	private static final int _DIRECTION_DOWN = -3;

	private static final int _DIRECTION_LEFT = -4;

	private static final int _DIRECTION_RIGHT = -2;

	private static final int _DIRECTION_UP = -1;

	private int bestScore;

	private Board board;

	private boolean gameOver;

	private GestureDetector gestureDetector;

	private boolean objectiveAchieved = false;

	private Paint paint;

	public GameView(Context context) {
		super(context);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(getResources().getColor(R.color.board));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(0);

		gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				switch (_getDirection(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
				case _DIRECTION_DOWN:
					board.down();
					break;
				case _DIRECTION_UP:
					board.up();
					break;
				case _DIRECTION_LEFT:
					board.left();
					break;
				case _DIRECTION_RIGHT:
					board.right();
					break;
				}
				invalidate();
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});

		newGame();
	}

	private double _getAngle(float x1, float y1, float x2, float y2) {
		double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
		return (rad * 180 / Math.PI + 180) % 360;
	}

	private int _getDirection(float x1, float y1, float x2, float y2) {
		double angle = _getAngle(x1, y1, x2, y2);
		if (_inRange(angle, 45, 135)) {
			return _DIRECTION_UP;
		} else if (_inRange(angle, 225, 315)) {
			return _DIRECTION_DOWN;
		} else if (_inRange(angle, 135, 225)) {
			return _DIRECTION_LEFT;
		} else {
			return _DIRECTION_RIGHT;
		}
	}

	private boolean _inRange(double angle, float init, float end) {
		return (angle >= init) && (angle < end);
	}

	private void displayResult() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(R.string.result_dialog_title);

		if (gameOver) {
			updateBestScore();
			dialogBuilder.setMessage(R.string.result_dialog_message_gameOver);
			dialogBuilder.setNeutralButton(R.string.result_dialog_newGame, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					newGame();
					dialog.dismiss();
				}
			});
		} else {
			dialogBuilder.setMessage(R.string.result_dialog_message_objectiveAchieved);
			dialogBuilder.setNeutralButton(R.string.result_dialog_continueGame, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		dialogBuilder.show();
	}

	public void newGame() {
		gameOver = false;
		objectiveAchieved = false;
		board = new Board();
		updateCurrentScore();
		showBestScore();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int tileLen = Math.min(getWidth(), getHeight()) / _BOARD_SIZE;
		board.draw(canvas, getResources(), tileLen, tileLen);
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthMeasureSpec) * 0.95), MeasureSpec.AT_MOST);
		int measuredheight = MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(heightMeasureSpec) * 0.95), MeasureSpec.AT_MOST);
		setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), measuredWidth), getDefaultSize(getSuggestedMinimumHeight(), measuredheight));
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gameOver) {
			return super.onTouchEvent(event);
		} else {
			if (!board.canMove()) {
				gameOver = true;
				updateBestScore();
				displayResult();
				return super.onTouchEvent(event);
			}

			int oldMilestone = board.getGameMilestone();
			boolean onTouchResult = gestureDetector.onTouchEvent(event);
			int newMilestone = board.getGameMilestone();
			updateCurrentScore();

			if (oldMilestone != newMilestone && !objectiveAchieved) {
				objectiveAchieved = true;
				displayResult();
			} else if (!board.canMove()) {
				gameOver = true;
				updateBestScore();
				displayResult();
			}
			return onTouchResult;
		}
	}

	private void showBestScore() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		String key = getResources().getString(R.string.best_score_key);
		bestScore = prefs.getInt(key, 0);
		TextView bestScoreView = (TextView) ((MainActivity) getContext()).findViewById(R.id.best_score);
		bestScoreView.setText(Html.fromHtml("<b>BEST</b><br/>" + bestScore));
	}

	private void updateBestScore() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		String key = getResources().getString(R.string.best_score_key);

		int bestScore = prefs.getInt(key, 0);
		if (bestScore < board.getScore()) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(key, board.getScore());
			editor.apply();
		}
	}

	private void updateCurrentScore() {
		int currentScore = board.getScore();
		TextView currentScoreView = (TextView) ((MainActivity) getContext()).findViewById(R.id.current_score);
		currentScoreView.setText(Html.fromHtml("<b>SCORE</b><br/>" + currentScore));

		if (bestScore < currentScore) {
			TextView bestScoreView = (TextView) ((MainActivity) getContext()).findViewById(R.id.best_score);
			bestScoreView.setText(Html.fromHtml("<b>BEST</b><br/>" + currentScore));
		}
	}
}
