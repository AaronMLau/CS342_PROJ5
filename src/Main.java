
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.geometry.*;
import javafx.scene.image.Image;
import java.util.HashMap;

//import ImageStore;
import javafx.event.ActionEvent;
import javafx.scene.input.*;
import java.util.Iterator;
import java.util.ArrayList;


public class Main extends Application {

	Deck card_deck;
	StackPane root_stack = new StackPane();
	Text startup_text = null;
	Group row_of_cards = new Group();
	Group group_for_role_card = new Group();
	//CardFormat role_card;
	//CardFormat selected_card;
	
	public static final int CARD_WIDTH = 150;
	public static final int CARD_HEIGHT = 215;

	public void start(Stage stage) {
		stage.setTitle("BANG!.java");


		
		//ImageStore.card_back_image = new Image("card_images/card_back.png");

		ImageStore.card_face_images = new HashMap<String, Image>();

		//String[] words_in_image_file_names = { "bang", "missed", "spades", "clubs" };

		card_deck = new Deck();

		card_deck.view = card_deck.cards.name;
		
		
		view = new ImageView(new Image(name + ".png"));
	    view.setFitHeight(215);
	    view.setFitWidth(150);
	    view.setPreserveRatio(true);
		
		
		
		
		/*
		for (String temp : card_deck) {
			System.out.println(temp);
		}
		
		
		for (int card_count = 0; card_count < 57; card_count++) {

			if(card_deck == "bang") {
				
				String image_file_name = "card_images/bang.png";

				Image card_faceup_image = new Image(image_file_name);
				String key_for_image = words_in_image_file_names[0] + card_count;
				ImageStore.card_face_images.put(key_for_image, card_faceup_image);
				
			}
			
			
			

		}
		*/

		

		Button button_to_deal_cards = new Button("DRAW");
		Button button_to_shuffle_deck = new Button("SHUFFLE");

		button_to_deal_cards.setOnAction((ActionEvent event) -> {
	

			if (startup_text != null) {

				root_stack.getChildren().remove(startup_text);
				startup_text = null;

			}

			row_of_cards.getChildren().clear();

			for (int card_index = 0; card_index < 4; card_index++) {

				//CardFormat new_card = card_deck.get_card();

				double card_position_x = 40 + (CARD_WIDTH + 20) * card_index;
				double card_position_y = 20;

				//new_card.set_card_position(card_position_x, card_position_y);

				//row_of_cards.getChildren().add() = draw;

			}

			
			

			
		});

		

		HBox pane_for_buttons = new HBox(16); // space between buttons is 16

		pane_for_buttons.getChildren().addAll(button_to_deal_cards, button_to_shuffle_deck);

		pane_for_buttons.setAlignment(Pos.CENTER); // The Box is centered
		
		pane_for_buttons.setPadding(new Insets(0, 0, 20, 0));

		BorderPane border_pane = new BorderPane();

		border_pane.setBottom(pane_for_buttons);

		Group main_group_for_cards = new Group();

		main_group_for_cards.setManaged(false);

		main_group_for_cards.getChildren().addAll(row_of_cards, group_for_role_card);

		if (startup_text != null) {

			startup_text.setFont(new Font(24));

		}

		if (startup_text != null) {

			root_stack.getChildren().addAll(border_pane, main_group_for_cards, startup_text);

		}

		else {

			root_stack.getChildren().addAll(border_pane, main_group_for_cards);

		}

		Scene scene = new Scene(root_stack, 910, 600); // Window initialize

		scene.setOnMouseClicked((MouseEvent event) -> {

			double clicked_point_x = event.getSceneX();
			double clicked_point_y = event.getSceneY();

			if (row_of_cards.getChildren().size() == 5) {

				for (Node card_as_node : row_of_cards.getChildren()) {

					//CardFormat card_in_row = (CardFormat) card_as_node;

					//if (card_in_row.contains_point(clicked_point_x, clicked_point_y)) {

						//card_in_row.turn_card();

				
						//selected_card = card_in_row;

					}
				}

				//if (role_card != null && role_card.contains_point(clicked_point_x, clicked_point_y)) {

					//role_card.turn_card();

				//}

			//}

		});



		root_stack.setBackground(null);

		scene.setFill(Color.WHITE);

		stage.setScene(scene);
		stage.show();

	}

	public static void main(String[] command_line_parameters) {
		launch(command_line_parameters);
	}
}