package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Bang_Server extends Application implements EventHandler<ActionEvent>{
	
	
	public static void main(String[] argv) {
		launch(argv);
	}

	Game BANG = new Game(4);
	
	//Declare JavaFX Items
	HBox servSituationBox;
	TextField  servSituationTxt;
	TextField  playerSituationTxt;
	Text       servSetup;
	HBox  servSetupBox;
	HBox     sendBox;
	Button    startButton;
	Button shutdown1;
	Button shutdown2;
	TextField userInput;
	TextField localHost;
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
	String player1Play = null;
	String player2Play = null;
	String player3Play = null;
	String player4Play = null;
	boolean allPlayersConnected = false;
	ArrayList<String> playerHands = new ArrayList<>();
	boolean waitingResponse = false;

	//******UI (JAVAFX) COMPONENT OF PROGRAM******//
	private Scene setStatusScene() {
		BackgroundImage titleImage2= new BackgroundImage(new Image("/pictures/bang.jpg",400,352,false,true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);

		servSituationTxt=new TextField("PORT SELECTION ERROR!!!");
		servSituationTxt.setPrefWidth(120);
		servSituationTxt.setEditable(false);
		playerSituationTxt=new TextField("");
		playerSituationTxt.setPrefWidth(120);
		playerSituationTxt.setEditable(false);

		shutdown1 = new Button("Shutdown Server");
		shutdown1.setOnAction(this);
		
		servSituationBox=new HBox(servSituationTxt,playerSituationTxt,shutdown1);
		servSituationBox.setAlignment(Pos.BOTTOM_CENTER);
		servSituationBox.setBackground(new Background(titleImage2));
		
		StackPane layout = new StackPane();
		layout.getChildren().add(servSituationBox);
		return new Scene(layout,400,350);
	}

	private Scene setSelectionScene() {

		//Image titleImage = new Image(getClass().getResourceAsStream("/pictures/bang.jpg"), .0, .0, false, false);
		//ImageView imageView = new ImageView(titleImage);

		BackgroundImage titleImage= new BackgroundImage(new Image("/pictures/bang.jpg",400,352,false,true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
//then you set to your node
		//myContainer.setBackground(new Background(myBI));


		startButton=new Button("Start Server");
		startButton.setOnAction(this);

		shutdown2 = new Button("Shutdown Server");
		shutdown2.setOnAction(this);

		localHost = new TextField("127.0.0.1");
		localHost.setPrefWidth(70);
		localHost.setEditable(false);
		
		userInput = new TextField("Enter Port #");
		userInput.setPrefWidth(80);
		userInput.setEditable(true);

		sendBox    = new HBox(userInput,localHost,startButton,shutdown2);
		sendBox.setAlignment(Pos.BOTTOM_CENTER);
		
		servSetupBox = new HBox(sendBox,shutdown2);
		servSetupBox.setAlignment(Pos.BOTTOM_CENTER);
		servSetupBox.setBackground(new Background(titleImage));
		
		StackPane layout = new StackPane();
		layout.getChildren().add(servSetupBox);
		return new Scene(layout,400,350);
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
				
				servSituationTxt.setText("Listening on: "+listeningPort);
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
					//int getPlayerCount2= 4 - getPlayerCount;
					if(getPlayerCount == 1){
						playerSituationTxt.setText( getPlayerCount +" player connected");
					}
					else{
						playerSituationTxt.setText( getPlayerCount +" players connected");
					}

					
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
			
			playerSituationTxt.setText("All 4 connected.");
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
							//BANG.initiateHands();
                            playerHands.add(BANG.info(0));
                            playerHands.add(BANG.info(1));
                            playerHands.add(BANG.info(2));
                            playerHands.add(BANG.info(3));
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

                            // Depending on the users name the method will know how to store the play of their hand
                            if(player1Name == playerNames[playerIterator])
                            {
                                if(waitingResponse == false)
                                {
                                    String[] arrOfStr = Message.split(" ", 2);
                                    System.out.println();
                                    BANG.getPlayer(0).playCard(arrOfStr[0], BANG.getPlayer(arrOfStr[1]));
                                    int result = Integer.parseInt(arrOfStr[1]);
                                    result = result-1;
                                    System.out.println(result);
                                    if(BANG.getPlayer(arrOfStr[1]).react()){
                                        playerTransmit[result].writeUTF("You are being BANG'd");
                                        System.out.println("IN IF STATEMENT 2");
                                        waitingResponse = true;
                                    }

                                }
                                else{
                                    System.out.println(" inside play1 else statement");
                                    BANG.getPlayer(0).playCard(player1Play, null);
                                    if (!BANG.getPlayer(0).react()) {
                                        System.out.println("Player 1 played a miss");
                                        playerTransmit[0].writeUTF("Player 1 played a miss");
                                        playerTransmit[1].writeUTF("Player 1 played a miss");
                                        playerTransmit[2].writeUTF("Player 1 played a miss");
                                        playerTransmit[3].writeUTF("Player 1 played a miss");
                                    } else {
                                        System.out.println("Player 1 did not miss");

                                        playerTransmit[0].writeUTF("Player 1 did not miss");
                                        playerTransmit[1].writeUTF("Player 1 did not miss");
                                        playerTransmit[2].writeUTF("Player 1 did not miss");
                                        playerTransmit[3].writeUTF("Player 1 did not miss");

                                    }
                                    waitingResponse = false;
                                }
                            }

                            if(player2Name == playerNames[playerIterator])
                            {
                                player2Play = Message;
                                if(waitingResponse == false)
                                {
                                    String[] arrOfStr = Message.split(" ", 2);
                                    System.out.println();
                                    BANG.getPlayer(0).playCard(arrOfStr[0], BANG.getPlayer(arrOfStr[1]));
                                    int result = Integer.parseInt(arrOfStr[1]);
                                    result = result-1;
                                    System.out.println(result);
                                    if(BANG.getPlayer(arrOfStr[1]).react()){
                                        playerTransmit[result].writeUTF("You are being BANG'd");
                                        System.out.println("IN IF STATEMENT 2");
                                    }
                                    waitingResponse = true;
                                }
                                else{
                                    System.out.println(" inside play2 else statement");
                                    BANG.getPlayer(1).playCard(player2Play, null);
                                    if (!BANG.getPlayer(1).react()) {
                                        System.out.println("Player 2 played a miss");

                                        playerTransmit[0].writeUTF("Player 2 played a miss");
                                        playerTransmit[1].writeUTF("Player 2 played a miss");
                                        playerTransmit[2].writeUTF("Player 2 played a miss");
                                        playerTransmit[3].writeUTF("Player 2 played a miss");
                                    } else {
                                        System.out.println("Player 2 did not miss");

                                        playerTransmit[0].writeUTF("Player 2 did not miss");
                                        playerTransmit[1].writeUTF("Player 2 did not miss");
                                        playerTransmit[2].writeUTF("Player 2 did not miss");
                                        playerTransmit[3].writeUTF("Player 2 did not miss");

                                    }
                                    waitingResponse = false;
                                }

                            }
                            // Depending on the users name the method will know how to store the play of their hand
                            if(player3Name == playerNames[playerIterator])
                            {
                                player3Play = Message;
                                if(waitingResponse == false)
                                {
                                    String[] arrOfStr = Message.split(" ", 2);
                                    System.out.println();
                                    BANG.getPlayer(0).playCard(arrOfStr[0], BANG.getPlayer(arrOfStr[1]));
                                    int result = Integer.parseInt(arrOfStr[1]);
                                    result = result-1;
                                    System.out.println(result);
                                    if(BANG.getPlayer(arrOfStr[1]).react()){
                                        playerTransmit[result].writeUTF("You are being BANG'd");
                                        System.out.println("IN IF STATEMENT 2");
                                    }
                                    waitingResponse = true;
                                }
                                else{
                                    System.out.println(" inside play3 else statement");
                                    BANG.getPlayer(2).playCard(player3Play, null);
                                    if (!BANG.getPlayer(2).react()) {
                                        System.out.println("Player 3 played a miss");

                                        playerTransmit[0].writeUTF("Player 3 played a miss");
                                        playerTransmit[1].writeUTF("Player 3 played a miss");
                                        playerTransmit[2].writeUTF("Player 3 played a miss");
                                        playerTransmit[3].writeUTF("Player 3 played a miss");
                                    } else {
                                        System.out.println("Player 3 did not miss");

                                        playerTransmit[0].writeUTF("Player 3 did not miss");
                                        playerTransmit[1].writeUTF("Player 3 did not miss");
                                        playerTransmit[2].writeUTF("Player 3 did not miss");
                                        playerTransmit[3].writeUTF("Player 3 did not miss");

                                    }
                                    waitingResponse = false;
                                }

                            }

                            if(player4Name == playerNames[playerIterator])
                            {
                                player4Play = Message;
                                if(waitingResponse == false)
                                {
                                    String[] arrOfStr = Message.split(" ", 2);
                                    System.out.println();
                                    BANG.getPlayer(0).playCard(arrOfStr[0], BANG.getPlayer(arrOfStr[1]));
                                    int result = Integer.parseInt(arrOfStr[1]);
                                    result = result-1;
                                    System.out.println(result);
                                    if(BANG.getPlayer(arrOfStr[1]).react()){
                                        playerTransmit[result].writeUTF("You are being BANG'd");
                                        System.out.println("IN IF STATEMENT 4");
                                    }
                                    waitingResponse = true;
                                }
                                else{
                                    System.out.println(" inside play 4 else statement");
                                    BANG.getPlayer(3).playCard(player4Play, null);
                                    if (!BANG.getPlayer(3).react()) {
                                        System.out.println("Player 4 played a miss");

                                        playerTransmit[0].writeUTF("Player 4 played a miss");
                                        playerTransmit[1].writeUTF("Player 4 played a miss");
                                        playerTransmit[2].writeUTF("Player 4 played a miss");
                                        playerTransmit[3].writeUTF("Player 4 played a miss");
                                    } else {
                                        System.out.println("Player 4 did not miss");

                                        playerTransmit[0].writeUTF("Player 4 did not miss");
                                        playerTransmit[1].writeUTF("Player 4 did not miss");
                                        playerTransmit[2].writeUTF("Player 4 did not miss");
                                        playerTransmit[3].writeUTF("Player 4 did not miss");

                                    }
                                    waitingResponse = false;
                                }

                            }



                            for(int j=0;j<4;++j) {
									System.out.println("Outputting message to "+playerNames[j]+" from "+playerNames[playerIterator]);
									playerTransmit[j].writeUTF("*n"+ playerNames[j] + playerHands.get(j));
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
			playerSituationTxt.setText("Waiting for 4 users");
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

	/*
	// IMPLEMENTION OF TEXT-BASED BANG
	private void Parse(String Message, Player p){
		if(Message == null){return "";}
		Message.ToLowerCase();
		String[] tokens = str.split(" ");
		switch(tokens[0]){
			case "h":
			case "help":
				return "Play bang by entering the keywords: \n \"cardname\" to play a card, \n \"bang playername\" to attack a player, \n \"missed\" to block an attack, etc.";
				break;
			case "b":
			case "bang":
				if(tokens[1].isEmpty()){
					return "";
				}
				p.playCard(new Card("bang"),BANG.getPlayer(tokens[1]));
				return p.name + " banged " + BANG.getPlayer(tokens[1]);
				break;
			case "m":
			case "miss":
			case "missed":
				p.playCard(new Card("missed"), p);
				return "missed!";
				break;
			case "v":
			case "view": // view hand
				ArrayList<Card> hand = p.getHand();
				String result = p.name + "\'s hand:";
				for(Card c : hand){
					result = result + "\n" + c.cardeffect();
				}
				result = result + "\n";
				return result;
				break;
			case "schofield":
				break;
			case "remington":
				break;
			case "mustang":
				break;
			// unsure how to implement weapons at this point
			default:
				return;
		}
	}
	 */
}
