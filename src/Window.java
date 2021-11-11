
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;
import javax.swing.text.DefaultCaret;

public class Window extends JFrame {
	
	private static final long serialVersionUID = -4912475998818997766L;
	private String title;
	private JTextArea console;
	private JTextField fieldInput;
	private JButton buttonSend;
	private EventListenerList listenerList = new EventListenerList();
	
	
	public Window(String windowType){

		if(windowType.equals("Server") || windowType.equals("Client")) {
			title = windowType;
		}else {
			System.out.println("Invalid window title");
			System.exit(11);
		}
		
		createView();
		
		////window settings
		//name of window
		setTitle(title);
		//size
		setSize(500, 400);
		//closes window when 'x' button is pressed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//sets visible
		setVisible(true);
		//disables setResizability
		setResizable(false);
		//sets default position to centre
		setLocationRelativeTo(null);
	}
	
	private void createView() {
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout());
		
		//area where text is displayed
		console = new JTextArea();
		//stops user from editting text that is being displayed
		console.setEditable(false);
		((DefaultCaret) console.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//allows for pane to be scrollable if the text goes off screen
		JScrollPane consoleSP = new JScrollPane(console);
		consoleSP.setBorder(BorderFactory.createTitledBorder("Console Output"));
		panel.add(consoleSP, BorderLayout.CENTER);
		
		if(title.equals("Client")) {
			JPanel panelInput = new JPanel(new BorderLayout());
			panel.add(panelInput, BorderLayout.SOUTH);
			//area for user to input text
			fieldInput = new JTextField();
			panelInput.add(fieldInput, BorderLayout.CENTER);
			
			//creates button
			buttonSend = new JButton("Send");
			//when button is pressed, text inside the textbox is sent to classes that are listening (ie ChatClient)
			buttonSend.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					//input text
					String output = fieldInput.getText();
					//sends text
					fireDetailEvent(new DetailEvent(this, output));	
				}
			});	
			//positions butt
			panelInput.add(buttonSend, BorderLayout.EAST);
		}
	}
	
	//print text in text area
	public void printText(String text){
		console.append(text);
	}
	
	//////https://www.youtube.com/watch?v=YIbA5YH1UeA
	public void fireDetailEvent(DetailEvent event) {
		Object[] listeners = listenerList.getListenerList();
		
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == DetailListener.class) {
				((DetailListener) listeners[i + 1]).detailEventOccurred(event);
			}
		}
	}
	
	// adds text that has been entered to list
	public void addDetailListener(DetailListener listener) {
		listenerList.add(DetailListener.class, listener);
	}
	
	// removes text that has been entered to list
	public void removeDetailListener(DetailListener listener) {
		listenerList.remove(DetailListener.class, listener);
	}
	//////
	
}
