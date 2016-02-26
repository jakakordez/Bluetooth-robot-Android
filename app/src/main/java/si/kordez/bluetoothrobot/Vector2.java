package si.kordez.bluetoothrobot;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class Vector2 {
    public float X, Y;

    public Vector2(float X, float Y){
        this.X = X;
        this.Y = Y;
    }

    public static Vector2 Zero(){
        return new Vector2(0, 0);
    }

    public float Distance(Vector2 v1){
        return (float) Math.sqrt(Math.pow(X-v1.X, 2)+Math.pow(Y-v1.Y, 2));
    }

    public static Vector2 Sub(Vector2 f, Vector2 s){
        return new Vector2(f.X-s.X, f.Y-s.Y);
    }
}