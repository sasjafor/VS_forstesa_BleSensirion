package ch.ethz.inf.vs.a1.forstesa.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static ch.ethz.inf.vs.a1.forstesa.ble.MainActivity.myBluetoothAdapter;

public class SensorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        device_address = getIntent().getExtras().getString("device_address");

        device = myBluetoothAdapter.getRemoteDevice(device_address);
        myBluetoothGatt = device.connectGatt(this, false, myGattCallback);

    }


    private final BluetoothGattCallback myGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == STATE_CONNECTED) {
                gatt.discoverServices();
            }
        }
    };

    private String device_address;
    private int connection_state = STATE_DISCONNECTED;
    private BluetoothGatt myBluetoothGatt;
    private BluetoothDevice device;
}
