package si.kordez.bluetoothrobot;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class Mesh {
    BufferedObject[] obj;
    ArrayList<Material> materials;
    ArrayList<Integer> indexBufferSizes;
    private FloatBuffer vertexBuffer, textureCoordinateBuffer;
    ArrayList<CharBuffer> indexBuffer;
    public Mesh(String filename, AssetManager manager, GL10 gl){
        materials = new ArrayList<Material>();
        try {
            String file = "";

            InputStream iS = manager.open(filename+".mesh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));

            file = reader.readLine();

            loadMtlFile(filename, manager, gl);
            String[] data = file.split(";");// File.ReadAllText(filename).Split(';');
            String[] vertices = data[0].split(" ");
            String[] texcoords = data[1].split(" ");
            float[] SortedVertices = new float[vertices.length];
            for (int i = 0; i < vertices.length; i++)
            {
                SortedVertices[i] = Float.parseFloat(vertices[i]);
            }

            // Load texture coordinates
            float[] TexCoords = new float[texcoords.length];

            for (int i = 0; i < texcoords.length; i++)
            {
                //SortedTextureCoordinates[i / 2] = new Vector2(Float.parseFloat(texcoords[i]), Float.parseFloat(texcoords[i + 1]));
                TexCoords[i] = Float.parseFloat(texcoords[i]);

            }
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(texcoords.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            textureCoordinateBuffer = byteBuf.asFloatBuffer();
            textureCoordinateBuffer.put(TexCoords);
            textureCoordinateBuffer.position(0);

            int bufferSize;
            indexBuffer = new ArrayList<CharBuffer>();
            indexBufferSizes = new ArrayList<Integer>();
            //GL.GenBuffers(Materials.Length, ElementArrays);
            for (int i = 0; i < data.length-2; i++)
            {
                if (data[i + 2] != "")
                {
                    String[] indicies = data[i + 2].split(" ");
                    char[] currentElements = new char[indicies.length];
                    for (int j = 0; j < indicies.length; j++)
                    {
                        currentElements[j] = (char)Integer.parseInt(indicies[j]);
                    }
                    indexBufferSizes.add(currentElements.length);//Misc.Push<int>(currentElements.Length, ref ElementArraySizes);
                    // short is 2 bytes, therefore we multiply the number if
                    // vertices with 2.

                    ByteBuffer ibb = ByteBuffer.allocateDirect(currentElements.length * 2);
                    ibb.order(ByteOrder.nativeOrder());
                    CharBuffer ib = ibb.asCharBuffer();
                    ib.put(currentElements);
                    ib.position(0);

                    indexBuffer.add(ib);

                }
            }

            ByteBuffer vbb = ByteBuffer.allocateDirect(SortedVertices.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            vertexBuffer = vbb.asFloatBuffer();
            vertexBuffer.put(SortedVertices);
            vertexBuffer.position(0);
        }
        catch(IOException e){}
    }

    public void loadMtlFile(String filename, AssetManager content, GL10 gl)
    {
        Material currentMaterial = null;
        try {
            InputStream iS = content.open(filename+".mtl");
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));

            String ln = null;
            while ((ln = reader.readLine()) != null) {
                String[] line = ln.split(" ");
                switch (line[0]) {
                    case "newmtl":
                        if (currentMaterial != null)
                            materials.add(currentMaterial);//Misc.Push<Material>(currentMaterial, ref materials);
                        currentMaterial = new Material(line[1]);
                        break;
                    case "Kd":
                        if (currentMaterial != null) {
                            currentMaterial.Brush = new Color4(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
                        }
                        break;
                    case "map_Kd":
                        if(currentMaterial != null)
                        {
                            String[] flnm = filename.replace('\\', '/').split("/");
                            String result = "";
                            for (int j = 0; j < flnm.length - 1; j++) result += flnm[j] + "/";
                            currentMaterial.Texture = World.LoadTexture(result + line[1], gl, content);
                            currentMaterial.TexturePath = result + line[1];
                        }
                        break;
                }
            }
            if (currentMaterial != null)materials.add(currentMaterial);//Misc.Push<Material>(currentMaterial, ref materials);
        }
        catch(IOException e){}
    }

    public void Draw(GL10 gl, int color)
    {
        gl.glEnable(GL10.GL_TEXTURE_2D);

        gl.glFrontFace(GL10.GL_CCW);
        // Enable face culling.
        //gl.glEnable(GL10.GL_CULL_FACE);
        // What faces to remove with the face culling.
        gl.glCullFace(GL10.GL_BACK);
        // Enabled the vertices buffer for writing and to be used during
        // rendering.

        // Specifies the location and data format of an array of vertex
        // coordinates to use when rendering.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        for (int i = 0; i < indexBufferSizes.size(); i++)
        {
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            if (materials.get(i).Texture != 0)
            {
                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                gl.glColor4f(1, 1, 1, 1);

                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoordinateBuffer);
                gl.glBindTexture(GL10.GL_TEXTURE_2D, materials.get(i).Texture);
            }
            else
            {
                switch (color){
                    case 0: materials.get(i).Brush.set(gl); break;
                    case 1: gl.glColor4f(1, 1.004f, 0.013f, 1); break;
                    case 2: gl.glColor4f(0.462f, 1, 0.013f, 1); break;
                }

                gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
            }

            gl.glDrawElements(GL10.GL_TRIANGLES, indexBufferSizes.get(i), GL10.GL_UNSIGNED_SHORT, indexBuffer.get(i));
            if (materials.get(i).Texture != 0) gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
        // Disable the vertices buffer.


        gl.glDisable(GL10.GL_TEXTURE_2D);
        // Disable face culling.
        gl.glDisable(GL10.GL_CULL_FACE);
    }
}