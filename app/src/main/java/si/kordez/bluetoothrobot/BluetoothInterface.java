package si.kordez.bluetoothrobot;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Jaka on 30.7.2014.
 */
public class BluetoothInterface {
    ConnectThread myConnection;
    public BluetoothInterface(BluetoothDevice device)
    {
        myConnection = new ConnectThread(device);
        myConnection.run();
    }

    public void write(byte[] data)
    {
        if(myConnection != null)myConnection.connected.write(data);
    }

    public byte readByte()
    {
        return myConnection.connected.readByte();
    }

    public void cancel(){
        myConnection.cancel();
    }
}

class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    public ConnectedThread connected;
    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        // manageConnectedSocket(mmSocket);
        connected = new ConnectedThread(mmSocket);
        connected.start();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
            connected.cancel();
        } catch (IOException e) { }
    }
}

class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

// Get the input and output streams, using temp objects because
// member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {

    }

    public void send(byte[] data)
    {
        try {
            mmOutStream.write(data);
        }
        catch(Exception e){}
    }

    public byte readByte()
    {
        try {
            return (byte) mmInStream.read();
        }
        catch(Exception e){}
        return 0;
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}
