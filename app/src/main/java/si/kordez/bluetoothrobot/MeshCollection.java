package si.kordez.bluetoothrobot;

import android.content.res.AssetManager;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class MeshCollection {
    ArrayList<Mesh> meshes;
    AssetManager cnt;
    ArrayList<String> paths;
    public MeshCollection(AssetManager content){
        cnt = content;
        meshes = new ArrayList<Mesh>();
        paths = new ArrayList<String>();
    }

    public int Import(String path, GL10 gl){
        for(int i = 0; i < paths.size(); i++){
            if(paths.get(i) == path) return i;
        }
        meshes.add(new Mesh(path, cnt, gl));
        return meshes.size()-1;
    }

    public void Draw(int index, GL10 gl){
        meshes.get(index).Draw(gl, 0);
    }
    public void Draw(int index, GL10 gl, int stage){
        meshes.get(index).Draw(gl, stage);
    }
}