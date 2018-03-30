package elvis.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bluetooth {
    private static final int REQUEST_ENABLE_BT = 1111;

    private Activity activity;
    private Context context;
    private UUID uuid;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice device, devicePair;
    private BufferedReader input;
    private OutputStream out;

    private CommunicationCallback communicationCallback;
    private DiscoveryCallback discoveryCallback;
    private BluetoothCallback bluetoothCallback;
    private boolean connected;

    private boolean runOnUi;

    public Bluetooth(Context context){
        initialize(context, UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
    }

    public Bluetooth(Context context, UUID uuid){
        initialize(context, uuid);
    }

    private void initialize(Context context, UUID uuid){
        this.context = context;
        this.uuid = uuid;
        this.communicationCallback = null;
        this.discoveryCallback = null;
        this.bluetoothCallback = null;
        this.connected = false;
        this.runOnUi = false;
    }

    public void onStart(){
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        context.registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    public void onStop(){
        context.unregisterReceiver(bluetoothReceiver);
    }

    public void showEnableDialog(Activity activity){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void enable(){
        if(bluetoothAdapter!=null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
    }

    public void disable(){
        if(bluetoothAdapter!=null) {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
            }
        }
    }

    public boolean isEnabled(){
        return bluetoothAdapter.isEnabled();
    }

    public void setCallbackOnUI(Activity activity){
        this.activity = activity;
        this.runOnUi = true;
    }

    public void onActivityResult(int requestCode, final int resultCode){
        if(bluetoothCallback!=null){
            if(requestCode==REQUEST_ENABLE_BT){
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        if(resultCode==Activity.RESULT_CANCELED){
                            bluetoothCallback.onUserDeniedActivation();
                        }
                    }
                });
            }
        }
    }
    
    public void connectToAddress(String address, boolean insecureConnection) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        connectToDevice(device, insecureConnection);
    }
    
    public void connectToAddress(String address) {
        connectToAddress(address, false);
    }
    
    public void connectToName(String name, boolean insecureConnection) {
        for (BluetoothDevice blueDevice : bluetoothAdapter.getBondedDevices()) {
            if (blueDevice.getName().equals(name)) {
                connectToDevice(blueDevice, insecureConnection);
                return;
            }
        }
    }
    
    public void connectToName(String name) {
        connectToName(name, false);
    }

    public void connectToDevice(BluetoothDevice device, boolean insecureConnection){
        new ConnectThread(device, insecureConnection).start();
    }
    
    public void connectToDevice(BluetoothDevice device){
        connectToDevice(device, false);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (final IOException e) {
            if(communicationCallback!=null) {
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        communicationCallback.onError(e.getMessage());
                    }
                });
            }
        }
    }

    public boolean isConnected(){
        return connected;
    }

    public void send(String msg){
        try {
            out.write(msg.getBytes());
        } catch (final IOException e) {
            connected=false;
            if(communicationCallback!=null){
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        communicationCallback.onDisconnect(device, e.getMessage());
                    }
                });
            }
        }
    }

    public List<BluetoothDevice> getPairedDevices(){
        List<BluetoothDevice> devices = new ArrayList<>();
        devices.addAll(bluetoothAdapter.getBondedDevices());
        return devices;
    }

    public void startScanning(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(scanReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    public void stopScanning(){
        bluetoothAdapter.cancelDiscovery();
    }

    public void pair(BluetoothDevice device){
        context.registerReceiver(pairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        devicePair=device;
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (final Exception e) {
            if(discoveryCallback!=null) {
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        discoveryCallback.onError(e.getMessage());
                    }
                });
            }
        }
    }

    public void unpair(BluetoothDevice device) {
        context.registerReceiver(pairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        devicePair=device;
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (final Exception e) {
            if(discoveryCallback!=null) {
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        discoveryCallback.onError(e.getMessage());
                    }
                });
            }
        }
    }

    private class ReceiveThread extends Thread implements Runnable{
        public void run(){
            String msg;
            try {
                while((msg = input.readLine()) != null) {
                    if(communicationCallback != null){
                        final String msgCopy = msg;
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                communicationCallback.onMessage(msgCopy);
                            }
                        });
                    }
                }
            } catch (final IOException e) {
                connected=false;
                if(communicationCallback != null){
                    ThreadHelper.run(runOnUi, activity, new Runnable() {
                        @Override
                        public void run() {
                            communicationCallback.onDisconnect(device, e.getMessage());
                        }
                    });
                }
            }
        }
    }

    private class ConnectThread extends Thread {    
        ConnectThread(BluetoothDevice device, boolean insecureConnection) {
            Bluetooth.this.device=device;
            try {
                if(insecureConnection){
                    Bluetooth.this.socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                }
                else{
                    Bluetooth.this.socket = device.createRfcommSocketToServiceRecord(uuid);
                }
            } catch (IOException e) {
                if(communicationCallback!=null){
                    communicationCallback.onError(e.getMessage());
                }
            }
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
                out = socket.getOutputStream();
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected=true;

                new ReceiveThread().start();

                if(communicationCallback!=null) {
                    ThreadHelper.run(runOnUi, activity, new Runnable() {
                        @Override
                        public void run() {
                            communicationCallback.onConnect(device);
                        }
                    });
                }
            } catch (final IOException e) {
                if(communicationCallback!=null) {
                    ThreadHelper.run(runOnUi, activity, new Runnable() {
                        @Override
                        public void run() {
                            communicationCallback.onConnectError(device, e.getMessage());
                        }
                    });
                }

                try {
                    socket.close();
                } catch (final IOException closeException) {
                    if (communicationCallback != null) {
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                communicationCallback.onError(closeException.getMessage());
                            }
                        });
                    }
                }
            }
        }
    }

    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action!=null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        if (state == BluetoothAdapter.STATE_OFF) {
                            if (discoveryCallback != null) {
                                ThreadHelper.run(runOnUi, activity, new Runnable() {
                                    @Override
                                    public void run() {
                                        discoveryCallback.onError("Bluetooth turned off");
                                    }
                                });
                            }
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        context.unregisterReceiver(scanReceiver);
                        if (discoveryCallback != null){
                            ThreadHelper.run(runOnUi, activity, new Runnable() {
                                @Override
                                public void run() {
                                    discoveryCallback.onFinish();
                                }
                            });
                        }
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (discoveryCallback != null){
                            ThreadHelper.run(runOnUi, activity, new Runnable() {
                                @Override
                                public void run() {
                                    discoveryCallback.onDevice(device);
                                }
                            });
                        }
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver pairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("BLE", "PAIR RECEIVER");

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    context.unregisterReceiver(pairReceiver);
                    if(discoveryCallback!=null){
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onPair(devicePair);
                            }
                        });
                    }
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    context.unregisterReceiver(pairReceiver);
                    if(discoveryCallback!=null){
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onUnpair(devicePair);
                            }
                        });
                    }
                }
            }
        }
    };

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action!=null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if(bluetoothCallback!=null) {
                    ThreadHelper.run(runOnUi, activity, new Runnable() {
                        @Override
                        public void run() {
                            switch (state) {
                                case BluetoothAdapter.STATE_OFF:
                                    bluetoothCallback.onBluetoothOff();
                                    break;
                                case BluetoothAdapter.STATE_TURNING_OFF:
                                    bluetoothCallback.onBluetoothTurningOff();
                                    break;
                                case BluetoothAdapter.STATE_ON:
                                    bluetoothCallback.onBluetoothOn();
                                    break;
                                case BluetoothAdapter.STATE_TURNING_ON:
                                    bluetoothCallback.onBluetoothTurningOn();
                                    break;
                            }                        }
                    });
                }
            }
        }
    };

    public void setCommunicationCallback(CommunicationCallback communicationCallback) {
        this.communicationCallback = communicationCallback;
    }

    public void removeCommunicationCallback(){
        this.communicationCallback = null;
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback){
        this.discoveryCallback = discoveryCallback;
    }

    public void removeDiscoveryCallback(){
        this.discoveryCallback = null;
    }

    public void setBluetoothCallback(BluetoothCallback bluetoothCallback){
        this.bluetoothCallback = bluetoothCallback;
    }

    public void removeBluetoothCallback(){
        this.bluetoothCallback = null;
    }
}