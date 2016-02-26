package si.kordez.bluetoothrobot;

/**
 * Created by jakak on 26. 02. 2016.
 */
public class Material
{
    public Color4 Brush;
    public String Name, TexturePath;
    public int Texture;
    public Material(String name)
    {
        Name = name;
        Brush = new Color4();
        Texture = 0;
    }
}