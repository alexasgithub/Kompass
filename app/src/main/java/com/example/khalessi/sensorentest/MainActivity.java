package com.example.khalessi.sensorentest;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private LinearLayout ll_basic;
    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mProximity;

    private TextView tvSensorXVal, tvSensorYVal, tvSensorZVal;
    private TextView compassAngle;

    private boolean color = false;
    private long lastUpdate;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    private ImageView mPointer;

  //  private float currentDegree = 0f;
    public static String TAG = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        lastUpdate = System.currentTimeMillis();

        ll_basic = (LinearLayout) findViewById(R.id.ll_basic);

        tvSensorXVal = (TextView) findViewById(R.id.tvSensorXVal);
        tvSensorYVal = (TextView) findViewById(R.id.tvSensorYVal);
        tvSensorZVal = (TextView) findViewById(R.id.tvSensorZVal);
        compassAngle = (TextView) findViewById(R.id.angle);
        mPointer = (ImageView) findViewById(R.id.imageViewCompass);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
     /*   if (mMagnetometer != null) {
            sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }*/


    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors

        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);


     /*   mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);

// Create listener
        SensorEventListener proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // More code goes here
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(mProximity == null) {
            Log.e(TAG, "Proximity sensor not available.");
           
        }
        // Register it, specifying the polling interval in
// microseconds
        sensorManager.registerListener(proximitySensorListener,
                mProximity, 2 * 1000 * 1000);
*/

    }




    /**
     *
     * @param event
     */
    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = Math.round(values[0]);
        float y = Math.round(values[1]);
        float z = Math.round(values[2]);


        tvSensorXVal.setText("X: " + x);
        tvSensorYVal.setText(" / Y: " + y);
        tvSensorZVal.setText(" / Z: " + z);
        ll_basic.setBackgroundResource(R.drawable.europa);
        //Der Vektor ist die Beschleunigung. √(x2 + y2 + z2) ist der Betrag der Beschleunigung.
        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            if (color) {
                ll_basic.setBackgroundColor(Color.WHITE);
            } else {
                ll_basic.setBackgroundColor(Color.RED);
            }
            color = !color;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //sensorManager.unregisterListener(this);
        sensorManager.unregisterListener(this, mAccelerometer);
        sensorManager.unregisterListener(this, mMagnetometer);

      //  sensorManager.unregisterListener(proximitySensorListener);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_compass, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        //     if (id == R.id.action_settings) {
        //        return true;
        //   }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
            getAccelerometer(event);
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
          //  getCompass(event);
      /*      float xv = Math.round(event.values[0]);
            float yv = Math.round(event.values[1]);
            float zv = Math.round(event.values[2]);
            float degree = Math.round(xv);

            compassAngle.setText("Ausrichtung X: " + Float.toString(xv) + " Y:" + Float.toString(yv) + " Z: " + Float.toString(zv));
*/

        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {



            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }

    }

    private void getCompass(SensorEvent event) {
        //float degree = Math.round(event);

        float xv = Math.round(event.values[0]);
        float yv = Math.round(event.values[1]);
        float zv = Math.round(event.values[2]);
        float degree = Math.round(xv);

        compassAngle.setText("Ausrichtung X: " + Float.toString(xv) + " Y:" + Float.toString(yv) + " Z: " + Float.toString(zv));

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(mCurrentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        // Start the animation
        mPointer.startAnimation(ra);
        mCurrentDegree = -degree;
    }

}
