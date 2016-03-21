package xyz.binormal;

import java.io.File;
import java.util.Arrays;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

public class GameHandler extends JFXGameEngine {

	public static void main(String[] args) {
		
		gameProperties.set("Width", 900);
		gameProperties.set("Height", 600);
		gameProperties.set("Background", Color.web("#002b53"));
		gameProperties.set("Title", "The Night");
		
		launch(args);
	}

	
	Menu mainMenu;
	Menu introMenu;
	Game level;
	MediaPlayer musicPlayer;
	
	@Override
	protected void initialize() {
		
		Media title = new Media(new File("./res/title.mp3").toURI().toString());
		musicPlayer = new MediaPlayer(title);
		musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		//musicPlayer.play();
		
		mainMenu = new Menu(this, "The Night", Arrays.asList("Play", "Options", "About", "Quit"));
		mainMenu.bind(gameScene.widthProperty(), gameScene.heightProperty());
		rootNode().add(mainMenu.getMenuNode());
		
		
		gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

	        @Override
	        public void handle(KeyEvent keyEvent) {
	        	if(level!=null){
	        		if(rootNode().contains(level.getLevelNode())){
	        			level.processKeys(keyEvent.getCode());
	        		}
	            }  
	        }
	    });
		
		gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			
			@Override
	        public void handle(KeyEvent keyEvent) {
	        	if(level!=null){
	        		if(rootNode().contains(level.getLevelNode())){
	        			level.releaseKeys(keyEvent.getCode());
	        		}
	            }  
			}
		});
		
		
	}

	@Override
	protected void update(double tpf) {
		
		if(level!=null)
			level.update(tpf, runningTime());
		
	}
	
	@Override
	protected GameHandler getInstance(){
		return this;
	}

	protected void parseInput(String input){
		System.out.println(input);
		
		switch(input.toLowerCase()){
		
		case "play": selectIntro(); break;
		case "quit": System.exit(0); break;
		case "yes": loadLevel(true); break;
		case "no": loadLevel(false); break;
		
		}
	}
	
	private void selectIntro(){
		
		rootNode().remove(mainMenu.getMenuNode());
		
		introMenu = new Menu(this, "Play Intro?", Arrays.asList("Yes", "No"));
		introMenu.bind(gameScene.widthProperty(), gameScene.heightProperty());
		rootNode().add(introMenu.getMenuNode());
		
	}
	
	private void loadLevel(Boolean showIntro){
		
		rootNode().remove(introMenu.getMenuNode());
		
		System.out.println("Loading level");
		
		level = new Game(this, gc, showIntro);
		level.bind(gameScene.widthProperty(), gameScene.heightProperty());
		rootNode().add(level.getLevelNode());
		
		
		
	}
	
}
