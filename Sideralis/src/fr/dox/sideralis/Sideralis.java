package fr.dox.sideralis;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.location.Position;
import fr.dox.sideralis.view.SideralisView;
import fr.dox.sideralis.view.SideralisView.SideralisViewThread;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.ZoomButton;


/**
 * 
 * @author Bernard 
 * TODO: Save preferences 
 * TODO: Get position from GPS 
 * TODO: Improve display: zoom 
 * TODO: Improve display: scroll 
 * TODO: Get standard info on objects 
 * TODO: Get full info on objects: open wiki page 
 * TODO: Support for localization 
 * TODO: Add splash screen and remove progress bar
 */
public class Sideralis extends Activity implements OnClickListener, OnTouchListener {

	private static final int MENU_SETTINGS = Menu.FIRST;
	private static final int MENU_ABOUT = Menu.FIRST + 1;
	private static final int MENU_SETTING_DATE = Menu.FIRST + 2;
	private static final int MENU_SETTING_TIME = Menu.FIRST + 3;
	private static final int MENU_SETTING_POSITION = Menu.FIRST + 4;
	private static final int MENU_SETTING_DISPLAY = Menu.FIRST + 5;

	private static final int POSITION_ACTIVITY = 1;
	public Position myPosition;
	private Sky mySky;
	private Timer timeOut;
	private boolean timeOutReached;
	private Vector<Float> vector;
	
	private ZoomButton zbIn;
	private ZoomButton zbOut;
	private SideralisView myView;
	private SideralisViewThread myViewThread;
	private Animation animationFadeIn,animationFadeOut;
	
	class TimeOut extends TimerTask {

		@Override
		public void run() {
//			zbIn.startAnimation(animationFadeOut);
//			zbOut.startAnimation(animationFadeOut);
			timeOutReached = true;
			Log.d("Sideralis","TimeOut reached");			
		}
		
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Sideralis: onCreate");


		// Create a position object
		myPosition = new Position();

		// Get settings
		SharedPreferences myPref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
		myPosition.setLongitude(new Double(myPref.getString("Longitude", "0")));
		myPosition.setLatitude(new Double(myPref.getString("Latitude", "0")));
		myPosition.getTemps().setTimeOffset(myPref.getLong("TimeOffset", 0L));
		
		// Create the full sky
		mySky = new Sky(myPosition);
		mySky.initSky();

		setContentView(R.layout.main);
        
		new Thread(mySky).start();
		
		zbIn = (ZoomButton) findViewById(R.id.zoomIn);
		zbOut = (ZoomButton) findViewById(R.id.zoomOut);
		myView = (SideralisView) findViewById(R.id.mySideralisView);
		myView.setOnClickListener(this);
		myView.setOnTouchListener(this);
		zbIn.setOnClickListener(this);
		zbOut.setOnClickListener(this);
		animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);		
		animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
		timeOut = null;
		timeOutReached = true;
		myViewThread = myView.getThread();
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
					GregorianCalendar gC = new GregorianCalendar();
					gC.set(year, monthOfYear, dayOfMonth);
					myPosition.getTemps().calculateTimeOffset(gC);
					
					SharedPreferences myPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
					SharedPreferences.Editor e = myPref.edit();
					e.putLong("TimeOffset", myPosition.getTemps().getTimeOffset());
					e.commit();
					SideralisView myView = (SideralisView) findViewById(R.id.mySideralisView);
					myView.setCounter(0);
				}
			};

			dpDialog = new DatePickerDialog(this, datePickerCallBack, myPosition.getTemps().getCalendar().get(Calendar.YEAR), myPosition.getTemps().getCalendar().get(Calendar.MONTH), myPosition.getTemps().getCalendar().get(Calendar.DAY_OF_MONTH));
			dpDialog.show();
			return true;

		case MENU_SETTING_TIME:
			OnTimeSetListener timePickerCallBack;
			timePickerCallBack = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					GregorianCalendar gC = new GregorianCalendar();
					gC.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
					gC.set(GregorianCalendar.MINUTE, minute);
					myPosition.getTemps().calculateTimeOffset(gC);

					SharedPreferences myPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
					SharedPreferences.Editor e = myPref.edit();
					e.putLong("TimeOffset", myPosition.getTemps().getTimeOffset());
					e.commit();
					SideralisView myView = (SideralisView) findViewById(R.id.mySideralisView);
					myView.setCounter(0);
				}
			};
			tpDialog = new TimePickerDialog(this, timePickerCallBack, myPosition.getTemps().getCalendar().get(Calendar.HOUR_OF_DAY), myPosition.getTemps().getCalendar().get(Calendar.MINUTE), true);
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
				SideralisView myView = (SideralisView) findViewById(R.id.mySideralisView);
				myView.setCounter(0);
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
	 * @param mySky the mySky to set
	 */
	public void setMySky(Sky mySky) {
		this.mySky = mySky;
	}

	@Override
	public void onClick(View v) {
		if (v == myView) {
			if (zbIn.getVisibility() == View.VISIBLE) {
				zbIn.startAnimation(animationFadeOut);
				zbOut.startAnimation(animationFadeOut);
				zbIn.setVisibility(View.INVISIBLE);
				zbOut.setVisibility(View.INVISIBLE);
			} else {
				zbIn.startAnimation(animationFadeIn);
				zbOut.startAnimation(animationFadeIn);
				zbIn.setVisibility(View.VISIBLE);
				zbOut.setVisibility(View.VISIBLE);	
			}
//			if (timeOutReached) {
////				zbIn.startAnimation(animationFadeIn);
////				zbOut.startAnimation(animationFadeIn);
//				zbIn.setVisibility(View.VISIBLE);
//				zbOut.setVisibility(View.VISIBLE);	
//				timeOutReached = false;
//				if (timeOut != null)
//					timeOut.cancel();
//				timeOut = null;
//				timeOut = new Timer();
//				timeOut.schedule(new TimeOut(), 2000);
//				Log.d("Sideralis","TimeOut Started after end");
//			} else {
//				if (timeOut != null)
//					timeOut.cancel();
//				timeOut = null;
//				timeOut = new Timer();
//				timeOut.schedule(new TimeOut(), 2000);
//				Log.d("Sideralis","TimeOut Started before end");
//			}
		} else if (v == zbIn) {
			myViewThread.zoomIn();
		} else if (v == zbOut) {
			myViewThread.zoomOut();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v == myView) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				vector = new Vector<Float>();
				vector.add(event.getX());
				vector.add(event.getY());
				break;
			case MotionEvent.ACTION_UP:
				vector.add(event.getX());
				vector.add(event.getY());
				myViewThread.setVector(vector);
				break;
			case MotionEvent.ACTION_MOVE:
				int historySize = event.getHistorySize();
				for (int i = 0; i<historySize;i++) {
//					long time = event.getHistoricalEventTime(i);
					float x = event.getHistoricalX(i);
					float y = event.getHistoricalY(i);
					vector.add(x);
					vector.add(y);
//					float size = event.getHistoricalSize(i);
//					Log.d("Sideralis","-"+i+": "+x+" "+y+" "+size+" "+time);
				}
				break;
			}
		}
		return false;
	}
}