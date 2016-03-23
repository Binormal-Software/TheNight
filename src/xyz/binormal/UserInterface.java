package xyz.binormal;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UserInterface {

	private Game sender;
	private VBox uiPane;
	private List<Button> itemButtons;
	
	public UserInterface(Game sender){
		
		uiPane = new VBox();
		uiPane.setAlignment(Pos.TOP_RIGHT);
		
		this.sender = sender;
		itemButtons = new ArrayList<Button>();
		
	}
	
	public void updateInventory(ArrayList<JFXItem> inventory){
		
		System.out.println("Refreshing inventory panel, " + inventory.size() + " items.");
		
		
		for(Button b: itemButtons){
			uiPane.getChildren().remove(b);
		}
		
		itemButtons.clear();
		
		for(JFXItem i: inventory){
			
			Button item = new Button(i.getName().toUpperCase());
			VBox.setMargin(item, new Insets(5));
			item.setTextFill(Color.WHITE);
			item.setFont(Font.font ("Segoe UI", FontWeight.BOLD, 8));
			item.setAlignment(Pos.BOTTOM_CENTER);
			item.setStyle("-fx-background-color: #002b53aa; -fx-border-color:white; -fx-border-width: 2; -fx-border-style: solid;");
			item.setPadding(new Insets(0));
			item.setPrefSize(64, 64);
			item.setOnAction(e -> {
				sender.useItem(i);
			});
			itemButtons.add(item);
			uiPane.getChildren().add(item);
			
		}
		
		
		
		
	}
	
	public Node getPane(){
		return uiPane;
	}
	
	
	
}
