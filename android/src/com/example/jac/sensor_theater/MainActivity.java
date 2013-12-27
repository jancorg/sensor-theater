package com.example.jac.sensor_theater;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.Menu;
import android.widget.LinearLayout;


import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphView.GraphViewData;


import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;


public class MainActivity extends Activity {

	private final Handler mHandler = new Handler();
	
	private static final String DEVICE_ADDRESS =  "98:D3:31:20:11:7B";
	private double time_X = 0.0;

	private GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
    	      new GraphViewData(0.0, 0)
    	     
    	      
    	});	

	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
	
	
	/**
	 * ArduinoReceiver is responsible for catching broadcasted Amarino
	 * events.
	 * 
	 * It extracts data from the intent and updates the graph accordingly.
	 */
	public class ArduinoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String data = null;

			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			double sensor_data = 0;
			
			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support differnt data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				time_X += 0.1;
				if (data != null){
	/*				try {
						// since we know that our string value is an int number we can parse it to an integer
	*/					sensor_data = Double.parseDouble(data);
	/*					
					}
					catch (NumberFormatException e) { }
    */				exampleSeries.appendData( new GraphViewData(time_X, sensor_data), true, 10);
				}
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GraphView graphView = new LineGraphView(
			      this // context
			      , "GraphView");
		
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setViewPort(1, 60);
		graphView.addSeries(exampleSeries);

		graphView.setBackgroundColor(Color.rgb(80, 30, 30));
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// in order to receive broadcasted intents we need to register our receiver
		registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
		
		// this is how you tell Amarino to connect to a specific BT device from within your own code
		Amarino.connect(this, DEVICE_ADDRESS);
		
	}


	@Override
	protected void onStop() {
		super.onStop();
		
		// if you connect in onStart() you must not forget to disconnect when your app is closed
		Amarino.disconnect(this, DEVICE_ADDRESS);
		
		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoReceiver);
	}
	

}
