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
import android.widget.TextView;



import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphView.GraphViewData;


import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;


#TODO: Replace Amarino libs

public class MainActivity extends Activity {

	
	private static final String DEVICE_ADDRESS =  "DE:AD:BE:EF:CA:FE";
	private double time_X = 0;

	private GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
    	      new GraphViewData(0, 0)
    	     
    	      
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
			
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				time_X += 1;
				if (data != null){
				try {
					sensor_data = Integer.parseInteger(data);
					}
				catch (NumberFormatException e) { }
				exampleSeries.appendData( new GraphViewData(time_X, sensor_data), true, 10);
    				TextView debug=(TextView)findViewById(R.id.textView1);
    				debug.setText(data);
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
		Amarino.connect(this, DEVICE_ADDRESS);
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		Amarino.disconnect(this, DEVICE_ADDRESS);
		unregisterReceiver(arduinoReceiver);
	}
	

}
