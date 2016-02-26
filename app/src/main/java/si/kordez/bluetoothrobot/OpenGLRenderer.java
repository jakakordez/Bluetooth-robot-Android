package si.kordez.bluetoothrobot;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.provider.ContactsContract;
import android.view.MotionEvent;

public class OpenGLRenderer implements Renderer, SensorEventListener {
    World currentWorld;
    AssetManager content;
    float[] accData;
    SensorManager mSensorManager;
    Sensor acc;
    public OpenGLRenderer(AssetManager Content, SensorManager sensorManager){
        content = Content;
        accData = new float[3];
        mSensorManager = sensorManager;
        acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accData[0] = event.values[0];
        accData[1] = event.values[1];
        accData[2] = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        // Set the background color to black ( rgba ).
        gl.glClearColor(0.33f, 0.66f, 1.0f, 0.5f);
        // Enable Smooth Shading, default not really needed.
        gl.glShadeModel(GL10.GL_SMOOTH);
        // Depth buffer setup.
        gl.glClearDepthf(1.0f);
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // The type of depth testing to do.
        gl.glDepthFunc(GL10.GL_LEQUAL);
        // Really nice perspective calculations.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_BLEND);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ALPHA);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);
        currentWorld = new World(content, gl, "path1");
    }

    public void onDrawFrame(GL10 gl) {
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        currentWorld.Draw(gl, accData);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Sets the current view port to the new size.
        gl.glViewport(0, 0, width, height);
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        gl.glLoadIdentity();
        // Calculate the aspect ratio of the window
        GLU.gluPerspective(gl, 80.0f,
                (float) width / (float) height,
                1f, 2500.0f);

        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        gl.glLoadIdentity();
    }
}