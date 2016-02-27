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
import android.opengl.Matrix;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainController extends Activity {

    BluetoothInterface myConnection;
    OpenGLRenderer glRenderer;
    Point screenSize;
    Timer t;
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
        while(myConnection.myConnection.connected == null);
        GLSurfaceView view = new GLSurfaceView(this);
        glRenderer = new OpenGLRenderer(getResources().getAssets(), (SensorManager)getSystemService(SENSOR_SERVICE));

        view.setRenderer(glRenderer);
        setContentView(view);

        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 0);

    }
    android.os.Handler customHandler;
    private Runnable updateTimerThread = new Runnable()
    {
        byte[] buffer = new byte[20];
        int i = 0;
        public void run()
        {
            if (myConnection.myConnection.connected != null) {
                buffer[i] = myConnection.readByte();
                if ((i == 0 && buffer[i] != (byte)0xAB) || (i == 5 && buffer[i] != (byte)0x56) || i > 5) {
                    for (int j = 0; j < 6; j++) buffer[j] = 0;
                    i = 0;
                } else if (buffer[i] == (byte)0x56 && i == 5) {
                    glRenderer.currentWorld.currentRobot.SetLocation(((float) toInt(buffer, 1)) / 10f, 0);
                    for (int j = 0; j < 6; j++) buffer[j] = 0;
                    i = 0;
                } else i++;
            }
            //write here whaterver you want to repeat
            customHandler.postDelayed(this, 1);
        }
    };


    public static int toInt(byte[] bytes, int offset) {
        int ret = 0;
        for (int i=0; i<4 && i+offset<bytes.length; i++) {
            ret <<= 8;
            ret |= (int)bytes[i+offset] & 0xFF;
        }
        return ret;
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
        float half = screenSize.y/2;
        float speed = -127*((e.getY()-half)/half);
        SetMotorSpeed(0, (int)speed);
        SetMotorSpeed(1, (int)speed);
        return true;
    }
}
