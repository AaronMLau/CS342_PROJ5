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
	String player1ID;
	String player2ID;
	String player3ID;
	String player4ID;
	String player1Role;
	String player2Role;
	String player3Role;
	String player4Role;
	String player1Health;
	String player2Health;
	String player3Health;
	String player4Health;
	String player1RestoreHealth;
	String player2RestoreHealth;
	String player3RestoreHealth;
	String player4RestoreHealth;
	String player1Turn;
	String player2Turn;
	String player3Turn;
	String player4Turn;
	int whosTurn = 0;
	boolean switchedTurn = false;
	int whoShot = 0;
	boolean restored = false;
	String lastWeapon = "";
	boolean player1Died = false;
	boolean player2Died = false;
	boolean player3Died = false;
	boolean player4Died = false;



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

		userInput = new TextField("8080");
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
						for(int j=0;j<4;++j) {
							playerTransmit[j].writeUTF("*n"+ playerNames[j] + playerHands.get(j) + "*$" +player1Name + "^" + player2Name + "^" +player3Name + "^" + player4Name);
						}
						String player1String = playerHands.get(0);
						String player2String = playerHands.get(1);
						String player3String = playerHands.get(2);
						String player4String = playerHands.get(3);

						String regex = "\\*[c#nhrdstwxep]";
						String[] tokens1 = player1String.split(regex);
						String[] tokens2 = player2String.split(regex);
						String[] tokens3 = player3String.split(regex);
						String[] tokens4 = player4String.split(regex);

						for(String s : tokens1)
						{
							System.out.println(s);
						}

						player1ID = tokens1[1];
						player2ID = tokens2[1];
						player3ID = tokens3[1];
						player4ID = tokens4[1];
						player1Role = tokens1[2];
						player2Role = tokens2[2];
						player3Role = tokens3[2];
						player4Role = tokens4[2];
						player1Health = tokens1[3];
						player2Health = tokens2[3];
						player3Health = tokens3[3];
						player4Health = tokens4[3];
						player1RestoreHealth = tokens1[3];
						player2RestoreHealth = tokens2[3];
						player3RestoreHealth = tokens3[3];
						player4RestoreHealth = tokens4[3];
						player1Turn = tokens1[6];
						if(player1Turn.equals("true")){whosTurn = 1;}
						player2Turn = tokens2[6];
						if(player2Turn.equals("true")){whosTurn = 2;}
						player3Turn = tokens3[6];
						if(player3Turn.equals("true")){whosTurn = 3;}
						player4Turn = tokens4[6];
						if(player4Turn.equals("true")){whosTurn = 4;}

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
						if(player1Name.equals(playerNames[playerIterator]))
						{
							if(Message.equals("beer"))
							{
								waitingResponse = true;
								whoShot = 1;
								player1Health = player1RestoreHealth;
								restored = true;
							}
							if(Message.equals("skipTurn"))
							{
								waitingResponse = true;
								whoShot = 1;
							}
							if(Message.equals("cantMiss"))
							{
								waitingResponse = true;
							}
							if(waitingResponse == false)
							{
								String[] arrOfStr = Message.split(" ", 2);
								int result = Integer.parseInt(arrOfStr[1]);
								result = result-1;
								for(int j=0;j<4;++j) {
									playerTransmit[j].writeUTF(playerNames[result] + " is shot at by " + player1Name + " using a " + arrOfStr[0] + "!");
								}
								whosTurn = result + 1;
								switchedTurn = true;
								whoShot = 1;
								lastWeapon = arrOfStr[0];
								waitingResponse = true;
							}
							else{
								if (Message.equals("missed")) {
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player1Name + " played a missed!");
									}
								}
								else if(restored == true){
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player1Name + " has restored their health!");
									}
								}
								else {
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player1Name + " did not miss, and was shot!");
									}
										int tempHealth = Integer.parseInt(player1Health);
										if(lastWeapon.equals("bang"))
										{
											tempHealth = tempHealth - 1;
											player1Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player1Name + " now has " + player1Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player1Name + " DIED!");
													player1Died = true;
												}
											}

										}
										else if(lastWeapon.equals("remington"))
										{
											tempHealth = tempHealth - 3;
											player1Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player1Name + " now has " + player1Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player1Name + " DIED!");
													player1Died = true;
												}
											}
										}
										else if(lastWeapon.equals("schofield"))
										{
											tempHealth = tempHealth - 2;
											player1Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player1Name + " now has " + player1Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player1Name + " DIED!");
													player1Died = true;
												}
											}
										}

								}
								if(whoShot == 4)
								{
									whosTurn = 1;
								}
								else{
									whosTurn = whoShot + 1;
								}
								switchedTurn = true;
								waitingResponse = false;
								restored = false;
								lastWeapon = "";
							}
						}

						if(player2Name.equals(playerNames[playerIterator]))
						{
							if(Message.equals("beer"))
							{
								waitingResponse = true;
								whoShot = 2;
								player2Health = player2RestoreHealth;
								restored = true;
							}
							if(Message.equals("skipTurn"))
							{
								waitingResponse = true;
								whoShot = 2;
							}
							if(Message.equals("cantMiss"))
							{
								waitingResponse = true;
							}
							if(waitingResponse == false)
							{
								String[] arrOfStr = Message.split(" ", 2);
								int result = Integer.parseInt(arrOfStr[1]);
								result = result-1;
								for(int j=0;j<4;++j) {
									playerTransmit[j].writeUTF(playerNames[result] + " is shot at by " + player2Name + " using a " + arrOfStr[0] + "!");
								}
								whosTurn = result + 1;
								switchedTurn = true;
								whoShot = 2;
								waitingResponse = true;
								lastWeapon = arrOfStr[0];
							}
							else{
								if (Message.equals("missed")) {
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player2Name + " played a missed!");
									}
								}
								else if(restored == true){
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player2Name + " has restored their health!");
									}
								}
								else {
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player2Name + " did not miss, and was shot!");
									}
										int tempHealth = Integer.parseInt(player2Health);
										if(lastWeapon.equals("bang"))
										{
											tempHealth = tempHealth - 1;
											player2Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player2Name + " now has " + player2Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player2Name + " DIED!");
													player2Died = true;
												}
											}
										}
										else if(lastWeapon.equals("remington"))
										{
											tempHealth = tempHealth - 3;
											player2Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player2Name + " now has " + player2Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player2Name + " DIED!");
													player2Died = true;
												}
											}
										}
										else if(lastWeapon.equals("schofield"))
										{
											tempHealth = tempHealth - 2;
											player2Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player2Name + " now has " + player2Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player2Name + " DIED!");
													player2Died = true;
												}
											}
										}

								}
								if(whoShot == 4)
								{
									whosTurn = 1;
								}
								else{
									whosTurn = whoShot + 1;
								}
								switchedTurn = true;
								waitingResponse = false;
								restored = false;
								lastWeapon = "";
							}

						}
						// Depending on the users name the method will know how to store the play of their hand
						if(player3Name.equals(playerNames[playerIterator]))
						{
							if(Message.equals("beer"))
							{
								waitingResponse = true;
								whoShot = 3;
								player3Health = player3RestoreHealth;
								restored = true;
							}
							if(Message.equals("skipTurn"))
							{
								waitingResponse = true;
								whoShot = 3;
							}
							if(Message.equals("cantMiss"))
							{
								waitingResponse = true;
							}
							if(waitingResponse == false)
							{
								String[] arrOfStr = Message.split(" ", 2);
								int result = Integer.parseInt(arrOfStr[1]);
								result = result-1;
								for(int j=0;j<4;++j) {
									playerTransmit[j].writeUTF(playerNames[result] + " is shot at by " + player3Name + " using a " + arrOfStr[0] + "!");
								}
								whosTurn = result + 1;
								switchedTurn = true;
								whoShot = 3;
								waitingResponse = true;
								lastWeapon = arrOfStr[0];
							}
							else{
								if (Message.equals("missed")) {
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player3Name + " played a missed!");
									}
								}
								else if(restored == true){
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player3Name + " has restored their health!");
									}
								}
								else {
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player3Name + " did not miss, and was shot!");
									}
										int tempHealth = Integer.parseInt(player3Health);
										if(lastWeapon.equals("bang"))
										{
											tempHealth = tempHealth - 1;
											player3Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player3Name + " now has " + player3Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player3Name + " DIED!");
													player3Died = true;
												}
											}
										}
										else if(lastWeapon.equals("remington"))
										{
											tempHealth = tempHealth - 3;
											player3Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player3Name + " now has " + player3Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player3Name + " DIED!");
													player3Died = true;
												}
											}
										}
										else if(lastWeapon.equals("schofield"))
										{
											tempHealth = tempHealth - 2;
											player3Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player3Name + " now has " + player3Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player3Name + " DIED!");
													player3Died = true;
												}
											}
										}


								}
								if(whoShot == 4)
								{
									whosTurn = 1;
								}
								else{
									whosTurn = whoShot + 1;
								}
								switchedTurn = true;
								waitingResponse = false;
								restored = false;
								lastWeapon = "";
							}
						}

						if(player4Name.equals(playerNames[playerIterator]))
						{
							if(Message.equals("beer"))
							{
								waitingResponse = true;
								whoShot = 4;
								player4Health = player4RestoreHealth;
								restored = true;
							}
							if(Message.equals("skipTurn"))
							{
								waitingResponse = true;
								whoShot = 4;
							}
							if(Message.equals("cantMiss"))
							{
								waitingResponse = true;
							}
							if(waitingResponse == false)
							{
								String[] arrOfStr = Message.split(" ", 2);
								int result = Integer.parseInt(arrOfStr[1]);
								result = result-1;
								for(int j=0;j<4;++j) {
									playerTransmit[j].writeUTF(playerNames[result] + " is shot at by " + player4Name + " using a " + arrOfStr[0] + "!");
								}
								whosTurn = result + 1;
								switchedTurn = true;
								whoShot = 4;
								waitingResponse = true;
								lastWeapon = arrOfStr[0];
							}
							else{
								if (Message.equals("missed")) {
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player4Name + " played a missed!");
									}
								}
								else if(restored == true){
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player4Name + " has restored their health!");
									}
								}
								else {
									for(int j=0;j<4;++j) {
										playerTransmit[j].writeUTF(player4Name + " did not miss, and was shot!");
									}
										int tempHealth = Integer.parseInt(player4Health);
										if(lastWeapon.equals("bang"))
										{
											tempHealth = tempHealth - 1;
											player4Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player4Name + " now has " + player4Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player4Name + " DIED!");
													player4Died = true;
												}
											}
										}
										else if(lastWeapon.equals("remington"))
										{
											tempHealth = tempHealth - 3;
											player4Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player4Name + " now has " + player4Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player4Name + " DIED!");
													player4Died = true;
												}
											}
										}
										else if(lastWeapon.equals("schofield"))
										{
											tempHealth = tempHealth - 2;
											player4Health = String.valueOf(tempHealth);
											for(int j=0;j<4;++j) {
												playerTransmit[j].writeUTF(player4Name + " now has " + player4Health + " health!");
												if(tempHealth <= 0){
													playerTransmit[j].writeUTF(player4Name + " DIED!");
													player4Died = true;
												}
											}
										}


								}
								if(whoShot == 4)
								{
									whosTurn = 1;
								}
								else{
									whosTurn = whoShot + 1;
								}
								switchedTurn = true;
								waitingResponse = false;
								restored = false;
								lastWeapon = "";
							}
						}



						if(whosTurn == 4 && switchedTurn == false){whosTurn = 1; switchedTurn = true;}
						if(whosTurn == 3 && switchedTurn == false){whosTurn = 4; switchedTurn = true;}
						if(whosTurn == 2 && switchedTurn == false){whosTurn = 3; switchedTurn = true;}
						if(whosTurn == 1 && switchedTurn == false){whosTurn = 2; switchedTurn = true;}
						switchedTurn = false;

						if(whosTurn == 1){
							playerTransmit[0].writeUTF("*n1*#1*r1*h" +player1Health+ "*d1*w1*ttrue*c1*$1");
							playerTransmit[1].writeUTF("*n1*#1*r1*h" +player2Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[2].writeUTF("*n1*#1*r1*h" +player3Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[3].writeUTF("*n1*#1*r1*h" +player4Health+ "*d1*w1*tfalse*c1*$1");
						}
						if(whosTurn == 2){
							playerTransmit[0].writeUTF("*n1*#1*r1*h" +player1Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[1].writeUTF("*n1*#1*r1*h" +player2Health+ "*d1*w1*ttrue*c1*$1");
							playerTransmit[2].writeUTF("*n1*#1*r1*h" +player3Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[3].writeUTF("*n1*#1*r1*h" +player4Health+ "*d1*w1*tfalse*c1*$1");
						}
						if(whosTurn == 3){
							playerTransmit[0].writeUTF("*n1*#1*r1*h" +player1Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[1].writeUTF("*n1*#1*r1*h" +player2Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[2].writeUTF("*n1*#1*r1*h" +player3Health+ "*d1*w1*ttrue*c1*$1");
							playerTransmit[3].writeUTF("*n1*#1*r1*h" +player4Health+ "*d1*w1*tfalse*c1*$1");
						}
						if(whosTurn == 4){
							playerTransmit[0].writeUTF("*n1*#1*r1*h" +player1Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[1].writeUTF("*n1*#1*r1*h" +player2Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[2].writeUTF("*n1*#1*r1*h" +player3Health+ "*d1*w1*tfalse*c1*$1");
							playerTransmit[3].writeUTF("*n1*#1*r1*h" +player4Health+ "*d1*w1*ttrue*c1*$1");
						}
						if(player1Died && player2Died && player3Died){
							for(int j=0;j<4;++j) {
								playerTransmit[j].writeUTF(player4Name + " Has WON THE GAME!");
							}
						}
						if(player1Died && player2Died && player4Died){
							for(int j=0;j<4;++j) {
								playerTransmit[j].writeUTF(player3Name + " Has WON THE GAME!");
							}
						}
						if(player1Died && player3Died && player4Died){
							for(int j=0;j<4;++j) {
								playerTransmit[j].writeUTF(player2Name + " Has WON THE GAME!");
							}
						}
						if(player2Died && player3Died && player4Died){
							for(int j=0;j<4;++j) {
								playerTransmit[j].writeUTF(player1Name + " Has WON THE GAME!");
							}
						}

						if(player1Died && player1Role.equals("sheriff")){
							for(int j=0;j<4;++j) {
								playerTransmit[j].writeUTF(player4Name + player3Name+ player2Name + " Has WON THE GAME!");
							}
						}
						if(player2Died && player2Role.equals("sheriff")){
							for(int j=0;j<4;++j) {
								playerTransmit[j].writeUTF(player4Name + player3Name+ player1Name + " Has WON THE GAME!");
							}
						}
						if(player3Died && player3Role.equals("sheriff")){
							for(int j=0;j<4;++j) {
								playerTransmit[j].writeUTF(player4Name + player1Name+ player2Name + " Has WON THE GAME!");
							}
						}
						if(player4Died && player4Role.equals("sheriff")){
							for(int j=0;j<4;++j) {
								playerTransmit[j].writeUTF(player1Name + player3Name+ player2Name + " Has WON THE GAME!");
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
	public void simulateGame() {
		while (BANG.Gameon()) {
			BANG.draw();
			//
			//	have the user input what they want to do
			//
			if (!BANG.Gameon()) {
				break;
			}
			BANG.endTurn();
		}
	}

 */

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