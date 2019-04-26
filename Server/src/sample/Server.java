/*
 * Author: Rami Masoud
 * UIN:    664724790
 * NetID:  rmasou2
 * Class:  CS 342
 * Assign: Programming Project #3
 * Description: A simple implementation of RPSLS, meant to be an introduction into the world of multi-threading and sockets (networking with java)
 * This File: Client class implementation, displayed to each player reads in and outputs to and from the server the results of the game.
 *
 * //Good documentation on passing strings from server to player and player to server using UTF.
 * Reference: https://docs.oracle.com/javase/7/docs/api/java/io/DataInputStream.html#readUTF()
 */
package sample;

// Imported libraries, now external packages other than javaFX
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Application implements EventHandler<ActionEvent>{

    // Start the gui
    public static void main(String[] argv) {
        launch(argv);
    }

    // Gui JavaFX elements are pre defined here 
    VBox servSituationBox;
    Text servSituationTxt;
    Text playerSituationTxt;
    HBox playerSituationBox;
    HBox shutDownBox;
    Button shutDown;
    Button shutDown0;
    Text playerAbout;
    Text servSetup;
    VBox servSetupBox;
    HBox sendBox;
    Button sendButton;
    TextField userInput;
    Stage mainMenu;
    Button readyUP;

    // Variables used within the methods are initialized here
    boolean player1Check = false;
    boolean player2Check = true;
    int initializer = 0;
    String player1Name = "Nothingness";
    String player2Name = "Nothingness";
    String player1Play = "Nothingness";
    String player2Play = "Nothingness";
    String outcome = "Nothingness";
    int player1Score = 0;
    int player2Score = 0;
    int listeningPort;
    ServerSocket Origin;
    boolean open=false;
    DataOutputStream[] playerTransmit;
    DataInputStream[] playerRecieve;
    Socket[] player;
    Thread setupThread;
    ArrayList<String> playerNames = new ArrayList<>();
    int playerCount = 0;
    boolean startGame = false;

    // This is the primary stage, here we can edit the name of the window in the tile bar
    public void start(Stage primaryStage) {
        mainMenu = primaryStage;
        mainMenu.setTitle("RPSLS Server");
        mainMenu.setScene(setSelectionScene());
        mainMenu.show();
    }

    // This is the first scene that the user sees this is the first part of the program
    // Here the user is asked to enter in a port for the server to start listening to
    private Scene setSelectionScene() {
        sendButton=new Button("Start Server");
        sendButton.setOnAction(this);

        servSetup=new Text("Please enter a listening port for the server.\nNote: Default port is already selected.\nNote: Range of possible ports for a server socket is (1 through 65535)");
        servSetup.setTextAlignment(TextAlignment.CENTER);

        shutDown0 = new Button("Shutdown Server");
        shutDown0.setOnAction(this);

        userInput = new TextField("1000");
        userInput.setPrefWidth(100);
        sendBox    = new HBox(userInput,sendButton,shutDown0);
        sendBox.setAlignment(Pos.CENTER);

        servSetupBox = new VBox(sendBox,servSetup);
        servSetupBox.setAlignment(Pos.CENTER);

        StackPane pageSetup = new StackPane();
        pageSetup.getChildren().add(servSetupBox);
        return new Scene(pageSetup,500,300);
    }

    // Second scene, this shows to the clients how many people are connected to the server
    // The clients can also shut down the server from here
    private Scene setStatusScene() {
        servSituationTxt=new Text("PORT SELECTION ERRROR!!!");
        playerSituationTxt=new Text("");

        playerAbout = new Text("");

        shutDown = new Button("Shutdown Server");
        shutDown.setOnAction(this);

        readyUP = new Button("Press, When All Clients Have Been Connected");
        readyUP.setOnAction(this);

        servSituationBox=new VBox(servSituationTxt,playerSituationTxt);
        servSituationBox.setAlignment(Pos.CENTER);

        playerSituationBox = new HBox(playerAbout);
        playerSituationBox.setAlignment(Pos.TOP_CENTER);

        shutDownBox = new HBox(shutDown, readyUP);
        shutDownBox.setAlignment(Pos.BOTTOM_CENTER);

        StackPane pageSetup = new StackPane();
        pageSetup.getChildren().addAll(servSituationBox, playerSituationBox, shutDownBox);
        return new Scene(pageSetup,500,300);
    }

    // A method created that takes the user selected port number and allows the server to open to that port and listen for the clients to connect 
    private void portOpener(int listenTo) {
        listeningPort = listenTo;
        while(open==false) {
            try {
                Origin = new ServerSocket(listeningPort);
                open = true;
                servSituationTxt.setText("Server is open for communication on port - "+ listeningPort);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    // A method created to that creates and the input and output stream for the clients connected to the server
    private void readAndFeed() {
        playerTransmit = new DataOutputStream[100]; // NOTE: How there is "2" being initiated in each of the arrays this is for the 2 players in RPSLS
        playerRecieve = new DataInputStream [100];
        player = new Socket[100];

        Runnable listenReadFeed = ()->{
            String tempName = "";
            for(int i=0;i<100;++i) {
                try {

                        player[i]=Origin.accept(); // Notify which ever user if the are the only ones connected
                        playerSituationTxt.setText("Accepting Clients To The Server");
                        playerTransmit[i]= new DataOutputStream(player[i].getOutputStream());
                        playerRecieve[i] = new DataInputStream(player[i].getInputStream());

                        tempName = playerRecieve[i].readUTF();
                        playerNames.add(tempName);

                        int numPlayersInLobby = i;

                        while (numPlayersInLobby >= 0)
                        {
                            playerTransmit[numPlayersInLobby].writeUTF("*START*");
                            for (String x : playerNames) {
                                playerTransmit[numPlayersInLobby].writeUTF(String.valueOf(x));
                            }
                            playerTransmit[numPlayersInLobby].writeUTF("*DONE*");
                            numPlayersInLobby--;
                        }

                        ++playerCount;
                        System.out.println("the number of players is " + playerCount);
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }

            // Once both players have connected you can notify the players that they can start the game.

            //calculateRPSLS(); // Jump to the game calculation rules
        };

        setupThread = new Thread(listenReadFeed); // Set up a new thread for each of the connected players
        setupThread.start();
    }

    // This is the method used to retrieve the inputs from both users
    // then calculate the round winner, loser, points, etc.
    // finally output back to the users
    private void calculateRPSLS() {

        playerSituationTxt.setText("All players are connected to the server,\nthe game can now start!");

        Runnable[] retrieveFromPlayers = new Runnable[playerCount];
        for(int i=0;i<playerCount;++i) {
            final int iterator=i;

            retrieveFromPlayers[i]=()->{

                String Message="";
                String verses1 = "";
                String verses2 = "";

                while(true) {
                    try {
                        Message=playerRecieve[iterator].readUTF();
                        for(int j=0; j<playerCount; ++j) {
                            if(j!=iterator) {
                                playerTransmit[j].writeUTF(playerNames.get(iterator)+": "+Message);



                            }
                        }
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread w = new Thread(retrieveFromPlayers[i]); // Initialize the thread
            w.start();
        }

    }

    // The only event handler needed for this class
    public void handle(ActionEvent event) {

        if(event.getSource()==sendButton) {

            // Retrieve the port to open based on the user selection
            String userSelecectedPort = userInput.getText();
            int listenTo = Integer.parseInt(userSelecectedPort);

            int numberOfUsers = 100;

            if(numberOfUsers == 100) { // Send the port to the port opener, and notify that there is no one connected
                mainMenu.setScene(setStatusScene());
                mainMenu.show();
                portOpener(listenTo);
                playerSituationTxt.setText("Listening for 2 or more users to connect.");
                readAndFeed();
            }
        }

        if(event.getSource()==readyUP) {
            calculateRPSLS();
        }
        //  The shutdown event and method
        if(event.getSource()==shutDown) {
            stop();
        }
        if(event.getSource()==shutDown0) {
            stop();
        }
    }

    public void stop() {
        System.exit(0);
    }
}
/*
    private void parseCommands(){
        Thread receiveData = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {// Once both players have connected and selected each other you can notify the players that they can start the game.
                        playerSituationTxt.setText("Two players are ready to Play,\nthe game can now start!");
                        if ((player1Play = playerRecieve[player1Index].readUTF()) == null) {
                            Thread.sleep(100);
                            continue;
                        }
                        if((player2Play = playerRecieve[player1Index].readUTF()) == null) {
                            Thread.sleep(100);
                            continue;
                        }
                        for (String x : playerNames) {
                            if (player1Play.startsWith(x)) {
                                player1Index = playerNames.indexOf(x);
                            }
                            if (player1Play.endsWith(x)) {
                                // play against each other
                                player2Index = playerNames.indexOf(x);
                                int j = player1Play.indexOf(' ');
                                String rest = player1Play.substring(j++);
                                player1Name = player1Play.substring(0, j);
                                j = player1Play.indexOf(' ');
                                player2Name = player1Play.substring(j, player1Play.length() - 1);
                                if (player1Index == -1 || player2Index == -1) {
                                    break;
                                }
                                if ((player1Play = playerRecieve[player1Index].readUTF()) == null) {
                                    Thread.sleep(100);
                                    continue;
                                }
                                if((player2Play = playerRecieve[player1Index].readUTF()) == null) {
                                    Thread.sleep(100);
                                    continue;
                                }

                                calculateRPSLS();
                                playerSituationTxt.setText("player one played " + player1Play + " and player two played" + player2Play);

                            }
                        }
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
        });
        receiveData.start();


    }
    private void setup(){
        Thread listenForClients = new Thread(new Runnable() {
            @Override
            public void run(){
                String tempName = "";
                for(int i=0;i<100;++i) {
                    numPlayers=i;
                    try {
                        player[i]=Origin.accept(); // Notify which ever user if the are the only ones connected
                        playerTransmit[i]= new DataOutputStream(player[i].getOutputStream());
                        playerRecieve[i] = new DataInputStream(player[i].getInputStream());
                        tempName = playerRecieve[i].readUTF();
                        playerNames.add(tempName);

                        int numPlayersInLobby = i;

                        while (numPlayersInLobby >= 0)
                        {
                            playerTransmit[numPlayersInLobby].writeUTF("*START*");
                            for (String x : playerNames) {
                                playerTransmit[numPlayersInLobby].writeUTF(String.valueOf(x));
                            }
                            playerTransmit[numPlayersInLobby].writeUTF("*DONE*");
                            numPlayersInLobby--;
                        }

                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }); listenForClients.start();
    }

    public int getNumPlayers(){
        //return 0;
        return numPlayers;
    }

    // This is the method used to retrieve the inputs from both users
    // then calculate the round winner, loser, points, etc.
    // finally output back to the users
    private void calculateRPSLS() {
        // it should simply determine winner in rock paper scissors between two clients
        switch(player1Play){
            case "rock":
                switch(player2Play){
                    case "rock":
                        // tie, just kick them out
                        writeOut("TIE", "TIE");
                        break;
                    case "paper":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;
                    case "scissors":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;
                    case "lizard":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;
                    case "spock":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;

                }
                break;
            case "scissors":
                switch(player2Play){
                    case "rock":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;
                    case "paper":
                        // player 1 wins, player 2 loses
                        writeOut("LOSE", "WIN");
                        break;
                    case "scissors":
                        // tie, just kick them out
                        writeOut("TIE", "TIE");
                        break;
                    case "lizard":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;
                    case "spock":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;

                }
                break;
            case "paper":
                switch(player2Play){
                    case "rock":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;
                    case "paper":
                        // tie, just kick them out
                        writeOut("TIE", "TIE");
                        break;
                    case "scissors":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;
                    case "lizard":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;
                    case "spock":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;

                }
                break;
            case "lizard":
                switch(player2Play){
                    case "rock":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;
                    case "paper":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;
                    case "scissors":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;
                    case "lizard":
                        // tie, just kick them out
                        writeOut("TIE", "TIE");
                        break;
                    case "spock":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;

                }
                break;
            case "spock":
                switch(player2Play){
                    case "rock":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;
                    case "paper":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;
                    case "scissors":
                        // player 1 wins, player 2 loses
                        writeOut("WIN", "LOSE");
                        break;
                    case "lizard":
                        // player 1 loses, player 2 wins
                        writeOut("LOSE", "WIN");
                        break;
                    case "spock":
                        // tie, just kick them out
                        writeOut("TIE", "TIE");
                        break;

                }
                break;
            default:
                System.out.print("Error.");
                writeOut("ERROR", "ERROR");
                break;

        }
        System.out.println("Reached calculateRPSLS ");
    }

    private void writeOut(String p1, String p2){
        if(!(p1.equals("TIE") || p1.equals("WIN") || p1.equals("LOSE") || p2.equals("TIE") || p2.equals("WIN") || p2.equals("LOSE"))){
            return;
        }
        try {
            playerTransmit[player1Index].writeUTF(p1);
            playerTransmit[player2Index].writeUTF(p2);
        }
        catch (IOException e){
            System.out.println("Error playing between client " + player1Name + " and " + player2Name + ".");
        }

    }

    // The only event handler needed for this class
    public void handle(ActionEvent event) {

        if(event.getSource()==sendButton) {

            // Retrieve the port to open based on the user selection
            String userSelecectedPort = userInput.getText();
            int listenTo = Integer.parseInt(userSelecectedPort);

            // what is numOfUsers for?
            int numberOfUsers = 100;

            if(numberOfUsers == 100) { // Send the port to the port opener, and notify that there is no one connected
                mainMenu.setScene(setStatusScene());
                mainMenu.show();
                portOpener(listenTo);
                playerSituationTxt.setText("Listening for 2 users to connect.");
                readAndFeed();
            }
        }

        if(event.getSource()==readyUP) {
            //stop();
            // this button should 'lock' the server so no other clients can connect
            // - close the thread that is listening
            open = true;
        }
        //  The shutdown event and method
        if(event.getSource()==shutDown) {
            stop();
        }
        if(event.getSource()==shutDown0) {
            stop();
        }
    }

class Player implements Runnable{
    String name;
    Player opponent;
    Socket socket;


    public Player(Socket socket, String name){


    }
    @Override
    public void run(){

    }

}

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8.name());

    }


    public void stop() {
        System.exit(0);
    }*/