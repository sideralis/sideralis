package fr.dox.sideralis;

import java.util.Calendar;

import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.location.Position;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * 
 * @author Bernard TODO: Save preferences TODO: Get position from GPS TODO:
 *         Improve display: zoom TODO: Improve display: scroll TODO: Get
 *         standard info on objects TODO: Get full info on objects: open wiki
 *         page TODO: Support for localization TODO: Add splash screen and
 *         remove progress bar
 */
public class Sideralis extends Activity {

	private static final int MENU_SETTINGS = Menu.FIRST;
	private static final int MENU_ABOUT = Menu.FIRST + 1;
	private static final int MENU_SETTING_DATE = Menu.FIRST + 2;
	private static final int MENU_SETTING_TIME = Menu.FIRST + 3;
	private static final int MENU_SETTING_POSITION = Menu.FIRST + 4;
	private static final int MENU_SETTING_DISPLAY = Menu.FIRST + 5;

	private static final int PROGRESS_DIALOG = 0;
	private static final int POSITION_ACTIVITY = 1;
	public Position myPosition;
	private Sky mySky;
	private ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Sideralis: onCreate");

		myPosition = new Position();
		mySky = new Sky(myPosition, handler);

		showDialog(PROGRESS_DIALOG);

		mySky.initSky();
		Thread thread = new Thread(mySky);
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("Sideralis: onDestroy");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("Sideralis: onPause");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println("Sideralis: onRestart");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("Sideralis: onResume");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("Sideralis: onStart");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("Sideralis: onStop");
	}

	/**
     * 
     */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(Sideralis.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			Resources r = getResources();
			progressDialog.setMessage(r.getString(R.string.progress_message));
			return progressDialog;
		default:
			return null;
		}
	}

	/**
	 * Define the Handler that receives messages from the thread and update the
	 * progress
	 */
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			progressDialog.setProgress(total);
			if (total >= 100) {
				dismissDialog(PROGRESS_DIALOG);
				setContentView(R.layout.main);
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		int groupID = 0;
		int menuItemOrder = Menu.NONE;
		int menuItemText = R.string.menu_item_settings;

		SubMenu sub = menu.addSubMenu(groupID, MENU_SETTINGS, menuItemOrder++, menuItemText);
		sub.add(groupID, MENU_SETTING_DATE, Menu.NONE, R.string.menu_item_setting_date);
		sub.add(groupID, MENU_SETTING_TIME, Menu.NONE, R.string.menu_item_setting_time);
		sub.add(groupID, MENU_SETTING_POSITION, Menu.NONE, R.string.menu_item_setting_position);
		sub.add(groupID, MENU_SETTING_DISPLAY, Menu.NONE, R.string.menu_item_setting_display);

		menuItemText = R.string.menu_item_about;
		menu.add(groupID, MENU_ABOUT, menuItemOrder++, menuItemText);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Window window;
		Dialog d;
		TimePickerDialog tpDialog;
		DatePickerDialog dpDialog;

		switch (item.getItemId()) {

		case MENU_SETTING_DATE:
			OnDateSetListener datePickerCallBack;
			datePickerCallBack = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					myPosition.getTemps().getDate().set(Calendar.YEAR, year);
					myPosition.getTemps().getDate().set(Calendar.MONDAY, monthOfYear);
					myPosition.getTemps().getDate().set(Calendar.DAY_OF_MONTH, dayOfMonth);
				}
			};

			dpDialog = new DatePickerDialog(this, datePickerCallBack, myPosition.getTemps().getDate().get(Calendar.YEAR), myPosition.getTemps().getDate().get(Calendar.MONTH), myPosition.getTemps().getDate().get(Calendar.DAY_OF_MONTH));
			dpDialog.show();
			return true;

		case MENU_SETTING_TIME:
			OnTimeSetListener timePickerCallBack;
			timePickerCallBack = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					myPosition.getTemps().getDate().set(Calendar.HOUR_OF_DAY, hourOfDay);
					myPosition.getTemps().getDate().set(Calendar.MINUTE, minute);
				}
			};
			tpDialog = new TimePickerDialog(this, timePickerCallBack, myPosition.getTemps().getDate().get(Calendar.HOUR_OF_DAY), myPosition.getTemps().getDate().get(Calendar.MINUTE), true);
			tpDialog.show();
			return true;

		case MENU_SETTING_POSITION:
			Intent intent = new Intent(Sideralis.this, PositionActivity.class);
			SharedPreferences myPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
			SharedPreferences.Editor e = myPref.edit();
			e.putString("Longitude", new Double(myPosition.getLongitude()).toString());
			e.putString("Latitude", new Double(myPosition.getLatitude()).toString());
			e.commit();
			startActivityForResult(intent, POSITION_ACTIVITY);
			return true;

		case MENU_ABOUT:
			d = new Dialog(this);
			window = d.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			d.setTitle("About Sideralis");
			d.setContentView(R.layout.about);
			d.show();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case POSITION_ACTIVITY:
			if (resultCode == Activity.RESULT_OK) {
				SharedPreferences myPref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
				myPosition.setLongitude(new Double(myPref.getString("Longitude", "0")));
				myPosition.setLatitude(new Double(myPref.getString("Latitude", "0")));
				progressDialog.setProgress(0);
				showDialog(PROGRESS_DIALOG);
				Thread thread = new Thread(mySky);
				thread.start();
			}
			break;
		}
	}

	/**
	 * @return the myPosition
	 */
	public Position getMyPosition() {
		return myPosition;
	}

	/**
	 * @param myPosition
	 *            the myPosition to set
	 */
	public void setMyPosition(Position myPosition) {
		this.myPosition = myPosition;
	}

	/**
	 * @return the mySky
	 */
	public Sky getMySky() {
		return mySky;
	}

	/**
	 * @param mySky
	 *            the mySky to set
	 */
	public void setMySky(Sky mySky) {
		this.mySky = mySky;
	}

}