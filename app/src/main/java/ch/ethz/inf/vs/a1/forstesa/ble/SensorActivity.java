package ch.ethz.inf.vs.a1.forstesa.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static ch.ethz.inf.vs.a1.forstesa.ble.MainActivity.myBluetoothAdapter;
import static ch.ethz.inf.vs.a1.forstesa.ble.SensirionSHT31UUIDS.*;

public class SensorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        device_address = getIntent().getExtras().getString("device_address");

        device = myBluetoothAdapter.getRemoteDevice(device_address);
        myBluetoothGatt = device.connectGatt(this, false, myGattCallback);

        //TODO: add graph

    }

    @Override
    protected void onDestroy() {
        myBluetoothGatt.disconnect();
        myBluetoothGatt.close();
        super.onDestroy();
    }

    private final BluetoothGattCallback myGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == STATE_CONNECTED) {
                connection_state = STATE_CONNECTED;
                gatt.discoverServices();
                //System.out.println("DEBUG: connected");
            }
            else if (newState == STATE_DISCONNECTED) {
                connection_state = STATE_DISCONNECTED;
                //System.out.println("DEBUG: disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            humidity_service = gatt.getService(UUID_HUMIDITY_SERVICE);
            temperature_service = gatt.getService(UUID_TEMPERATURE_SERVICE);

            humidity_characteristics = new BluetoothGattCharacteristic(UUID_HUMIDITY_CHARACTERISTIC, PROPERTY_READ + PROPERTY_WRITE, PERMISSION_WRITE + PERMISSION_READ);
            temperature_characteristics = new BluetoothGattCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC, PROPERTY_READ + PROPERTY_WRITE, PERMISSION_WRITE + PERMISSION_READ);

            humidity_service.addCharacteristic(humidity_characteristics);
            temperature_service.addCharacteristic(temperature_characteristics);

            humidity_characteristics.setValue(new byte[] {0x01});
            gatt.writeCharacteristic(humidity_characteristics);

            //System.out.println("DEBUG: byte written");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (NOTIFICATION_DESCRIPTOR_UUID.equals(descriptor.getUuid())) {

                BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
                if (characteristic.getUuid().equals(UUID_TEMPERATURE_CHARACTERISTIC)) {
                    gatt.setCharacteristicNotification(humidity_characteristics, true);
                    BluetoothGattDescriptor humidity_descriptor = new BluetoothGattDescriptor(NOTIFICATION_DESCRIPTOR_UUID, PERMISSION_WRITE + PERMISSION_READ);
                    humidity_characteristics.addDescriptor(humidity_descriptor);
                    humidity_descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(humidity_descriptor);
                }
                else if (characteristic.getUuid().equals(UUID_HUMIDITY_CHARACTERISTIC)) {
                    gatt.readCharacteristic(humidity_characteristics);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //System.out.println("DEBUG: charRead");

            if (characteristic.getUuid().equals(UUID_HUMIDITY_CHARACTERISTIC)) {
                gatt.readCharacteristic(temperature_characteristics);
            }
            else if (characteristic.getUuid().equals(UUID_TEMPERATURE_CHARACTERISTIC)) {
                read_value(characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // super.onCharacteristicWrite(gatt, characteristic, status);


            //System.out.println("DEBUG: characteristicWrite");
            if (characteristic.getUuid().equals(UUID_HUMIDITY_CHARACTERISTIC)) {
                //System.out.println("DEBUG: characteristicWrite if");
                temperature_characteristics.setValue(new byte[] {0x01});
                gatt.writeCharacteristic(temperature_characteristics);
            }
            else if (characteristic.getUuid().equals(UUID_TEMPERATURE_CHARACTERISTIC)) {
                gatt.setCharacteristicNotification(temperature_characteristics, true);
                BluetoothGattDescriptor temperature_descriptor = new BluetoothGattDescriptor(NOTIFICATION_DESCRIPTOR_UUID, PERMISSION_WRITE + PERMISSION_READ);
                temperature_characteristics.addDescriptor(temperature_descriptor);
                temperature_descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(temperature_descriptor);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            read_value(characteristic);
        }

    };

    private void read_value(BluetoothGattCharacteristic characteristic) {
    //TODO: add these values to the graph
        byte[] data = characteristic.getValue();
        float value = convertRawValue(data);

        if (characteristic.getUuid().equals(UUID_HUMIDITY_CHARACTERISTIC)) {

            System.out.println("DEBUG: humidity: " + value);
        }
        else if (characteristic.getUuid().equals(UUID_TEMPERATURE_CHARACTERISTIC)) {

            System.out.println("DEBUG: temperature: " + value);
        }
    }


    private float convertRawValue(byte[] raw) {
        ByteBuffer wrapper = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getFloat();
    }


    private String device_address;
    private int connection_state = STATE_DISCONNECTED;
    private BluetoothGatt myBluetoothGatt;
    private BluetoothDevice device;
    private BluetoothGattService humidity_service;
    private BluetoothGattService temperature_service;
    private BluetoothGattCharacteristic humidity_characteristics;
    private BluetoothGattCharacteristic temperature_characteristics;
}
