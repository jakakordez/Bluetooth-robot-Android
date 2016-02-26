package si.kordez.bluetoothrobot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConnectionMenu extends AppCompatActivity {
    ListView lstDevices;
    ArrayAdapter<DeviceInfo> devicesAdapter;
    TextView lblStatus;
    Button btnSearch;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_menu);

        lstDevices = (ListView)findViewById(R.id.lstDevices);
        ArrayList<DeviceInfo> s = new ArrayList<>();
        devicesAdapter = new ArrayAdapter<DeviceInfo>(this, android.R.layout.simple_list_item_1, android.R.id.text1, s);
        lstDevices.setAdapter(devicesAdapter);
        lblStatus = (TextView)findViewById(R.id.lblStatus);

        lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothAdapter.cancelDiscovery();
                Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(ConnectionMenu.this, MainController.class);
                myIntent.putExtra("address", ((DeviceInfo)lstDevices.getItemAtPosition(i)).Naslov);

                ConnectionMenu.this.startActivity(myIntent);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            lblStatus.setText("Device does not support Bluetooth");
        }
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);

            btnSearch = (Button) findViewById(R.id.btnSearch);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBluetoothAdapter.cancelDiscovery();
                    lblStatus.setText("Searching");
                    devicesAdapter.clear();
                    mBluetoothAdapter.startDiscovery();
                }
            });
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                devicesAdapter.add(new DeviceInfo(device.getName(), device.getAddress()));
            }
        }
    };
    // Register the BroadcastReceiver


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class DeviceInfo{
    public String Ime, Naslov;
    public DeviceInfo(String Ime, String Naslov){this.Ime = ((Ime == null)?"Brez imena":((Ime.length() > 12)?Ime.substring(0, 9)+"...":Ime)); this.Naslov = Naslov;}
    public String toString(){return Ime+"\n"+Naslov;}
}
