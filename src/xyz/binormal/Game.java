package xyz.binormal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class Game {

	private BorderPane levelPane;
	
	private GraphicsContext gc;
	private ArrayList<Boolean> keyHeld = new ArrayList<Boolean>();
	private ReadOnlyDoubleProperty screenWidth;
	private ReadOnlyDoubleProperty screenHeight;
	protected Player player;
	protected Map map;
	protected Narrator narrator;
	protected UserInterface ui;
	
	public Game(GameHandler sender, GraphicsContext gc, Boolean showIntro){
		
		this.gc = gc;
		levelPane = new BorderPane();
		
		//levelPane.setStyle("-fx-background-color: #4f06ad33;");
		levelPane.setPadding(new Insets(10));
		
		narrator = new Narrator();
		ui = new UserInterface(this);
		
		levelPane.setBottom(narrator.getTextNode());
		BorderPane.setAlignment(narrator.getTextNode(),Pos.CENTER);
		
		levelPane.setRight(ui.getPane());
		//BorderPane.setAlignment(ui.getPane(),Pos.CENTER);
		
		for(int i = 0; i < 4; i++){
			keyHeld.add(false);
		}
		
		try {
			
			player = new Player("res/tokinoiori08.png", 0.2d);
			map = new Map("./res/forest.tmx");
			
			player.addToInventory(new JFXItem("cellphone", false));
			
			ui.updateInventory(player.getInventory());
			
		} catch (IOException | SAXException | ParserConfigurationException e) {
			System.err.println("Error loading resources");
			e.printStackTrace();
		}
		finally{
			
			if(showIntro)
				playIntro();
			
		}
		
		
		
	}

	public void bind(ReadOnlyDoubleProperty widthProperty, ReadOnlyDoubleProperty heightProperty){
		
		screenWidth = widthProperty;
		screenHeight = heightProperty;
		
		levelPane.prefWidthProperty().bind(screenWidth);
		levelPane.prefHeightProperty().bind(screenHeight);
		
		narrator.bind(widthProperty);
		
		map.setBounds(screenWidth.doubleValue(), screenHeight.doubleValue());
		
		screenWidth.addListener((observable, oldValue, newValue) -> {
		    System.out.println("screenWidth changed, redrawing...");
		    map.tileMap.resolutionChange("x", (double)newValue - (double)oldValue);
		});
		screenHeight.addListener((observable, oldValue, newValue) -> {
		    System.out.println("screenHeight changed, redrawing...");
		    map.tileMap.resolutionChange("y", (double)newValue - (double)oldValue);
		});
				
	}
	
	public void update(double tpf, double deltaTime){
		
		
		gc.clearRect(0, 0, screenWidth.doubleValue(), screenHeight.doubleValue()); // clear frame
		
		if(player.walking){
			map.tileMap.scrollMap(player.walkingSpeed, player.walkingDirection);
		}
		
		map.tileMap.drawMap(gc, 0, 2);
		drawCharacters(deltaTime);
		map.tileMap.drawMap(gc, 2, 3);
		
		
	}
	
	private void drawCharacters(double deltaTime){
		
		double walkingX = (screenWidth.doubleValue()/2)-(player.playerSprite.chunkWidth/2);
		double walkingY = (screenHeight.doubleValue()/2)-(player.playerSprite.chunkHeight/2);
		
		if(player.walking){
			
			player.playerSprite.duration = (0.2d*(1d/player.walkingSpeed));
			gc.drawImage( player.playerSprite.getFrame(deltaTime, player.walkingDirection), walkingX, walkingY );
			
		}else{
			gc.drawImage( player.playerSprite.getFrame(0, player.walkingDirection), walkingX, walkingY );
			
		}
		
		
	}

	public void processKeys(KeyCode keyCode){
		
		if(movementDirection(keyCode)!=-1){
			
			keyHeld.set(movementDirection(keyCode), true);
			player.walkingDirection = movementDirection(keyCode);
			player.walking = true;
			
		}
		
        if(keyCode == KeyCode.SPACE || keyCode == KeyCode.SHIFT){
        	player.walkingSpeed = 10;//2
        }  
        
        if(keyCode == KeyCode.DIGIT1){
        	narrator.narrate(System.currentTimeMillis() + "", 5, map);
        } 
		
	}
	
	public void releaseKeys(KeyCode keyCode){ 
		
		
		if(movementDirection(keyCode)!=-1){
			keyHeld.set(movementDirection(keyCode), false);
		}
        
        if(!keyHeld.contains(true)){
        	player.walking = false;
        }else{
        	if(movementDirection(keyCode) == player.walkingDirection){
				player.walkingDirection = keyHeld.indexOf(true);
			}
        }
        
        if(keyCode.toString() == "SPACE" || keyCode.toString() == "SHIFT"){
        	player.walkingSpeed = 1;
        }  
        
        if(keyCode.toString() == "ENTER"){
        	narrator.continueNarration();
        	checkForItems();
        }  
		
	}
	
	protected void useItem(JFXItem item){
		
		if(map.itemUsable(item)){
			File file = new File("res/strings/" + item.getName());
			if(file.exists()){
				narrator.narrate(file, 10, map);
			}else{
				narrator.narrate("You used the " + item.getName() + "!", 10, map);
			}
			
			if(item.isRemovable()){
				player.removeFromInventory(item);
				ui.updateInventory(player.getInventory());
			}
		}else{
			narrator.narrate("You can't use the " + item.getName() + " here...", 10, map);
		}
		
	}
	
	protected void checkForItems(){
		
		if(map.nearItem()){
			JFXItem item = map.nearestItem();
			item.take();
			narrator.narrate("You found a " + item.getName(), 10, map);
			player.addToInventory(item);
			ui.updateInventory(player.getInventory());
		}
		
	}
	
	private int movementDirection(KeyCode keyCode){ // 3 = up; 2 = right, 1 = left, 0 = down
		
		switch(keyCode.toString()){
		case "UP": return 3;
		case "W": return 3;
		case "RIGHT": return 2;
		case "D": return 2;
		case "LEFT": return 1;
		case "A": return 1;
		case "DOWN": return 0;
		case "S": return 0;
		
		
		default: return -1;
		
		
		}
		
		
	}
	
	private void playIntro(){
		
		narrator.narrate(new File("res/strings/intro"), 8, map);
		
		
	}
	
	public Node getLevelNode(){
		return levelPane;
	}
	
	
	
	
}
