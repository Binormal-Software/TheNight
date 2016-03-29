package xyz.binormal;

import java.util.List;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Menu {

	private VBox menuBox;
	private final String FONT_TITLE = "Crimes Times Six";
	//private Runnable onClick;
	
	public Menu(GameHandler sender, String title, List<String> menuOptions){
		
		
		menuBox = new VBox();
		menuBox.setAlignment(Pos.CENTER);
		
		
		Text text = new Text(title);
		text.setFont(Font.font (FONT_TITLE, 100));
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFill(Color.WHITE);
		menuBox.setPadding(new Insets(30));
		menuBox.getChildren().add(text);
		
		Button[] button = new Button[menuOptions.size()];
		
		for(int i = 0; i < menuOptions.size(); i++){
			int index = i;
			button[i] = new Button(menuOptions.get(i));
			button[i].setOnAction(e -> {
				sender.parseInput(button[index].getText());
				//onClick.run();
			});
			button[i].setStyle(getButtonStyle(0));
			button[i].setOnMouseEntered(e -> {
				button[index].setStyle(getButtonStyle(1));
			});
			button[i].setOnMouseExited(e -> {
				button[index].setStyle(getButtonStyle(0));
			});
			//button[i].setPadding(new Insets(50));
			VBox.setMargin(button[i], new Insets(10));
			menuBox.getChildren().add(button[i]);
		}
	}

	public void bind(ReadOnlyDoubleProperty widthProperty, ReadOnlyDoubleProperty heightProperty){
		menuBox.prefWidthProperty().bind(widthProperty);
		menuBox.prefHeightProperty().bind(heightProperty);
	}
	
	public Node getMenuNode(){
		return menuBox;
	}
	
	private String getButtonStyle(int action){//0 none, 1 hovered
		
		String buttonColor;
		
		if(action == 0){
			buttonColor = "#000000";
		}else{
			buttonColor = "#00000066";
		}
		return "-fx-background-color: " + buttonColor + "; "
				+ " -fx-background-insets: 12; "
				+ " -fx-background-radius: 3;"
				+ " -fx-padding: 0 30 5 30;"
				+ " -fx-text-fill: white;"
				+ " -fx-font: 24 \"" + FONT_TITLE + "\";";
		
		
	}
	
}
