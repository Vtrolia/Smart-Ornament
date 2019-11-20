package laststandcloud.java.smartornament;

/* needed Android imports */
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/* java imports for data transfer purposes */
import java.io.OutputStream;
import java.util.UUID;


/**
 * Main class that runs the Smart Ornament Application
 * @author Vincent Trolia/Last Stand Cloud Software Copyright 2019 All Rights Reserved
 */
public class MainActivity extends AppCompatActivity {

    /* The elements needed to be edited multiple times across the app */
    private Button button = null;
    private TextView message = null;

    /* Android bluetooth specific elements */
    private BluetoothDevice target = null;
    private BluetoothAdapter defaultAdapter = null;
    private BluetoothSocket sock = null;

    /* other, java classes needed throughout the app */
    public static final String name = "Smart Ornament";
    private OutputStream bloody = null;

    /* used when app is first launched from home screen */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // creates the UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button is initially disabled so that they must wait until the device is connected to
        // send a message
        button = findViewById(R.id.Send);
        button.setEnabled(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.isClickable()) {
                    Toast.makeText(getApplicationContext(), "Sending....", Toast.LENGTH_SHORT).show();
                    try {
                        sock = target.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
                        sock.connect();
                        bloody = sock.getOutputStream();
                        String mess = message.toString() + "\r\n";
                        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
                        bloody.write(mess.getBytes());

                        Toast.makeText(getApplicationContext(), "Message sent successfully", Toast.LENGTH_SHORT).show();
                        bloody.close();
                        sock.close();
                    }
                    catch(Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to send message!", Toast.LENGTH_LONG).show();
                        Log.e("ERROR", e.toString());
                    }
                }
            }
        });

        // display initial message then set it to clear the text box when the user clicks it,
        // for ease of use.
        message = findViewById(R.id.NMT);
        message.setText("Connecting to Ornament....");
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.setText("");
            }
        });

        // pressing "done" or enter on the virtual, or a physical keyboard will be the same as clicking
        // the send button
        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (button.isClickable()) {
                        button.performClick();
                    }
                    return true;
                }
                return false;
            }
        });

        // if there is no Bluetooth adapter on this device, tell the user that this app cannot be used, and close it
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        // ask the user to turn Bluetooth on if it is not
        if (!defaultAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, 86);
        }

        // easy pickings if the ornament is already paired
        for (BluetoothDevice query : defaultAdapter.getBondedDevices()) {
            if (name.equals(query.getName())) {
                Toast.makeText(getApplicationContext(), query.getName(), Toast.LENGTH_SHORT).show();
                target = query;
                enable();
                break;
            }
        }

        // if the ornament is not yet connected, start a Bluetooth discovery until it is found, and
        // await its result
        if (target == null) {
            defaultAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(rec, filter);
        }
    }


    /*
     * If the device was not found, a Bluetooth device discovery must query for it. This broadcast
     * receiver will handle the device discoveries and will enable a message to be sent if it is found
     */
    private final BroadcastReceiver rec = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // if the device hasn't been found during discovery, try again, and warn the user
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                defaultAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Smart Ornament not found, trying again...", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // if a device is found and it is the ornament, get it and enable the inputs
                BluetoothDevice temp = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (name.equals(temp.getName())) {
                    target = temp;
                    defaultAdapter.cancelDiscovery();
                    unregisterReceiver(this);

                    // tell the user of success and enable message sending
                    Toast.makeText(getApplicationContext(), name + " was found!", Toast.LENGTH_SHORT).show();
                    enable();
                }
            }
        }
    };


    /**
     * This function is called whenever the app is interrupted, closing all open and running background
     * processes.
     */
    private void closeApp() {
        // if in the process of sending data, close the output stream and the BluetoothSocket
        try {
            if (bloody != null) {
                bloody.close();
            }
            if (sock != null || sock.isConnected()) {
                sock.close();
            }
        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Uh-oh, an error in closing app!", Toast.LENGTH_SHORT).show();
        }

        // if searching for devices, cancel it
        try
        {unregisterReceiver(rec);}catch(Exception e){}
        finish();
    }


    /**
     * close app completely if the app is ended or even if the user switches away from it
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeApp();
    }
    @Override
    protected void onStop() {
        super.onStop();
        closeApp();
    }


    /**
     * Handles the Bluetooth enabling request
     * @param requestCode: request that was sent to operating system
     * @param resultCode: the result
     * @param data: result data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 86) {
            // close app if user did not enable Bluetooth
            if (resultCode != RESULT_OK) {
                finish();
            }
        }
    }


    /**
     * When the device is found or if it is already paired, this function enables the send button
     * and prompts the user that they can now enter a message to send
     */
    private void enable() {
        button.setEnabled(true);
        button.setClickable(true);
        message.setText("Enter a new message here!");
    }
}