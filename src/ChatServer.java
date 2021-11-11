	
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

	private ServerSocket socket;
	private static Window serverWindow;
	private boolean running = false;
	private static boolean GUI;
	private int port, i;
	private List<Socket> clientList = new ArrayList<Socket>();
	 
	public ChatServer(int port) {
		this.port = port;
	}
	
	public void startServer() {			
		
		try {
			//creates new socket
			socket = new ServerSocket(port);
			printMessageN("Server socket port: " + port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
				
		new Thread(new Runnable() {
			@Override
			public void run() {
				running = true;
				printMessageN("Listening for clients");
				
				while(running) {
					try {
						Socket client;
						client = socket.accept();
						//adds client to list when they join
						clientList.add(client);
						printMessageN("A clinet has connected: " + client.getRemoteSocketAddress());
						
						new Thread(new Runnable() {
						@Override
						public void run() {
								while(clientList.size() > 0) {
									try {
										InputStreamReader r = new InputStreamReader(client.getInputStream());
										BufferedReader clientIn = new BufferedReader(r);
										PrintWriter out;
										
										while(true){
											//reads client input
											String userInput = clientIn.readLine();
											//splits it by ":" 
											String[] parts = userInput.split(":");
											//checks if the input is "EXIT"
											if(parts.length>0) {
												if(parts[1].equals(" EXIT")) {
													//if so, the client socket is removed
													clientList.remove(client);
													client.close();
												}
											}
											
											//iterates through all the clients in the list and sends the user input to each of them
											for(int i = 0; i < clientList.size(); i++) {
												out = new PrintWriter(clientList.get(i).getOutputStream(), true);
												out.println(userInput);
												out.flush();
											}
											//prints user message to server
											printMessageN(client.getRemoteSocketAddress() + ": " + userInput);
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
									finally{
										try {
											clientList.remove(client);
											client.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
									
								}
							}
						
						}).start();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				BufferedReader terminalIn = new BufferedReader(new InputStreamReader(System.in));
				String s;
				try {
					//reads input from server terminal
					s = terminalIn.readLine().toUpperCase();
					while(true) {
						//if "EXIT" is entered, the server shuts down
						if(s.equals("EXIT")) {
							printMessage("Exiting program");
							System.exit(1);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}
	
	public void StopServer() {
		running = false;
	}
	
	private void printMessage(String message) {
		if(GUI) {
			serverWindow.printText(message);
		}
		System.out.print(message);
	}
	
	private void printMessageN(String message) {
		printMessage(message + "\n");
	}
	
	private static String getInput() throws IOException {
		String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
		return input;
	}
			
	public int getI(){
		return i;
	}
	
	public void setI(int i) {
		this.i = i;
	}
	
	private static int getPort(String[] args){
		//iterates through arguments, looking for "-csp"
		if(args.length == 3 && args[1].equals("-csp")) {
			try{
				  int num = Integer.parseInt(args[2]);
				  return num;
				} catch (NumberFormatException e) {
				  return 14001;
				}
		//returns default port 14001 if no port is found
		}else return 14001;
	}
	
	public static void main(String[] args) throws IOException {
		//gets port address from argument given by user or default if not given
		int port = getPort(args);
		
		System.out.print("Use GUI? Y/N ");
		String answer = getInput();
		if(answer.toUpperCase().equals("Y")) {
			GUI = true;
			serverWindow = new Window("Server");
		}else GUI = false;
		
		ChatServer cs = new ChatServer(port);
		cs.startServer();
	}
}