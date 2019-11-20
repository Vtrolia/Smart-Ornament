import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;
import javax.swing.JButton;

/**
 * Swing Graphical User Interface created by WindowBuilder
 * @author Vincent Trolia
 *
 */
public class GUI implements ActionListener, MouseListener{

	public JFrame frmSmartOrnament;
	public JTextField txtEnterANew;
	public JButton btnNewButton;
	private String message = "none";

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSmartOrnament = new JFrame();
		frmSmartOrnament.setTitle("Smart Ornament");
		frmSmartOrnament.setBounds(100, 100, 405, 319);
		frmSmartOrnament.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSmartOrnament.setIconImage(new ImageIcon(GUI.class.getResource("/logo.png")).getImage());
		frmSmartOrnament.setResizable(false);
		
		JLayeredPane layeredPane = new JLayeredPane();
		frmSmartOrnament.getContentPane().add(layeredPane, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("Enter a new message here!");
		layeredPane.setLayer(lblNewLabel, 0);
		lblNewLabel.setForeground(new Color(0, 0, 0));
		lblNewLabel.setIcon(new ImageIcon(GUI.class.getResource("/bg.png")));
		lblNewLabel.setBounds(0, 0, 401, 300);
		layeredPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Welcome to your Smart Ornament!");
		lblNewLabel_1.setFont(new Font("DejaVu Math TeX Gyre", Font.BOLD, 14));
		lblNewLabel_1.setForeground(new Color(0, 0, 0));
		layeredPane.setLayer(lblNewLabel_1, 1);
		lblNewLabel_1.setBounds(65, 28, 289, 20);
		layeredPane.add(lblNewLabel_1);
		
		txtEnterANew = new JTextField();
		txtEnterANew.setFont(new Font("Century Schoolbook L", Font.PLAIN, 11));
		txtEnterANew.setText("Enter a new message here!");
		layeredPane.setLayer(txtEnterANew, 1);
		txtEnterANew.setBounds(65, 172, 164, 19);
		layeredPane.add(txtEnterANew);
		txtEnterANew.setColumns(10);
		txtEnterANew.addMouseListener(this);
		txtEnterANew.setActionCommand("Text");
		
		btnNewButton = new JButton("Send");
		btnNewButton.setBackground(new Color(0, 128, 0));
		layeredPane.setLayer(btnNewButton, 1);
		btnNewButton.setBounds(259, 173, 87, 20);
		layeredPane.add(btnNewButton);
		btnNewButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		setMessage(txtEnterANew.getText());
		
	}
	
	
	/**
	 * Getter and setter for message
	 * @return the message string
	 */
	public String getMessage() {
		System.out.println();
		return message;
	}
	public void setMessage(String newMessage) {
		message = newMessage;
	}

	/**
	 * When the text box is clicked, it should be cleared out
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		txtEnterANew.setText("");
		
	}

	/* unused methods */
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
