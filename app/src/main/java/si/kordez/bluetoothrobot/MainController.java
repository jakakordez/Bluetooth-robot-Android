package si.kordez.bluetoothrobot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

public class MainController extends Activity {

    BluetoothInterface myConnection;
    OpenGLRenderer glRenderer;
    Point screenSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);

        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice d = mBluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("address"));
        myConnection = new BluetoothInterface(d);

        GLSurfaceView view = new GLSurfaceView(this);
        glRenderer = new OpenGLRenderer(getResources().getAssets(), (SensorManager)getSystemService(SENSOR_SERVICE));

        view.setRenderer(glRenderer);
        setContentView(view);
    }

    private void SetMotorSpeed(int motorNumber, int Speed){
        byte[] OutputBuffer = new byte[5];
        OutputBuffer[0] = (byte)0xAA;
        OutputBuffer[1] = (byte) Speed;
        OutputBuffer[3] = (byte)motorNumber;
        OutputBuffer[4] = (byte)0x55;
        myConnection.write(OutputBuffer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        return true;
    }
}
