package xyz.binormal;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class UserInterface {

	private Game sender;
	private VBox uiPane;
	private ArrayList<Button> itemButtons;
	
	public UserInterface(Game sender){
		
		uiPane = new VBox();
		uiPane.setAlignment(Pos.TOP_RIGHT);
		
		this.sender = sender;
		itemButtons = new ArrayList<Button>();
		
		Button timeButton = new Button();
		timeButton.setText("Time");
		timeButton.setOnAction(e -> {
			sender.narrator.narrate("The current time is: " + sender.map.getFormattedTime(), 10, sender.map);
		});
		//uiPane.getChildren().add(timeButton);
	}
	
	public void updateInventory(ArrayList<JFXItem> inventory){
		
		
		for(Button b: itemButtons){
			uiPane.getChildren().remove(b);
		}
		
		itemButtons.clear();
		
		for(JFXItem i: inventory){
			
			Button item = new Button(i.getName());
			VBox.setMargin(item, new Insets(5));
			item.setPadding(new Insets(10));
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
