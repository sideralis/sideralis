package fr.dox.sideralis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PositionActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Position: onCreate");
		setContentView(R.layout.position);
		final SharedPreferences myPref = getSharedPreferences("Settings",Context.MODE_PRIVATE);
		String lonPosition = myPref.getString("Longitude","0");
		String latPosition = myPref.getString("Latitude","0");
		
		final EditText lon = (EditText) findViewById(R.id.EditTextLongitude);
		final EditText lat = (EditText) findViewById(R.id.EditTextLatitude);
		lon.setText(lonPosition);
		lat.setText(latPosition);
		Button b = (Button) findViewById(R.id.ButtonPositionOK); 
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = lon.getText().toString();
				System.out.println("Longitude = "+s);
				SharedPreferences.Editor e = myPref.edit();
				e.putString("Longitude", s);
				
				s = lat.getText().toString();
				System.out.println("Latitude = "+s);
				e.putString("Latitude",s);
				e.commit();
				
				setResult(RESULT_OK);
				finish();
			}
		});

	}
    /* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("Position: onDestroy");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("Position: onPause");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println("Position: onRestart");

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("Position: onResume");
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("Position: onStart");
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("Position: onStop");		
	}


}
