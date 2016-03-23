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
	
	private double playerSize = 0.8d;
	
	protected Player player;
	protected Map map;
	protected Narrator narrator;
	protected UserInterface ui;
	
	
	public Game(GameHandler sender, GraphicsContext gc, Boolean showIntro){
		
		this.gc = gc;
		levelPane = new BorderPane();
		levelPane.setPadding(new Insets(10));
		
		narrator = new Narrator();
		ui = new UserInterface(this);
		
		levelPane.setBottom(narrator.getTextNode());
		BorderPane.setAlignment(narrator.getTextNode(),Pos.CENTER);
		levelPane.setRight(ui.getPane());
		
		for(int i = 0; i < 4; i++){
			keyHeld.add(false);
		}
		
		try {
			
			player = new Player("res/tokinoiori08.png", 0.2d);
			map = new Map("./res/map.tmx");
			
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
		    map.resolutionChange("x", (double)newValue - (double)oldValue);
		});
		screenHeight.addListener((observable, oldValue, newValue) -> {
		    System.out.println("screenHeight changed, redrawing...");
		    map.resolutionChange("y", (double)newValue - (double)oldValue);
		});
				
	}
	
	public void update(double tpf, double deltaTime){
		
		
		gc.clearRect(0, 0, screenWidth.doubleValue(), screenHeight.doubleValue()); // clear frame
		
		if(player.walking){
			map.movePlayer(player, player.walkingSpeed*playerSize, player.walkingDirection);
		}
		
		map.drawMap(gc, 0, 3);
		drawCharacters(deltaTime);
		map.drawMap(gc, 3, 4);
		
		
	}
	
	private void drawCharacters(double deltaTime){
		
		double walkingX = (screenWidth.doubleValue()/2)-(player.playerSprite.chunkWidth/2) + player.deltaX;;
		double walkingY = (screenHeight.doubleValue()/2)-(player.playerSprite.chunkHeight/2) + player.deltaY;
		
		if(player.walking){
			
			player.playerSprite.duration = (0.2d*(1d/player.walkingSpeed));
			gc.drawImage( player.playerSprite.getFrame(deltaTime, player.walkingDirection), walkingX, walkingY, playerSize*32, playerSize*48 );
			
		}else{
			gc.drawImage( player.playerSprite.getFrame(0, player.walkingDirection), walkingX, walkingY, playerSize*32, playerSize*48 );
			
		}
		
		
	}

	public void processKeys(KeyCode keyCode){
		
		if(movementDirection(keyCode)!=-1){
			
			keyHeld.set(movementDirection(keyCode), true);
			player.walkingDirection = movementDirection(keyCode);
			player.walking = true;
			
		}
		
        if(keyCode == KeyCode.SPACE || keyCode == KeyCode.SHIFT){
        	player.walkingSpeed = 10;//3
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
		
		JFXEvent e = map.getEventByItem(player, item);
		
		if(e!=null && !e.eventDone()){
			narrateItem(item, "use");
			
			if(e.getEventType().equals(e.itemEvent())){
				player.addToInventory(e.getGiveItem());
			}
			
			if(item.isRemovable()){
				player.removeFromInventory(item);
			}
			
			ui.updateInventory(player.getInventory());
			e.doEvent();
			
		}else{
			narrateItem(item, "fail");
		}
		
	}
	
	protected void checkForItems(){
		
		if(map.nearestItem(player)!=null){
			JFXItem item = map.nearestItem(player);
			item.take();
			narrateItem(item, "find");
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
	
	private void narrateItem(JFXItem item, String messageType){
		
		File file = new File("res/strings/" + item.getName() + "_" + messageType);
		if(file.exists()){
			narrator.narrate(file, 10, map);
		}else{
			narrator.narrate("" + item.getName() + "; " + file.getPath() + " not found...", 10, map);
		}
		
	}
	
	public Node getLevelNode(){
		return levelPane;
	}
	
	
	
	
}
