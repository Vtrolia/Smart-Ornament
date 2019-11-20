import java.io.IOException;
import java.io.OutputStream;

public class Main {
	public static void main(String[] args) {
		// create the GUI and start connecting to the ornament. Lock the inputs until connection is complete
		GUI gui = new GUI();
		gui.frmSmartOrnament.setVisible(true);
		gui.txtEnterANew.setEditable(false);
		gui.txtEnterANew.setText("Connecting to Ornament...");
		gui.btnNewButton.setEnabled(false);
		
		// initialize variables to shut those damn warnings up
		bluetoothHandler ornament = null;
		OutputStream bloodyStone = null;
		
		
			//ornament = new bluetoothHandler("Smart Ornament");
			//bloodyStone = ornament.connectToTarget();
		
		// now that the connection is established, the text box and button can be enabled
		gui.txtEnterANew.setEditable(true);
		gui.txtEnterANew.setText("Enter a new message here!");
		gui.btnNewButton.setEnabled(true);
		
		// run forever
		while(true) {
			// when the message is available, send it to the ornament
			if (gui.getMessage() != "none") {
				try {
					System.out.println(gui.getMessage());
					bloodyStone.write(gui.getMessage().getBytes());
					
					// change the message back so that it is not continuously sent to ornament
					gui.setMessage("none");
				} catch (IOException e) {
					// if there is an exception here, end the program.
					e.printStackTrace();
					return;
				}
			}
		}
	}

}
