package xyz.binormal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {

	private ArrayList<JFXItem> playerInventory;
	private HashMap<String, Boolean> playerAttributes; // isAlive, hasWon, ringEquipped, drankAntidote, etc.
	
	public int walkingDirection;
	public int walkingSpeed = 1;
	public Boolean walking = false;
	public JFXAnimatedSprite playerSprite;
	
	
	public double deltaX;
	public double deltaY;
	
	public Player(String spritePath, double spriteDelay) throws IOException{ // player constructor
		
		playerSprite = new JFXAnimatedSprite(spritePath, spriteDelay);
		playerAttributes = new HashMap<String, Boolean>();
		playerInventory = new ArrayList<JFXItem>();
	
	}
	
	public void putAttribute(String attributeName, Boolean value){
		this.playerAttributes.put(attributeName, value);
	}
	
	public Boolean getAttribute(String attributeName){
		return this.playerAttributes.get(attributeName);
	}
	
	public void kill(){
		this.putAttribute("alive", false);
	}

	public void addToInventory(JFXItem item){
		playerInventory.add(item);
	}
	
	public void removeFromInventory(JFXItem item){ // remove from inventory array
		playerInventory.remove(item);
	}
	
	public boolean hasItem(String item){ // scan inventory array for item and return result
		return playerInventory.contains(item);
	}
	
	public ArrayList<JFXItem> getInventory(){
		return playerInventory;
	}
	
	
}
