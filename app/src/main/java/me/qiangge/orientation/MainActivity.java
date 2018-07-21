package me.qiangge.orientation;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;
import site.gemus.openingstartanimation.RotationDrawStrategy;

import static android.view.OrientationEventListener.ORIENTATION_UNKNOWN;
//记得把对应的activity 的方向设置为 android:configChanges="orientation|keyboardHidden|screenSize|navigation"
//        SCREEN_ORIENTATION_UNSPECIFIED 根据系统（系统禁用重力感应就会锁定方向，反之方向跟随重力感应）
//        SCREEN_ORIENTATION_LANDSCAPE  水平
//        SCREEN_ORIENTATION_PORTRAIT 竖直
//        SCREEN_ORIENTATION_USER
//        SCREEN_ORIENTATION_BEHIND
//        SCREEN_ORIENTATION_SENSOR 根据重力感应的方向
//        SCREEN_ORIENTATION_NOSENSOR
//        SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//        SCREEN_ORIENTATION_SENSOR_PORTRAIT
//        SCREEN_ORIENTATION_REVERSE_LANDSCAPE  反向的水平
//        SCREEN_ORIENTATION_REVERSE_PORTRAIT 反向的竖直
//        SCREEN_ORIENTATION_FULL_SENSOR
//        SCREEN_ORIENTATION_USER_LANDSCAPE
//        SCREEN_ORIENTATION_USER_PORTRAIT
//        SCREEN_ORIENTATION_FULL_USER
//        SCREEN_ORIENTATION_LOCKED
public class MainActivity extends AppCompatActivity {
    Sensor sensor;
    SensorManager mSensorManager;
    SensorEventListener mSensorListener;
    final int _DATA_X = 0;
    final int _DATA_Y = 1;
    final int _DATA_Z = 2;


    @Override
    protected void onResume() {
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new RotationDrawStrategy()) //设置动画效果
                .setAnimationFinishTime(1000) // 设置动画的消失时长
                .setAppStatement("loading...") //设置一句话描述
                .create();
        openingStartAnimation.show(this);
        mSensorListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values = event.values;
                int orientation = ORIENTATION_UNKNOWN;
                float X = -values[_DATA_X];
                float Y = -values[_DATA_Y];
                float Z = -values[_DATA_Z];
                float magnitude = X * X + Y * Y;
                // Don't trust the angle if the magnitude is small compared to the y value
                if (magnitude * 4 >= Z * Z) {
                    float OneEightyOverPi = 57.29577957855f;
                    float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                    orientation = 90 - (int) Math.round(angle);
                    // normalize to 0 - 359 range
                    while (orientation >= 360) {
                        orientation -= 360;
                    }
                    while (orientation < 0) {
                        orientation += 360;
                    }
                }
                Log.d("johnny", "orientation:" + orientation);
                if (orientation > 45 && orientation < 135) {
//                  getActivity().setRequestedOrientation(8);
                    //根据系统来决定屏幕旋转的方向
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                } else if (orientation > 135 && orientation < 225) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
//                  getActivity().setRequestedOrientation(9);
                } else if (orientation > 225 && orientation < 315) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//                  getActivity().setRequestedOrientation(0);
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
//                  getActivity().setRequestedOrientation(1);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert mSensorManager != null;
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorListener, sensor, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}
