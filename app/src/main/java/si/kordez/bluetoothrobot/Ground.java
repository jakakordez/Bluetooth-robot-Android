package si.kordez.bluetoothrobot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class Ground {
    BufferedObject groundObject;
    // Our vertex buffer.
    private FloatBuffer vertexBuffer;
    int size;
    // Our index buffer.
    private ShortBuffer indexBuffer;
    public Ground(int segments, int width)
    {
        float[] Vertices = new float[segments*2*2*3];
        short[] indices = new short[segments*2*2];
        long half = (segments*width)/2;
        for(int i = 0; i < segments; i++){
            Vertices[(i*2*2*3)+0] = i*width-half;
            Vertices[(i*2*2*3)+2] = 0-half;
            Vertices[(i*2*2*3)+3] = i*width-half;
            Vertices[(i*2*2*3)+5] = segments*width-half;
            indices[(i*2*2)+0] = (short)((i*2*2)+0);
            indices[(i*2*2)+1] = (short)((i*2*2)+1);

            Vertices[(i*2*2*3)+6] = 0-half;
            Vertices[(i*2*2*3)+8] = i*width-half;
            Vertices[(i*2*2*3)+9] = segments*width-half;
            Vertices[(i*2*2*3)+11] = i*width-half;
            indices[(i*2*2)+2] = (short)((i*2*2)+2);
            indices[(i*2*2)+3] = (short)((i*2*2)+3);
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(Vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(Vertices);
        vertexBuffer.position(0);

        // short is 2 bytes, therefore we multiply the number if
        // vertices with 2.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
        size = indices.length;
    }

    public void Draw(GL10 gl) {
        gl.glColor4f(1, 1, 1, 1);

        // Enabled the vertices buffer for writing and to be used during
        // rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Specifies the location and data format of an array of vertex
        // coordinates to use when rendering.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
                vertexBuffer);

        gl.glDrawElements(GL10.GL_LINES, size,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // Disable face culling.
        // gl.glDisable(GL10.GL_CULL_FACE);
    }
}
