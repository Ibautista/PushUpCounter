package com.example.push.up.counter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.app.Activity;

public class PushUpCounter extends Activity implements SensorEventListener {

	private static final int UPDATE_THRESHOLD = 500;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private long mLastUpdate;

	private ViewFlipper mFlipper;
	private TextView mTextView1, mTextView2;
	private int mCurrentLayoutState, mCount;

	// Axis triggers
	private int mCurrentAxis, mNewAxis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize values for UI
		setContentView(R.layout.main);
		mCurrentLayoutState = 0;
		mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		mTextView1 = (TextView) findViewById(R.id.textView1);
		mTextView2 = (TextView) findViewById(R.id.textView2);
		mTextView1.setText(String.valueOf(mCount));
		mCurrentAxis = 0;
		mNewAxis = 0;

		// Get reference to SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get reference to Accelerometer
		if (null == (mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)))
			finish();

	}

	@Override
	protected void onResume() {
		// Register listeners
		super.onResume();

		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);

		mLastUpdate = System.currentTimeMillis();
	}

	@Override
	protected void onPause() {
		// Unregister listeners
		mSensorManager.unregisterListener(this);
		super.onPause();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Update Sensor values and increment mCount

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			long actualTime = System.currentTimeMillis();

			if (actualTime - mLastUpdate > UPDATE_THRESHOLD) {

				mLastUpdate = actualTime;

				// Implement push-up evaluator method
				// Switch UI only when true push-up
				// Incorporated into main thread to avoid UI Thread issues
				
				// Boolean evaluator if a true rep is reached
				// For loop for index of max value
				// Find max value from absolute value(events) list (retrieve index)
				// Max value determines what axis gravity is on
				// current axis vs. new axis
				// conditional if statement
				// if current = x and new axis = y then true
				// otherwise set new axis to current axis and return false
				// If Index == 0, gravity on X axis
				// If Index == 1, gravity on Y axis
				// If Index == 2, gravity on z axis

				float maxValue = 0;
				int maxIndex = 0;
				for (int i = 0; i < 3; i++) { // Don't like the hard-coded 3
					float eventValue = Math.abs(event.values[i]);
					if (eventValue > maxValue) {
						maxIndex = i + 1;
						maxValue = eventValue;
					}
				}

				mNewAxis = maxIndex;
				
				// True push-up = X axis >> Y axis movement
				// Decent logic for rotational moves
				// Dictionary:  X = 0 index + 1 / Y = 1 index + 1 / Z = 2 index + 1
				if (mCurrentAxis == 1 && mNewAxis == 2) {
					mCurrentLayoutState = mCurrentLayoutState == 0 ? 1 : 0;
					switchLayoutStateTo(mCurrentLayoutState);
					mCurrentAxis = 0;
					mNewAxis = 0;							
				} else {
					mCurrentAxis = mNewAxis;
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// No need to change this method
	}
	
	// Animation Methods

	public void switchLayoutStateTo(int switchTo) {
		mCurrentLayoutState = switchTo;

		mFlipper.setInAnimation(inFromRightAnimation());
		mFlipper.setOutAnimation(outToLeftAnimation());

		mCount++;

		if (switchTo == 0) {
			mTextView1.setText(String.valueOf(mCount));
		} else {
			mTextView2.setText(String.valueOf(mCount));
		}

		mFlipper.showPrevious();
	}

	private Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new LinearInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new LinearInterpolator());
		return outtoLeft;
	}
}
