import java.io.IOException;
import java.io.OutputStream;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;


/**
 * This class, while used as an interface between a GUI program and an Arduino in its originality,
 * is general in its purpose enough to serve as a general class for creating/sending data over Bluetooth
 * to a remote device. The constructor will find the device it has been called to connect to, and will
 * retain information about the device as well as its address. Later, a direct call to connect to the
 * device will start the actual connection. Created with the Bluecove library
 * @author Vincent Trolia, July 23 2019
 * @version 1.0
 *
 */
public class bluetoothHandler {
	
	/* used to hold information about the device it will connect to */
	private RemoteDevice target;
	
	
	/**
	 * The constructor takes in the name that you will see when the device's
	 * graphical Bluetooth manager lists it
	 * @param targetFriendlyName: the string the device sends out
	 * @throws IOException
	 */
	public bluetoothHandler(String targetFriendlyName) throws IOException {
		RemoteDevice test = null;
		// check if this device is already connected
		test = alreadyConnected(targetFriendlyName);
		
		// if not already connected, get the connection info
		if (test != null) {
			target = test;
		}
		else {
			newTarget(targetFriendlyName);
		}
	}
	
	
	/**
	 * Before querying every possible device available by Bluetooth, this function serves
	 * as a time saving measure to check if the device is already connected to the LocalDevice.
	 * It will return the target device if it is present.
	 * @param knownDevice: The name of the target that may possibly be known
	 * @return: Either the device if it is connected, or null if it is not currently known
	 */
	private RemoteDevice alreadyConnected(String knownDevice) {
		RemoteDevice[] devices = null;
		try {
			// grab all the devices connected to the LocalDevice, and check each one to see if it
			// fits the desired name
			devices = LocalDevice.getLocalDevice().getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);
			for (int i = 0; i < devices.length; i++) {
				if(devices[i].getFriendlyName(true).equals(knownDevice)) {
					return devices[i];
				}
			}
		}
		// if an exception occurs or the target is not among the known devices, return null
		catch (Exception e) {
			return null;
		}
		return null;
	}
	
	/**
	 * If not already connected, a DiscoveryListener object will be needed to find
	 * the target device. 
	 * @param targetName: the name of the target device
	 */
	private void newTarget(String targetName) {
		Object inquiryCompletedEvent = new Object();
		/*
		 * This section of code is directly taken from the examples given from the Bluecove
		 * website, at http://www.bluecove.org/bluecove/apidocs/overview-summary.html. This
		 * implements the DiscoveryListener interface and goes through each possible bluetooth
		 * device until the correct target is found and it is set as the target device.
		 */
		DiscoveryListener listener = new DiscoveryListener() {
			public void deviceDiscovered(RemoteDevice dev, DeviceClass c) {
				
				try {
					if (dev.getFriendlyName(true).equals(targetName)) {
						target = dev;
					}
				} catch (IOException e) {}
			}
			
			public void inquiryCompleted(int discType) {
				synchronized(inquiryCompletedEvent) {
					inquiryCompletedEvent.notifyAll();
				}
			}

			// unused methods
			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {}
			@Override
			public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {}
		};
		
		// the example says that this synchronized method must be called, so it shall be done
		synchronized(inquiryCompletedEvent) {
			try {
				boolean done = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
				if (done) {
					inquiryCompletedEvent.wait();
				}
			} catch(Exception e) {}
		}
	}
	
	
	/**
	 * When the target device has been found, a connection is not created. This method creates the connection
	 * and creates an output stream to be used to send data to the RemoteDevice stored in target. If not 
	 * successful, it returns null
	 * @return: either the OutputStream connection or null
	 */
	public OutputStream connectToTarget() {
		try {
			String url = new String("btspp://" + target.getBluetoothAddress() + ":1;authenticate=false;" + "encrypt=false;master=false");
			StreamConnection bt = (StreamConnection) Connector.open(url);
			return bt.openOutputStream();
		} catch (IOException e) {
			return null;
		}
	}
	
	
	/**
	 * Custom getter if the caller wants the target's BT address
	 * @return the address of the target machine
	 */
	public String getBtAddress() {
		return target.getBluetoothAddress();
	}
	
	
	/**
	 * Getter to ask the target device for its name
	 * @return Either the name, or null if not available
	 */
	public String getTargetName() {
		try {
			return target.getFriendlyName(true);
		} catch (IOException e) {
			return null;
		}
	}
}
