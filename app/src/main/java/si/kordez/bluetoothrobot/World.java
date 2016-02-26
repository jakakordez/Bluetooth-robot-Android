package si.kordez.bluetoothrobot;

import android.app.Notification;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pManager;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.microedition.khronos.opengles.GL10;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class World {
    long startTime;
    float[] viewMatrix;
    MeshCollection meshCollector;
    Robot currentRobot;
    public World(AssetManager content, GL10 gl, String path){
        meshCollector = new MeshCollection(content);
        currentRobot = new Robot("robot", meshCollector, gl);
        viewMatrix = new float[16];
        startTime = System.currentTimeMillis();
    }

    public void Draw(GL10 gl, float[] acc){
        currentRobot.Control();
        Vector3 pos = ExtractTranslation(currentRobot.bodyOffset);

        gl.glLoadIdentity();
        GLU.gluLookAt(gl, currentRobot.cameraPosition.X, currentRobot.cameraPosition.Y, currentRobot.cameraPosition.Z, pos.X, pos.Y, pos.Z, 0, 1, 0);
        currentRobot.Draw(gl, meshCollector);

        gl.glLoadIdentity();
        GLU.gluLookAt(gl, currentRobot.cameraPosition.X, currentRobot.cameraPosition.Y, currentRobot.cameraPosition.Z, pos.X, pos.Y, pos.Z, 0, 1, 0);

    }

    public static Vector3 ExtractTranslation(float[] m){
        return new Vector3(m[12], m[13], m[14]);
    }

    public static int LoadTexture(String path, GL10 gl, AssetManager content){
        try {
            int r[] = new int[1];
            gl.glGenTextures(1, r, 0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, r[0]);

            // Create Nearest Filtered Texture
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                    GL10.GL_LINEAR);
/*
// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                    GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                    GL10.GL_REPEAT);*/

            Bitmap bmp = BitmapFactory.decodeStream(content.open(path));

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
            return r[0];
        }
        catch(IOException e){}
        return 0;
    }

    private static Vector2 CartesianToPolar(Vector2 t)
    {
        //return new Tocka(Math.Atan(t.x / t.y), Math.Sqrt((t.x * t.x) + (t.y * t.y)));

        Vector2 a = new Vector2((float)Math.atan(-t.Y/t.X), (float)Math.sqrt((t.X * t.X) + (t.Y * t.Y)));
        if (t.X < 0)a.X += Math.PI;
        else if (t.Y < 0) a.X += Math.PI * 2;
        return a;
    }
}
