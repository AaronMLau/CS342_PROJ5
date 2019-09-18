package sample;

// Imported libraries, now external packages other than javaFX
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Pair;


public class Bang_Client extends Application implements EventHandler < ActionEvent > {

 public static void main(String[] argv) {
  launch(argv);
 }


 //Declare JavaFX Items
 Stage mainMenu;
 HBox firstScreenBox;
 TextField addressInput;
 TextField portInput;
 TextField playerNameArea;
 Button joinServerButton;
 Scene openingScene;
 Scene gameplayScene;
 VBox sendToServerBox;
 HBox userControlsBox;
 TextArea serverCommWindow;
 TextField playerInput;
 Button card1, card2, card3, card4, card5, card6, card7;
 Button bang1, bang2, bang3, bang4;
 Button skipTurn;
 Button cantMiss;
 HBox cardBox;
 TextField health;
 TextField turn;
 TextField role;
 TextField playerNum;

 // Declare Variables
 Socket host;
 DataOutputStream playerTransmit = null;
 DataInputStream playerRecieve = null;
 boolean playerConnected = false;
 String playerName = "";
 Image bangIMG;
 Image missedIMG;
 Image beerIMG;
 Image remingtonIMG;
 Image schofieldIMG;
 boolean cardSetup = true;
 ArrayList<String> cardOrder = new ArrayList<String>();
 String playerID;
 String playerRole;
 String playerHealth;
 String playerTurn;
 boolean waitForBang = true;
 boolean useBang = false;
 boolean useRemington = false;
 boolean useSchofield = false;
 boolean card1disabled = false;
 boolean card2disabled = false;
 boolean card3disabled = false;
 boolean card4disabled = false;
 boolean card5disabled = false;
 boolean card6disabled = false;
 boolean card7disabled = false;

 //******UI (JAVAFX) COMPONENT OF PROGRAM******//
 private Scene openingScene() {

  BackgroundImage titleImage= new BackgroundImage(new Image("/pictures/bang.jpg",400,352,false,true),
          BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);

  joinServerButton = new Button();
  joinServerButton.setText("Connect");
  joinServerButton.setOnAction(this);

  addressInput = new TextField("127.0.0.1");
  addressInput.setPrefWidth(70);
  addressInput.setEditable(false);

  portInput = new TextField("8080");
  portInput.setPrefWidth(80);

  playerNameArea = new TextField("Enter Name");
  playerNameArea.setPrefWidth(80);

  firstScreenBox = new HBox(portInput, addressInput, playerNameArea, joinServerButton);
  firstScreenBox.setAlignment(Pos.BOTTOM_CENTER);
  firstScreenBox.setBackground(new Background(titleImage));

  StackPane layout = new StackPane();
  layout.getChildren().add(firstScreenBox);

  return new Scene(layout, 400, 350);
 }

 // Make and return the chat box scene.
 private Scene gameplayScene() {

  bangIMG = new Image(getClass().getResourceAsStream("/pictures/bang.png"));
  missedIMG = new Image(getClass().getResourceAsStream("/pictures/missed.png"));
  beerIMG = new Image(getClass().getResourceAsStream("/pictures/beer.png"));
  remingtonIMG = new Image(getClass().getResourceAsStream("/pictures/remington.png"));
  schofieldIMG = new Image(getClass().getResourceAsStream("/pictures/schofield.png"));

  card1 = new Button("");
  card1.setOnAction(this);
  card2 = new Button("");
  card2.setOnAction(this);
  card3 = new Button("");
  card3.setOnAction(this);
  card4 = new Button("");
  card4.setOnAction(this);
  card5 = new Button("");
  card5.setOnAction(this);
  card6 = new Button("");
  card6.setOnAction(this);
  card7 = new Button("");
  card7.setOnAction(this);

  bang1 = new Button("Shoot Player 1");
  bang1.setDisable(true);
  bang1.setOnAction(this);
  bang2 = new Button("Shoot Player 2");
  bang2.setDisable(true);
  bang2.setOnAction(this);
  bang3 = new Button("Shoot Player 3");
  bang3.setDisable(true);
  bang3.setOnAction(this);
  bang4 = new Button("Shoot Player 4");
  bang4.setDisable(true);
  bang4.setOnAction(this);

  skipTurn = new Button("Skip Turn");
  skipTurn.setOnAction(this);

  cantMiss = new Button("Can't Miss");
  cantMiss.setOnAction(this);

  cardBox = new HBox(card1, card2, card3, card4, card5, card6, card7);
  cardBox.setAlignment(Pos.BOTTOM_CENTER);

  health = new TextField("Health: ");
  turn = new TextField("Your Turn: ");
  role = new TextField("Your Role: ");
  playerNum = new TextField("Your Player Number: ");
  health.setEditable(false);
  turn.setEditable(false);
  role.setEditable(false);
  playerNum.setEditable(false);

  playerInput = new TextField();
  serverCommWindow = new TextArea();

  serverCommWindow.setPrefHeight(400);
  serverCommWindow.setPrefWidth(400);
  serverCommWindow.setEditable(false);
  serverCommWindow.setMouseTransparent(true);
  serverCommWindow.setFocusTraversable(false);
  serverCommWindow.setWrapText(true);

  playerInput.setPrefWidth(212);
  playerInput.requestFocus();

  userControlsBox = new HBox(health, turn, role, playerNum, cantMiss ,skipTurn, bang1, bang2, bang3, bang4);
  userControlsBox.setAlignment(Pos.CENTER);

  sendToServerBox = new VBox(serverCommWindow, userControlsBox,cardBox);
  sendToServerBox.setAlignment(Pos.BOTTOM_CENTER);
  StackPane layout = new StackPane();
  layout.getChildren().add(sendToServerBox);

  return new Scene(layout, 800, 800);
 }

 @Override
 public void start(Stage primaryStage) {
  mainMenu = primaryStage;
  mainMenu.setTitle("BANG! Player Window");
  mainMenu.setScene(openingScene());
  mainMenu.show();
 }

 //******SERVER COMPONENT OF PROGRAM******//
 @Override
 public void handle(ActionEvent event) {

  if (event.getSource() == joinServerButton) {
   String Address = addressInput.getText();
   int PORT = Integer.parseInt(portInput.getText());

   if (connectToServer(Address, PORT) != 0) {
    System.out.println("Server not found");
   } else {
    mainMenu.setScene(gameplayScene());
    mainMenu.show();
    Runnable listenReadFeed = () -> {
     @SuppressWarnings("not needed")
     String message = null;
     System.out.println("Establishing connection with server");
     playerName = playerNameArea.getText();
     while (true) {
      try {
       if (playerConnected == false) {
        playerTransmit.writeUTF(playerName);
        playerConnected = true;
       }
       message = playerRecieve.readUTF();
       serverCommWindow.appendText(message);
       serverCommWindow.appendText("\n");

       if(message.charAt(0) == '*'){
        String regex = "\\*[c#nhrdstwxep$]";
        String[] tokens = message.split(regex);
        playerHealth = tokens[4];
        playerTurn = tokens[7];


        Platform.runLater(new Runnable() {
         @Override
         public void run() {

          try {
           if(playerTurn.equals("false")){
            card1.setDisable(true); card2.setDisable(true); card3.setDisable(true); card4.setDisable(true); card5.setDisable(true); card6.setDisable(true); card7.setDisable(true); skipTurn.setDisable(true);
           }
           if(playerTurn.equals("true")){
            if(card1disabled == false){ card1.setDisable(false);}
            if(card2disabled == false){ card2.setDisable(false);}
            if(card3disabled == false){ card3.setDisable(false);}
            if(card4disabled == false){ card4.setDisable(false);}
            if(card5disabled == false){ card5.setDisable(false);}
            if(card6disabled == false){ card6.setDisable(false);}
            if(card7disabled == false){ card7.setDisable(false);}
            skipTurn.setDisable(false);
           }
           health.setText("Health: " + playerHealth);
           turn.setText("Your Turn: " + playerTurn);
           role.setText("Your role: " + playerRole);
           playerNum.setText("Your Player Number: " + playerID);

           int myHealth = Integer.parseInt(playerHealth);

           if(myHealth <= 0)
           {
            card1.setVisible(false);
            card2.setVisible(false);
            card3.setVisible(false);
            card4.setVisible(false);
            card5.setVisible(false);
            card6.setVisible(false);
            card7.setVisible(false);
            bang1.setVisible(false);
            bang2.setVisible(false);
            bang3.setVisible(false);
            bang4.setVisible(false);
           }
          } catch (Exception e) {
           e.printStackTrace();
          } finally {
           Thread.yield();
          }
         }

        });

        for(String s : tokens){
         System.out.println(s);
        }

        if(cardSetup) {
         playerID = tokens[2];
         playerRole = tokens[3];
         String cards = tokens[8];
         String names = tokens[9];

         String[] listcards = cards.split("\\^");
         String[] playerNames = names.split("\\^");

         cardSetup = false;
         ArrayList<Image> listImages = new ArrayList<>();
         for (String c : listcards) {
          cardOrder.add(c);
          switch (c) {
           case "bang":
            listImages.add(bangIMG);
            break;
           case "missed":
            listImages.add(missedIMG);
            break;
           case "beer":
            listImages.add(beerIMG);
            break;
           case "schofield":
            listImages.add(schofieldIMG);
            break;
           case "remington":
            listImages.add(remingtonIMG);
            break;
           default:
            System.out.print("Error setting card images.\n");
            break;
          }
         }

         Platform.runLater(new Runnable() {
          @Override
          public void run() {

           try {
            bang1.setText("Shoot " + playerNames[0]);
            bang2.setText("Shoot " + playerNames[1]);
            bang3.setText("Shoot " + playerNames[2]);
            bang4.setText("Shoot " + playerNames[3]);
            card1.setGraphic(new ImageView(listImages.get(0)));
            card2.setGraphic(new ImageView(listImages.get(1)));
            card3.setGraphic(new ImageView(listImages.get(2)));
            card4.setGraphic(new ImageView(listImages.get(3)));
            card5.setGraphic(new ImageView(listImages.get(4)));
            card6.setGraphic(new ImageView(listImages.get(5)));
            card7.setGraphic(new ImageView(listImages.get(6)));
           } catch (Exception e) {
            e.printStackTrace();
           } finally {
            Thread.yield();
           }
          }

         });
        }
       }

      } catch (IOException e) {
       System.out.println("Someone has shut down the server, exiting...");
       System.exit(0);
      }
     }
    };
    Thread receiver_thread = new Thread(listenReadFeed);
    receiver_thread.start();
   }
  }

  if (event.getSource() == card1) {
   String message = cardOrder.get(0);
   card1disabled = true;
   card1.setDisable(true); card2.setDisable(true); card3.setDisable(true); card4.setDisable(true); card5.setDisable(true); card6.setDisable(true); card7.setDisable(true); skipTurn.setDisable(true);
   if(message.equals("bang" )|| message.equals("schofield") || message.equals("remington")){
    bang1.setDisable(false);
    bang2.setDisable(false);
    bang3.setDisable(false);
    bang4.setDisable(false);
    if(playerID.equals("1")){bang1.setDisable(true);}
    if(playerID.equals("2")){bang2.setDisable(true);}
    if(playerID.equals("3")){bang3.setDisable(true);}
    if(playerID.equals("4")){bang4.setDisable(true);}
    if(message.equals("bang")){ useBang = true; }
    if(message.equals("remington")){ useRemington = true; }
    if(message.equals("schofield")){ useSchofield = true; }
   }
   else{
    waitForBang = false;
   }
   try {
    if(waitForBang == false){
     playerTransmit.writeUTF(message);
    }
    waitForBang = true;
    Thread.sleep(500);
   }catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }
  if (event.getSource() == card2) {
   String message = cardOrder.get(1);
   card2disabled = true;
   card1.setDisable(true); card2.setDisable(true); card3.setDisable(true); card4.setDisable(true); card5.setDisable(true); card6.setDisable(true); card7.setDisable(true); skipTurn.setDisable(true);
   if(message.equals("bang" )|| message.equals("schofield") || message.equals("remington")){
    bang1.setDisable(false);
    bang2.setDisable(false);
    bang3.setDisable(false);
    bang4.setDisable(false);
    if(playerID.equals("1")){bang1.setDisable(true);}
    if(playerID.equals("2")){bang2.setDisable(true);}
    if(playerID.equals("3")){bang3.setDisable(true);}
    if(playerID.equals("4")){bang4.setDisable(true);}
    if(message.equals("bang")){ useBang = true; }
    if(message.equals("remington")){ useRemington = true; }
    if(message.equals("schofield")){ useSchofield = true; }
   }
   else{
    waitForBang = false;
   }
   try {
    if(waitForBang == false){
     playerTransmit.writeUTF(message);
    }
    waitForBang = true;
    Thread.sleep(500);
   }catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }

  if (event.getSource() == card3) {
   String message = cardOrder.get(2);
   card3disabled = true;
   card1.setDisable(true); card2.setDisable(true); card3.setDisable(true); card4.setDisable(true); card5.setDisable(true); card6.setDisable(true); card7.setDisable(true); skipTurn.setDisable(true);
   if(message.equals("bang" )|| message.equals("schofield") || message.equals("remington")){
    bang1.setDisable(false);
    bang2.setDisable(false);
    bang3.setDisable(false);
    bang4.setDisable(false);
    if(playerID.equals("1")){bang1.setDisable(true);}
    if(playerID.equals("2")){bang2.setDisable(true);}
    if(playerID.equals("3")){bang3.setDisable(true);}
    if(playerID.equals("4")){bang4.setDisable(true);}
    if(message.equals("bang")){ useBang = true; }
    if(message.equals("remington")){ useRemington = true; }
    if(message.equals("schofield")){ useSchofield = true; }
   }
   else{
    waitForBang = false;
   }
   try {
    if(waitForBang == false){
     playerTransmit.writeUTF(message);
    }
    waitForBang = true;
    Thread.sleep(500);
   }catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }

  if (event.getSource() == card4) {
   String message = cardOrder.get(3);
   card4disabled = true;
   card1.setDisable(true); card2.setDisable(true); card3.setDisable(true); card4.setDisable(true); card5.setDisable(true); card6.setDisable(true); card7.setDisable(true); skipTurn.setDisable(true);
   if(message.equals("bang" )|| message.equals("schofield") || message.equals("remington")){
    bang1.setDisable(false);
    bang2.setDisable(false);
    bang3.setDisable(false);
    bang4.setDisable(false);
    if(playerID.equals("1")){bang1.setDisable(true);}
    if(playerID.equals("2")){bang2.setDisable(true);}
    if(playerID.equals("3")){bang3.setDisable(true);}
    if(playerID.equals("4")){bang4.setDisable(true);}
    if(message.equals("bang")){ useBang = true; }
    if(message.equals("remington")){ useRemington = true; }
    if(message.equals("schofield")){ useSchofield = true; }
   }
   else{
    waitForBang = false;
   }
   try {
    if(waitForBang == false){
     playerTransmit.writeUTF(message);
    }
    waitForBang = true;
    Thread.sleep(500);
   }catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }

  if (event.getSource() == card5) {
   String message = cardOrder.get(4);
   card5disabled = true;
   card1.setDisable(true); card2.setDisable(true); card3.setDisable(true); card4.setDisable(true); card5.setDisable(true); card6.setDisable(true); card7.setDisable(true); skipTurn.setDisable(true);
   if(message.equals("bang" )|| message.equals("schofield") || message.equals("remington")){
    bang1.setDisable(false);
    bang2.setDisable(false);
    bang3.setDisable(false);
    bang4.setDisable(false);
    if(playerID.equals("1")){bang1.setDisable(true);}
    if(playerID.equals("2")){bang2.setDisable(true);}
    if(playerID.equals("3")){bang3.setDisable(true);}
    if(playerID.equals("4")){bang4.setDisable(true);}
    if(message.equals("bang")){ useBang = true; }
    if(message.equals("remington")){ useRemington = true; }
    if(message.equals("schofield")){ useSchofield = true; }
   }
   else{
    waitForBang = false;
   }
   try {
    if(waitForBang == false){
     playerTransmit.writeUTF(message);
    }
    waitForBang = true;
    Thread.sleep(500);
   }catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }

  if (event.getSource() == card6) {
   String message = cardOrder.get(5);
   card6disabled = true;
   card1.setDisable(true); card2.setDisable(true); card3.setDisable(true); card4.setDisable(true); card5.setDisable(true); card6.setDisable(true); card7.setDisable(true); skipTurn.setDisable(true);
   if(message.equals("bang" )|| message.equals("schofield") || message.equals("remington")){
    bang1.setDisable(false);
    bang2.setDisable(false);
    bang3.setDisable(false);
    bang4.setDisable(false);
    if(playerID.equals("1")){bang1.setDisable(true);}
    if(playerID.equals("2")){bang2.setDisable(true);}
    if(playerID.equals("3")){bang3.setDisable(true);}
    if(playerID.equals("4")){bang4.setDisable(true);}
    if(message.equals("bang")){ useBang = true; }
    if(message.equals("remington")){ useRemington = true; }
    if(message.equals("schofield")){ useSchofield = true; }
   }
   else{
    waitForBang = false;
   }
   try {
    if(waitForBang == false){
     playerTransmit.writeUTF(message);
    }
    waitForBang = true;
    Thread.sleep(500);
   }catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }

  if (event.getSource() == card7) {
   String message = cardOrder.get(6);
   card7disabled = true;
   card1.setDisable(true); card2.setDisable(true); card3.setDisable(true); card4.setDisable(true); card5.setDisable(true); card6.setDisable(true); card7.setDisable(true); skipTurn.setDisable(true);
   if(message.equals("bang" )|| message.equals("schofield") || message.equals("remington")){
    bang1.setDisable(false);
    bang2.setDisable(false);
    bang3.setDisable(false);
    bang4.setDisable(false);
    if(playerID.equals("1")){bang1.setDisable(true);}
    if(playerID.equals("2")){bang2.setDisable(true);}
    if(playerID.equals("3")){bang3.setDisable(true);}
    if(playerID.equals("4")){bang4.setDisable(true);}
    if(message.equals("bang")){ useBang = true; }
    if(message.equals("remington")){ useRemington = true; }
    if(message.equals("schofield")){ useSchofield = true; }
   }
   else{
    waitForBang = false;
   }
   try {
    if(waitForBang == false){
     playerTransmit.writeUTF(message);
    }
    waitForBang = true;
    Thread.sleep(500);
   }catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }

  if (event.getSource() == bang1) {
   String message = "";
   if(useBang == true){ message = "bang 1";}
   if(useRemington == true){ message = "remington 1";}
   if(useSchofield == true){ message = "schofield 1";}
   if(card1disabled == false){ card1.setDisable(false);}
   if(card2disabled == false){ card2.setDisable(false);}
   if(card3disabled == false){ card3.setDisable(false);}
   if(card4disabled == false){ card4.setDisable(false);}
   if(card5disabled == false){ card5.setDisable(false);}
   if(card6disabled == false){ card6.setDisable(false);}
   if(card7disabled == false){ card7.setDisable(false);}

   bang1.setDisable(true);
   bang2.setDisable(true);
   bang3.setDisable(true);
   bang4.setDisable(true);
   useSchofield = false;
   useRemington = false;
   useBang = false;
   try {
    playerTransmit.writeUTF(message);
    Thread.sleep(500);
   } catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }
  if (event.getSource() == bang2) {
   String message = "";
   if(useBang == true){ message = "bang 2";}
   if(useRemington == true){ message = "remington 2";}
   if(useSchofield == true){ message = "schofield 2";}
   if(card1disabled == false){ card1.setDisable(false);}
   if(card2disabled == false){ card2.setDisable(false);}
   if(card3disabled == false){ card3.setDisable(false);}
   if(card4disabled == false){ card4.setDisable(false);}
   if(card5disabled == false){ card5.setDisable(false);}
   if(card6disabled == false){ card6.setDisable(false);}
   if(card7disabled == false){ card7.setDisable(false);}
   bang1.setDisable(true);
   bang2.setDisable(true);
   bang3.setDisable(true);
   bang4.setDisable(true);
   useSchofield = false;
   useRemington = false;
   useBang = false;
   try {
    playerTransmit.writeUTF(message);
    Thread.sleep(500);
   } catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }
  if (event.getSource() == bang3) {
   String message = "";
   if(useBang == true){ message = "bang 3";}
   if(useRemington == true){ message = "remington 3";}
   if(useSchofield == true){ message = "schofield 3";}
   if(card1disabled == false){ card1.setDisable(false);}
   if(card2disabled == false){ card2.setDisable(false);}
   if(card3disabled == false){ card3.setDisable(false);}
   if(card4disabled == false){ card4.setDisable(false);}
   if(card5disabled == false){ card5.setDisable(false);}
   if(card6disabled == false){ card6.setDisable(false);}
   if(card7disabled == false){ card7.setDisable(false);}
   bang1.setDisable(true);
   bang2.setDisable(true);
   bang3.setDisable(true);
   bang4.setDisable(true);
   useSchofield = false;
   useRemington = false;
   useBang = false;
   try {
    playerTransmit.writeUTF(message);
    Thread.sleep(500);
   } catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }
  if (event.getSource() == bang4) {
   String message = "";
   if(useBang == true){ message = "bang 4";}
   if(useRemington == true){ message = "remington 4";}
   if(useSchofield == true){ message = "schofield 4";}
   if(card1disabled == false){ card1.setDisable(false);}
   if(card2disabled == false){ card2.setDisable(false);}
   if(card3disabled == false){ card3.setDisable(false);}
   if(card4disabled == false){ card4.setDisable(false);}
   if(card5disabled == false){ card5.setDisable(false);}
   if(card6disabled == false){ card6.setDisable(false);}
   if(card7disabled == false){ card7.setDisable(false);}
   bang1.setDisable(true);
   bang2.setDisable(true);
   bang3.setDisable(true);
   bang4.setDisable(true);
   useSchofield = false;
   useRemington = false;
   useBang = false;
   try {
    playerTransmit.writeUTF(message);
    Thread.sleep(500);
   } catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }

  if (event.getSource() == skipTurn) {
   String message = "skipTurn";
   try {
    playerTransmit.writeUTF(message);
    Thread.sleep(500);
   } catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }
  if (event.getSource() == cantMiss) {
   String message = "cantMiss";
   try {
    playerTransmit.writeUTF(message);
    Thread.sleep(500);
   } catch(InterruptedException ex)
   {
    Thread.currentThread().interrupt();
   }
   catch (IOException e) {
    System.out.println("Unable to send message.");
    System.exit(-1);
   }
  }
 }





 @Override
 public void stop() {
  try {
   playerTransmit.close();
   playerRecieve.close();
   host.close();
  } catch (IOException e) {
   System.out.println("Failed to close serverSocket");
   System.exit(-1);
  }
 }


 private int connectToServer(String address, int PORT) {
  host = null;
  try {
   host = new Socket("localhost", PORT);
  } catch (IOException e) {
   System.out.println("Error cannot connect.");
   return 1;
  }
  System.out.println("Connection made.");

  try {
   playerTransmit = new DataOutputStream(host.getOutputStream());
   playerRecieve = new DataInputStream(host.getInputStream());
  } catch (IOException e) {
   System.out.println("Error when connecting to the server.");
   return 2;
  }
  System.out.println("Connected Properly.");
  return 0;
 }

}