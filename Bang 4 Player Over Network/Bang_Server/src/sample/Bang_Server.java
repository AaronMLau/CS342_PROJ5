package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Bang_Server extends Application implements EventHandler<ActionEvent>{
	
	
	public static void main(String[] argv) {
		launch(argv);
	}

	Game BANG = new Game(4);
	
	//Declare JavaFX Items
	VBox servSituationBox;
	Text  servSituationTxt;
	Text  playerSituationTxt;
	Text       servSetup;
	VBox  servSetupBox;
	HBox     sendBox;
	Button    startButton;
	Button shutdown1;
	Button shutdown2;
	TextField userInput;
	Stage mainMenu;

	// Declare Variables
	int listeningPort = 0;
	ServerSocket Origin;
	boolean establishedPort=false;
	DataOutputStream[] playerTransmit;
	DataInputStream[] playerReceive;
	Socket[] connectedClients;
	Thread setupThread;
	String[] playerNames;
	String player1Name = null;
	String player2Name = null;
	String player3Name = null;
	String player4Name = null;
	boolean allPlayersConnected = false;

	//******UI (JAVAFX) COMPONENT OF PROGRAM******//
	private Scene setStatusScene() {
		servSituationTxt=new Text("PORT SELECTION ERROR!!!");
		playerSituationTxt=new Text("");

		shutdown1 = new Button("Shutdown Server");
		shutdown1.setOnAction(this);
		
		servSituationBox=new VBox(servSituationTxt,playerSituationTxt,shutdown1);
		servSituationBox.setAlignment(Pos.CENTER);
		
		StackPane layout = new StackPane();
		layout.getChildren().add(servSituationBox);
		return new Scene(layout,300,200);
	}

	private Scene setSelectionScene() {
		startButton=new Button("Start Server");
		startButton.setOnAction(this);

		shutdown2 = new Button("Shutdown Server");
		shutdown2.setOnAction(this);
		
		servSetup=new Text("Enter port number for the server to listen to.");
		servSetup.setTextAlignment(TextAlignment.CENTER);
		
		userInput = new TextField("8080");
		userInput.setPrefWidth(60);
		sendBox    = new HBox(userInput,startButton);
		sendBox.setAlignment(Pos.CENTER);
		
		servSetupBox = new VBox(servSetup,sendBox,shutdown2);
		servSetupBox.setAlignment(Pos.CENTER);
		
		StackPane layout = new StackPane();
		layout.getChildren().add(servSetupBox);
		return new Scene(layout,300,200);	
	}

	@Override
	public void start(Stage primaryStage) {
		mainMenu = primaryStage;
		mainMenu.setTitle("BANG! Server");
		mainMenu.setScene(setSelectionScene());
		
		mainMenu.show();
	}
	
	//******SERVER COMPONENT OF PROGRAM******//
	private void portOpener(int listenTo) {
		listeningPort = listenTo;
		servSituationTxt.setText("Opening server on port:  "+listeningPort);
		while(establishedPort==false) {
			try {
				Origin = new ServerSocket(listeningPort);
				establishedPort = true;
				
				servSituationTxt.setText("Listening for clients on: "+listeningPort);
			}catch(IOException e) {
				
				servSituationTxt.setText("Unable to open port on:  "+listeningPort);
			}
		}
	}

	private void listen() {
		playerTransmit = new DataOutputStream[4];
		playerReceive = new DataInputStream [4];
		connectedClients = new Socket[4];
		
		Runnable retrieveFromPlayers = ()->{
			for(int i=0;i<4;++i) {
				try {
					connectedClients[i]=Origin.accept();
					int getPlayerCount = i + 1;
					int getPlayerCount2= 4 - getPlayerCount;
					playerSituationTxt.setText( getPlayerCount +" currently players connected, open for "+ getPlayerCount2 +" more.");
					
				}catch(IOException e) {
					System.out.println("User connection error " + (i+1) );
				}
			}
			
			for(int i=0;i<4;++i) {
				try {
					playerTransmit[i]= new DataOutputStream(connectedClients[i].getOutputStream());
					playerReceive[i] = new DataInputStream(connectedClients[i].getInputStream());
				}catch(IOException e) {
					try {connectedClients[i].close();} catch (IOException e1) {}
					System.out.println("Cannot establish connection "+ i);
					System.exit(-1);
				}
			}
			
			playerSituationTxt.setText("All 4 players are connected.");
			clientCommunication();
		};
		
		setupThread = new Thread(retrieveFromPlayers);
		setupThread.start();
	}

	private void clientCommunication() {
		playerNames = new String[4];
		
		Runnable[] retrieveFromPlayers = new Runnable[4];
			for(int i=0;i<4;++i) {
				final int playerIterator=i;
				
				retrieveFromPlayers[i]=()->{

					String Message="";
					try {
						playerNames[playerIterator]=playerReceive[playerIterator].readUTF();
						System.out.println(playerNames[playerIterator]);
						if(playerNames[3] != null && allPlayersConnected == false){
							// A test to make sure each player is assigned correctly
							player1Name = playerNames[0];
							player2Name = playerNames[1];
							player3Name = playerNames[2];
							player4Name = playerNames[3];
							System.out.println("Player 1 is: " + player1Name);
							System.out.println("Player 2 is: " + player2Name);
							System.out.println("Player 3 is: " + player3Name);
							System.out.println("Player 4 is: " + player4Name);
							BANG.startGame();
							for (Card card : BANG.showplayerhand()) {
								System.out.println(card.cardeffect());
							}
							allPlayersConnected = true;
						}



					}catch(IOException e){
						System.out.println("Username retrieval error!");
						System.exit(-1);
					}
					while(true) {
						try {
							// WHERE THE MESSAGES ARE TAKING PLACE
							/***********************************************************************/
							Message=playerReceive[playerIterator].readUTF();
							for(int j=0;j<4;++j) {
								if(j!=playerIterator) {
									System.out.println("Outputting message to "+playerNames[j]+" from "+playerNames[playerIterator]);
									playerTransmit[j].writeUTF(playerNames[playerIterator]+": "+Message);
								}
							}
							/***********************************************************************/
						}catch(IOException e) {
							System.out.println("Message Error From: "+ playerNames[playerIterator]);
							System.exit(-1);
						}
					}
				};
				
				Thread thread = new Thread(retrieveFromPlayers[i]);
				thread.start();
			}

	}

	@Override
	public void handle(ActionEvent event) {

		// Button which will open user selected port
		if(event.getSource()==startButton) {
			String userSelectedPort = userInput.getText();
			int listenTo = Integer.parseInt(userSelectedPort);
			mainMenu.setScene(setStatusScene());
			mainMenu.show();
			portOpener(listenTo);
			playerSituationTxt.setText("Waiting for 4 users to connect.");
			listen();
		}

		if(event.getSource() == shutdown1)
		{
			stop();
			System.exit(0);
		}

		if(event.getSource() == shutdown2)
		{
			System.exit(0);
		}
	}
	
	public void stop() {
		try {

			for(int i=0;i<4;++i) {
				playerTransmit[i].close();
			}
			for(int i=0;i<4;++i) {
				playerReceive[i].close();
			}
			for(int i=0;i<4;++i) {
				connectedClients[i].close();
			}
		  } catch (IOException e) {
		   System.out.println("Error while closing connection!");
		   System.exit(-1);
		  }
	}
}
