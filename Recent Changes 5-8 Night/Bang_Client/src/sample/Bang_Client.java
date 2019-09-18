package sample;

// Imported libraries, now external packages other than javaFX
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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
 Button submitButton;
 Button card1, card2, card3, card4, card5, card6, card7;
 Button bang1, bang2, bang3, bang4;
 HBox cardBox;

 // Declare Variables
 Socket host;
 DataOutputStream playerTransmit = null;
 DataInputStream playerRecieve = null;
 boolean playerConnected = false;
 String playerName = "";

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

  portInput = new TextField("Enter Port #");
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

  Image bangIMG = new Image(getClass().getResourceAsStream("/pictures/bang.png"));
  Image missedIMG = new Image(getClass().getResourceAsStream("/pictures/missed.png"));
  Image beerIMG = new Image(getClass().getResourceAsStream("/pictures/beer.png"));
  Image remingtonIMG = new Image(getClass().getResourceAsStream("/pictures/remington.png"));
  Image schofieldIMG = new Image(getClass().getResourceAsStream("/pictures/schofield.png"));

  card1 = new Button("card1", new ImageView(bangIMG));
  card1.setOnAction(this);
  card2 = new Button("card2", new ImageView(missedIMG));
  card2.setOnAction(this);
  card3 = new Button("card3", new ImageView(beerIMG));
  card3.setOnAction(this);
  card4 = new Button("card4", new ImageView(remingtonIMG));
  card4.setOnAction(this);
  card5 = new Button("card5", new ImageView(schofieldIMG));
  card5.setOnAction(this);
  card6 = new Button("card6", new ImageView(bangIMG));
  card6.setOnAction(this);
  card7 = new Button("card7", new ImageView(bangIMG));
  card7.setOnAction(this);

  bang1 = new Button("Bang 1");
  bang1.setOnAction(this);
  bang2 = new Button("Bang 2");
  bang2.setOnAction(this);
  bang3 = new Button("Bang 3");
  bang3.setOnAction(this);
  bang4 = new Button("Bang 4");
  bang4.setOnAction(this);

  cardBox = new HBox(card1, card2, card3, card4, card5, card6, card7);
  cardBox.setAlignment(Pos.BOTTOM_CENTER);

  TextField health = new TextField("Health: ");
  health.setEditable(false);

  TextField turn = new TextField("Who's Turn: ");
  turn.setEditable(false);

  TextField role = new TextField("Your Role: ");
  role.setEditable(false);

  TextField playerNum = new TextField("Your Player Number: ");
  playerNum.setEditable(false);

  submitButton = new Button("Send");
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

  submitButton.setFocusTraversable(false);
  submitButton.setOnAction(this);
  userControlsBox = new HBox(playerInput, submitButton, health, turn, role, playerNum, bang1, bang2, bang3, bang4);
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
       String regex = "\\*[c#nhrdstwxep]";
       String[] tokens = message.split(regex);

       for(String s : tokens){
        System.out.println(s);
       }

       String cards = tokens[8];
       String[] listcards = cards.split("\\^");

       for(String c : listcards){
        System.out.println(c);
       }

       ImageStore.card_face_images = new HashMap<String, Image>();
       //String[] wordsFileNames = {"bang", "beer", "missed", "schofield", "remington"};

       for(String c : listcards)
       {
        String image_file_name = "pictures/" + c + ".jpg";
        Image card_face_image = new Image(image_file_name);
        ImageStore.card_face_images.put(c, card_face_image);
        //button.setGraphic(new ImageView(image));

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

  if (event.getSource() == submitButton) {
   playerInput.requestFocus();
   String message = playerInput.getText();
   playerInput.clear();
   serverCommWindow.appendText(playerName + ": " + message);
   serverCommWindow.appendText("\n");
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
  if (event.getSource() == card1) {
   String message = "message";
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
  if (event.getSource() == card2) {
   String message = "message";
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
  if (event.getSource() == card3) {
   String message = "message";
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
  if (event.getSource() == card4) {
   String message = "message";
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
  if (event.getSource() == card5) {
   String message = "message";
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
  if (event.getSource() == card6) {
   String message = "message";
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
  if (event.getSource() == card7) {
   String message = "message";
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
  if (event.getSource() == bang1) {
   String message = "message";
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
   String message = "message";
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
   String message = "message";
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
   String message = "message";
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