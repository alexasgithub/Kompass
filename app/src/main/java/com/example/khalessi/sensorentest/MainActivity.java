package com.example.khalessi.sensorentest;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private LinearLayout ll_basic;
    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

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

    /**
     * Die einzelnen Views werden geholt und
     * der SensorManager initializiert.
     * Sensoren werden ausgelesen.
     * Falls kein Magnetsensor im Smarphone vorhanden ist,
     * erfolgt eine Meldung an den User.
     *
     * @param savedInstanceState
     */
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
      //Test, ob Sensor.TYPE_MAGNETIC_FIELD existiert, sonst eine Meldung an den User
       if (mMagnetometer == null) {
           Toast.makeText(getApplicationContext(), "Sie haben keinen Sensor.TYPE_MAGNETIC_FIELD", Toast.LENGTH_LONG).show();

        }


    }

    /**
     * Beim (wiederholten) Starten werden Listener für die Sensoren registriert.
     */
    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);

    }

    /**
     * Beim Pausieren werden die Listener für die Sensoren entfernt.
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, mAccelerometer);
        sensorManager.unregisterListener(this, mMagnetometer);

    }


    /**
     * Liest die Daten des Accelorometers aus und
     * zeigt sie in den Textviews an.
     * Überprüft die Beschleunigung und falls der Schwellwert von 2
     * überschritten wird, erfolgt die Änderung der Hintergrundfarbe
     *
     * @param event
     */
    private void getAccelerometerData(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = Math.round(values[0]);
        float y = Math.round(values[1]);
        float z = Math.round(values[2]);


        tvSensorXVal.setText(" X: " + x);
        tvSensorYVal.setText(" / Y: " + y);
        tvSensorZVal.setText(" / Z: " + z);
        ll_basic.setBackgroundResource(R.drawable.europa);
        //Der Vektor ist die Beschleunigung. √(x2 + y2 + z2) ist der Betrag der Beschleunigung.
        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2)
        {
            if (actualTime - lastUpdate < 2000) {
                return;
            }
            lastUpdate = actualTime;

            if (color) {
                ll_basic.setBackgroundResource(R.drawable.europa);
            } else {
                ll_basic.setBackgroundColor(Color.RED);
            }
            color = !color;
        }

    }


    /**
     * Notwendig zur Implementierung des Interfaces SensorEventListener
     * @param sensor
     * @param i
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //TODO Reaktion auf Änderung der Genauigkeit des Sensors
    }

    /**
     * Prüft welcher Sensor die Daten liefert und verzweigt entsprechend.
     * Startet die Rotationsanimation für den Kompass.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
            getAccelerometerData(event);
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;


        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {



            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            compassAngle.setText("Azimuth: " + ((int) Math.round(azimuthInDegress)+"°"));


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


}
