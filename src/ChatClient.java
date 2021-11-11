
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

public class ChatClient {

	Socket server;
	private Window clientWindow;
	private static boolean GUI;
	private String username;
	
	
	public ChatClient(String address, int port, String username) {
		this.username = username;
		try {
			//creates new socket
			server = new Socket(address, port);
		} catch (UnknownHostException e) {
			printMessage("Unable to connect");
			System.exit(2);
		} catch (IOException e) {
			System.exit(3);
		}
		
		if(GUI) {
			//creates GUI if user answered "Y" at the command line
			clientWindow = new Window("Client");
			//listens for the window sending text from the user
			clientWindow.addDetailListener(new DetailListener() {
				public void detailEventOccurred(DetailEvent event) {
					String text = event.getText();
					sendMessage(text);
				}
			});
		}		
	}
	
	
	public void go() {
		try {
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(server.getOutputStream(), true);
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
			
			sendMessage(userIn, out);
			recieveMessage(serverIn);
			
		} catch (IOException e) {
			System.exit(4);
		}
		finally {
			try {
				printMessageN("Connction lost. Shutting down");
				server.close();
				System.exit(5);
			} catch (IOException e) {
				System.exit(6);
			}
		}
	}

	//sends message to the server
	private synchronized void sendMessage(BufferedReader userIn, PrintWriter out) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					String userInput;
					try {
						while(true) {
							//reads from command line
							userInput = userIn.readLine();
							out.println(username + ": " + userInput);
						}
					} catch (IOException e) {
						System.exit(7);
					}
				}	
			}
		);
	}
	
	//sends message to the server
	public synchronized void sendMessage(String text) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					try {
						PrintWriter out = new PrintWriter(server.getOutputStream(), true);
						//sends text and username
						out.println(username + ": "+ text + "\n");
					} catch (IOException e) {
						System.exit(8);
					}
				}
			}
		);
	}
	
	//recieves messages from the server and proccesses what to do with them
	private void recieveMessage(BufferedReader serverIn) {
		while(true) {
			String serverRes;
			try {
				//reads input from the server
				serverRes = serverIn.readLine();
				//splits the message by ":", giving the username and the message separately
				String[] parts = serverRes.split(":");
				//username
				String name = parts[0];
				
				printName(serverRes, name);
				
				//checks if exit promt is entered and exits the program
				if(parts[1].equals("EXIT")) {
					printMessageN("Exiting");
					System.exit(9);
				}
			}catch (IOException e) {
				//e.printStackTrace();
				printMessage("Unable to communicate with server. Disconnecting.");
				System.exit(10);
			}
		}
	}
	
	//decides if name should be printed with name
	private void printName(String serverRes, String name) {
		//prints name and message if the sender and reciever are different
		if(!username.equals(name)) {
			printMessageN(serverRes);
		//prints name and message if GUI is active
		}else if(GUI){
			clientWindow.printText(serverRes);
		} 
		//doesn't print the message if the sender matches the reciever
	}
	
	//prints message to GUI if it active and console
	private void printMessage(String message) {
		if(GUI) {
			clientWindow.printText(message);
		}
		System.out.print(message);
	}
	
	//adds new line to text to be printed
	private void printMessageN(String message) {
		 printMessage(message + "\n");
	}
	
	//gets input from user
	private static String getInput() throws IOException {
		String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
		return input;
	}
	
	public String getName() {
		return username;
	}
	
	private static boolean getGUIAnswer(String answer) throws IOException {
		do {
			System.out.print("Use GUI? Y/N ");
			//converts input from user into uppercase
			answer = getInput().toUpperCase();
			//compares answer given and returns corresponding statement
			if(answer.equals("Y")) return true;
			else if(answer.equals("N")) return false;
		//repeats asking the using and taking input until a valid answer is given
		}while(!answer.equals("Y") && !answer.equals("N"));
		return false;
	}
	
	//gets port/ip address from user when entered at command line
	private static String getAddress(String[] args, String text, String deflt) {
		//iterates through arguments until the desired string (eg -ccp) is found
		for(int i = 0; i < args.length-1; i++) {
			if(args[i].equals(text)) {
				//the arguement following is returned
				return args[i+1];
			}
		}
		//returns the default opotion if no match is found
		return deflt;
	}
	
	public static void main(String[] args) throws IOException {
		Boolean invalid = true;
		String name = "username";
		
		String ip = getAddress(args, "-cca", "localhost");
		int port = Integer.parseInt(getAddress(args, "-ccp", "14001"));
		
		do {
			System.out.print("Username: ");	
			name = getInput();
			invalid = false;
			for(int i = 0; i < name.length(); i++) {
				if(name.charAt(i) == ':') {
					invalid = true;
				} 
			}
		}while(invalid);
		
		ChatClient cc = new ChatClient(ip, port, name);
		
		//////GUI freezes opened so it will not be run in this current version
		//GUI = getGUIAnswer("");
		
		cc.go();
	}
}
