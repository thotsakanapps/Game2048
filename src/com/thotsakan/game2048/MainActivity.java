package com.thotsakan.game2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.thotsakan.game2048.gameview.GameView;

public class MainActivity extends Activity {

	private GameView gameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LinearLayout gameBoard = (LinearLayout) findViewById(R.id.game_view);
		gameView = new GameView(this);
		gameBoard.addView(gameView);

		// AdMob
		AdView mAdView = (AdView) findViewById(R.id.ad_view);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_info:
			showInfo();
			break;
		case R.id.action_new:
			gameView.newGame();
			break;
		case R.id.action_rate_app:
			Intent rateAppIntent = new Intent(Intent.ACTION_VIEW);
			rateAppIntent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.class.getPackage().getName()));
			startActivity(rateAppIntent);
			break;
		case R.id.action_other_apps:
			Intent otherAppsIntent = new Intent(Intent.ACTION_VIEW);
			otherAppsIntent.setData(Uri.parse("https://play.google.com/store/search?q=pub:" + getString(R.string.action_other_apps_publisher)));
			startActivity(otherAppsIntent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showInfo() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.info_dialog_title);
		dialogBuilder.setMessage(R.string.info_dialog_message);
		dialogBuilder.setNeutralButton(R.string.info_dialog_close_button_text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogBuilder.show();
	}
}
