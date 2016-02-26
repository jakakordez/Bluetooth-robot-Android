package si.kordez.bluetoothrobot;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class Color4 {
    public float A, R, G, B;

    public Color4(){
        A = 1;
    }


    public Color4(float R, float G, float B){
        this.A = 1f;
        this.R = R;
        this.G = G;
        this.B = B;
    }


    public void set(GL10 gl){
        gl.glColor4f(R, G, B, A);
    }
}