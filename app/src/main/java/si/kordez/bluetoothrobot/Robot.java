package si.kordez.bluetoothrobot;

import android.graphics.Point;
import android.opengl.Matrix;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class Robot {
    int body;
    public float[] bodyOffset, cameraOffset;
    public Vector3 cameraPosition;
    public Robot(String filename, MeshCollection content, GL10 gl){
        body = content.Import(filename+"/body", gl);

        bodyOffset = new float[16];
        Matrix.setIdentityM(bodyOffset, 0);

        cameraOffset = new float[16];
        Matrix.setIdentityM(cameraOffset, 0);
        Matrix.translateM(cameraOffset, 0, 0, 2, -5);
    }

    public void Draw(GL10 gl, MeshCollection content){
        gl.glMultMatrixf(bodyOffset, 0);
        content.Draw(body, gl);
    }

    public void SetLocation(float x, float y){
        Matrix.setIdentityM(bodyOffset, 0);
        Matrix.translateM(bodyOffset, 0, x, 0, y);
        Matrix.rotateM(bodyOffset, 0, -90, 0, 1, 0);
    }

    public void Control(){
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, bodyOffset, 0, cameraOffset, 0);
        cameraPosition = World.ExtractTranslation(temp);
    }
}
