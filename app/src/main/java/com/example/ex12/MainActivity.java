package com.example.ex12;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;



public class MainActivity extends Activity {

	private final static int MAX = 1000;
	private TextView counter;
	private int count = MAX;
	private ImageButton egg;
	private SharedPreferences prefs;
	private MediaPlayer mp;
	private long startTime;
	private long endTime;
	private long deltaTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		counter = ((TextView)findViewById(R.id.tvCounter));
		egg = ((ImageButton)findViewById(R.id.btnEgg));
		
		mp = MediaPlayer.create(this, R.raw.eggcrack);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		count = prefs.getInt("counter", MAX);
		counter.setText(""+count);
		deltaTime = prefs.getLong("time", 0);
		
		if ((count-1) <= (MAX/2))
		{
			egg.setImageResource(R.drawable.broken);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		prefs.edit().putInt("counter", this.count).commit();
		deltaTime += (System.currentTimeMillis() - startTime);
		prefs.edit().putLong("time", deltaTime).commit();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		startTime = System.currentTimeMillis();
		//boolean ans = prefs.getBoolean("only_portrait", false);
		//Settings.System.putInt( this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, ans ? 1 : 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			getFragmentManager().beginTransaction().replace(android.R.id.content, new MyFragment()).addToBackStack(null).commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void touches(View v)
	{
		int flag = 0;
		Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);//Initiate the vibrate service
		vib.vibrate(100);
		
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		v.startAnimation(shake);
		count = Integer.parseInt(counter.getText().toString());
		
		if (count == MAX)
		{
			startTime = System.currentTimeMillis();
		}
		
		if ((count-1) <= (MAX/2))
		{
			egg.setImageResource(R.drawable.broken);
			if (flag < 1)
			{	
				mp.start();
				flag++;
			}
		}
		
		
		if (count == 1)
		{
			endTime = System.currentTimeMillis();
			counter.setText((((endTime-startTime)+deltaTime)/1000)+ " seconds");
			egg.setEnabled(false);
		}
		else
		{
			count -= 1;
			counter.setText(""+count);
		}
	}
	
	public void restart(View v)
	{
		counter.setText(""+MAX);
		egg.setImageResource(R.drawable.start);
		egg.setEnabled(true);
		prefs.edit().putInt("counter", MAX).commit();
	}
	
	public static class MyFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
	{

		
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			
			addPreferencesFromResource(R.xml.preferences);
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
			//initSummary(getPreferenceScreen());
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
		{
				View v = super.onCreateView(inflater, container, savedInstanceState);
				v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
				return v;
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			
		}
		
	}
}
