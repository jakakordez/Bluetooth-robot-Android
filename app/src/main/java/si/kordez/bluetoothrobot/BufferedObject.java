package si.kordez.bluetoothrobot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class BufferedObject {
    // Our vertex buffer.
    private FloatBuffer vertexBuffer;
    int size;
    // Our index buffer.
    private ShortBuffer indexBuffer;
    public BufferedObject(float[] Vertices, short[] indices)
    {
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

    public void Draw(GL10 gl)
    {
        // Enabled the vertices buffer for writing and to be used during
        // rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Specifies the location and data format of an array of vertex
        // coordinates to use when rendering.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
                vertexBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLES, size,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // Disable face culling.
        // gl.glDisable(GL10.GL_CULL_FACE);
    }
}