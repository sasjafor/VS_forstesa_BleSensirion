package ch.ethz.inf.vs.a1.forstesa.ble;

import android.Manifest;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.os.SystemClock.sleep;

// import static ch.ethz.inf.vs.a1.forstesa.ble.ViewHolder.myBluetoothAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Check if BLE is supported:
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Toast.makeText(this, R.string.ble_supported, Toast.LENGTH_SHORT).show();
        }

        // Initialize myBluetoothAdapter:
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        myBluetoothAdapter = bluetoothManager.getAdapter();

        // Enabling Bluetooth:
        if (myBluetoothAdapter == null || !myBluetoothAdapter.isEnabled()) {
            // Dialog
            DialogFragment dialog = new EnableDialogFragment();
            dialog.show(this.getSupportFragmentManager(), "bluetooth");
        }
        else {
            Toast.makeText(this, R.string.bt_enabled, Toast.LENGTH_SHORT).show();
        }
        //TODO: The same kind of dialog but for location services.

        // 4. Permissions:
        System.out.println("DEBUG: permissions");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            DialogFragment permission_dialog = new PermissionDialogFragment();
            permission_dialog.show(this.getSupportFragmentManager(), "permission");
        }


        // 3. List devices:
        lv = (ListView) findViewById(R.id.devices_lv);
        devices_list = new ArrayList<String>();
        lv_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devices_list);
        lv.setAdapter(lv_adapter);
        lv.setEnabled(true);

        // 6. Open new activity:
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                scanLeDevice(false);
                Intent myIntent = new Intent(MainActivity.this, SensorActivity.class);
                String device = devices_list.get(i);
                String address = device.substring(device.length()-17);
                System.out.println("DEBUG: " + address);
                myIntent.putExtra("device_address", address);
                startActivity(myIntent);
            }
        });

        // Start scan by button. This gives enough time to show the dialog when bluetooth is disabled or the permissions not given.
        Button startScan = (Button) findViewById(R.id.start);
        startScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("DEBUG: click");
                if(!myBluetoothAdapter.isEnabled() && ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(v.getContext(), R.string.bt_disabled, Toast.LENGTH_SHORT).show();
                }
                else {
                    scanLeDevice(true);
                }
            }
        });

    }

    private void scanLeDevice(final boolean enable) {
        System.out.println("DEBUG scanLeDevice");
        final BluetoothLeScanner bluetoothLeScanner = myBluetoothAdapter.getBluetoothLeScanner();
        myHandler = new Handler();

        if (enable) {
            System.out.println("DEBUG scanLeDevice IF");
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myScanning = false;
                    bluetoothLeScanner.stopScan(myLeScanCallback);
                    System.out.println("DEBUG scanLeDevice stopScan return");
                }
            }, Scan_period);

            myScanning = true;
            bluetoothLeScanner.startScan(myLeScanCallback);
        }
        else {
            System.out.println("DEBUG scanLeDevice ELSE");
            myScanning = false;
            bluetoothLeScanner.stopScan(myLeScanCallback);
        }

        /*
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("DEBUG scanLeDevice ASYNC TASK");
                bluetoothLeScanner.startScan(myLeScanCallback);
            }
        });
        */
    }

    /* private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;
        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity.this.getLayoutInflater();
        }
        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
        public void clear() {
            mLeDevices.clear();
        }
        @Override
        public int getCount() {
            return mLeDevices.size();
        }
        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
*/
    private ScanCallback myLeScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, final android.bluetooth.le.ScanResult result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceAddress = result.getDevice().getAddress();
                    String deviceName = result.getDevice().getName();
                    System.out.println("DEBUG: " +deviceName);
                    if (deviceName != null && deviceName.contains("Humigadget") && !devices_list.contains(deviceName + " " + deviceAddress)) {
                        System.out.println("DEBUG: onScanResult");
                        devices_list.add(deviceName + " " + deviceAddress);
                        lv_adapter.notifyDataSetChanged();
                    }

                }
            });
        }
    };

    public static BluetoothAdapter myBluetoothAdapter;
    private ArrayAdapter lv_adapter;
    private ArrayList<String> devices_list;
    private ListView lv;
    private boolean myScanning;
    private Handler myHandler;
    private static final long Scan_period = 200000;

}
