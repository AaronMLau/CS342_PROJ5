package sample;

// Imported libraries, now external packages other than javaFX
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
 VBox firstScreenBox;
 TextField addressInput;
 TextField portInput;
 TextField playerNameArea;
 Button joinServerButton;
 Scene openingScene;
 Scene gamplayScene;
 VBox sendToServerBox;
 HBox userControlsBox;
 TextArea serverCommWindow;
 TextField playerInput;
 Button submitButton;

 // Declare Variables
 Socket host;
 DataOutputStream playerTransmit = null;
 DataInputStream playerRecieve = null;
 boolean playerConnected = false;
 String playerName = "";

 //******UI (JAVAFX) COMPONENT OF PROGRAM******//
 private Scene openingScene() {
  joinServerButton = new Button();
  joinServerButton.setText("Connect");
  joinServerButton.setOnAction(this);

  addressInput = new TextField("127.0.0.1");
  addressInput.setPrefWidth(100);

  portInput = new TextField("8080");
  portInput.setPrefWidth(100);

  playerNameArea = new TextField("Enter Name Here");
  playerNameArea.setPrefWidth(100);

  firstScreenBox = new VBox(addressInput, portInput, playerNameArea, joinServerButton);
  firstScreenBox.setAlignment(Pos.CENTER);

  StackPane layout = new StackPane();
  layout.getChildren().add(firstScreenBox);

  return new Scene(layout, 400, 400);
 }

 // Make and return the chat box scene.
 private Scene gamplayScene() {
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
  userControlsBox = new HBox(playerInput, submitButton);
  userControlsBox.setAlignment(Pos.CENTER);

  sendToServerBox = new VBox(serverCommWindow, userControlsBox);
  sendToServerBox.setAlignment(Pos.CENTER);
  StackPane layout = new StackPane();
  layout.getChildren().add(sendToServerBox);

  return new Scene(layout, 400, 500);
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
    mainMenu.setScene(gamplayScene());
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