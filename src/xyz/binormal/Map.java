package xyz.binormal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.javafx.geom.Rectangle;

import javafx.scene.canvas.GraphicsContext;

public class Map {

	public Player player;
	
	private JFXTileMap tileMap;
	
	private int[] clock;
	private List<JFXItem> itemList;
	private List<JFXEvent> eventList;
	private List<Rectangle> hitboxList;
	
	public Map(String filePath) throws SAXException, IOException, ParserConfigurationException{ // constructor for map
		
		
		tileMap = new JFXTileMap(filePath);
		itemList = tileMap.getItems();
		eventList = tileMap.getEvents();
		hitboxList = tileMap.getHitboxes();
		clock = new int[]{11,4};
		
	}
	
	public void setBounds(double screenWidth, double screenHeight){
		tileMap.setBounds(this.spawnLocation(), screenWidth, screenHeight, 0, 0);
	}
	
	public void movePlayer(Player player, double walkingSpeed, int walkingDirection){ // 3 = up; 2 = right, 1 = left, 0 = down
		
		switch(walkingDirection){
		
		case 0: //down
			if(tileMap.getPlayerLocation()[1] < tileMap.getDistanceToEdge()[3]){
				if(player.deltaY < 0){
					player.deltaY += walkingSpeed;
				}else{
					tileMap.scrollMap(walkingSpeed, walkingDirection);
				}
			}else{
				player.deltaY += walkingSpeed;
			}
			break; 
		
		case 1: // left
			if(tileMap.getPlayerLocation()[0] > tileMap.getDistanceToEdge()[0]){
				if(player.deltaX > 0){
					player.deltaX -= walkingSpeed;
				}else{
					tileMap.scrollMap(walkingSpeed, walkingDirection);
				}
			}else{
				player.deltaX -= walkingSpeed;
			}
			break; 
		
		case 2: // right
			
			if(tileMap.getPlayerLocation()[0] < tileMap.getDistanceToEdge()[2]){
				if(player.deltaX < 0){
					player.deltaX += walkingSpeed;
				}else{
					tileMap.scrollMap(walkingSpeed, walkingDirection);
				}
			}else{
				player.deltaX += walkingSpeed;
			}
			break; 
		
		case 3: // up
			if(tileMap.getPlayerLocation()[1] > tileMap.getDistanceToEdge()[1]){
				if(player.deltaY > 0){
					player.deltaY -= walkingSpeed;
				}else{
					tileMap.scrollMap(walkingSpeed, walkingDirection);
				}
			}else{
				player.deltaY -= walkingSpeed;
			}
			
			break; 
		
		
		
		}
		
		checkCollisions(player, walkingSpeed, walkingDirection);
		
	}
	
	public void drawMap(GraphicsContext gc, int startLayer, int endLayer){
		tileMap.drawMap(gc, startLayer, endLayer);
	}
	
	public void resolutionChange(String dimension, double delta){
		tileMap.resolutionChange(dimension, delta);
	}
	
	public Boolean nearItem(){
		
		for(JFXItem i: itemList){
			if(!i.taken && i.getBounds().contains( (int)tileMap.getPlayerLocation()[0], (int)tileMap.getPlayerLocation()[1])){
				return true;
			}
		}
		
		return false;
	}
	
	public JFXItem nearestItem(){
		
		for(JFXItem i: itemList){
			if(!i.taken && i.getBounds().contains( (int)tileMap.getPlayerLocation()[0], (int)tileMap.getPlayerLocation()[1])){
				return i;
			}
		}
		return null;
	}
	
	public JFXEvent getEventByItem(JFXItem item){
		
		for(JFXEvent e: eventList){
			if(e.getEventType().equals(e.itemEvent())){
				if(e.getBounds().contains( (int)tileMap.getPlayerLocation()[0], (int)tileMap.getPlayerLocation()[1])){
					if(e.getTakeItem().getName().equals(item.getName())){
						return e;
					}
				}
			}
		}


		return null;
		
	}

	public int[] getTime(){
		return clock;
	}
	
	public void updateTime(){
		
		clock[1] += ((new Random().nextDouble())*10)+5;
		
		if(clock[1] >= 60){
			clock[0]++;
			clock[1] -= 60;
		}
		
	}
	
	public String getFormattedTime(){ // nicely formatted clock
		
		String minutes;
		String ampm;
		
		if (clock[1] < 10)
			minutes = "0" + clock[1];
		else
			minutes = clock[1] + "";
		
		if (clock[0] > 12)
			clock[0] = 1;
		
		if (clock[0] < 10 || clock[0] == 12)
			ampm = "AM";
		else
			ampm = "PM";
		
		return ("[" + clock[0] + ":" + minutes + " " + ampm + "]");
	}
	
	public void setTime(int hours, int minutes){
		clock = new int[]{hours, minutes};
	}
	
	private void checkCollisions(Player player, double walkingSpeed, int walkingDirection){
		
		
		for(int i = 0; i < hitboxList.size(); i++){ // check for collisions
			
			int playerYOffset = 10;
			int playerHeight = 10;
			int playerWidth = 10;
			Rectangle hitBox = hitboxList.get(i);
			
			
			//prevent player from scrolling beyond map bounds
			
			if(tileMap.getPlayerLocation()[0] + player.deltaX < 0){
				player.deltaX += walkingSpeed;
			}
			if(tileMap.getPlayerLocation()[1] + player.deltaY < 0){
				player.deltaY += walkingSpeed;
			}
			if(tileMap.getPlayerLocation()[0] + player.deltaX > (tileMap.getMapSizeInPixels()[0])){
				player.deltaX -= walkingSpeed;
			}
			if(tileMap.getPlayerLocation()[1] + player.deltaY > (tileMap.getMapSizeInPixels()[1])){
				player.deltaY -= walkingSpeed;
			}
			
			
			
			
			if(tileMap.getPlayerLocation()[0] + player.deltaX + playerWidth > hitBox.x){                                     //left
				if(tileMap.getPlayerLocation()[0] + player.deltaX - playerWidth < hitBox.x + hitBox.width){                       //right
					if(tileMap.getPlayerLocation()[1] + player.deltaY + playerYOffset + playerHeight > hitBox.y){                           //top
						if(tileMap.getPlayerLocation()[1] + player.deltaY + playerYOffset - playerHeight < hitBox.y + hitBox.height){            //bottom
							
							//collision happening!
							
							switch(getDirection(walkingDirection)){
							case "DOWN":  
								if(Math.abs(player.deltaY) > 0){
									player.deltaY -= walkingSpeed;
								}else{
									tileMap.setPlayerLocation(tileMap.getPlayerLocation()[0], hitBox.y - playerYOffset - playerHeight); 
								}
								break; 
							
							case "LEFT":  
								if(Math.abs(player.deltaX) > 0){
									player.deltaX += walkingSpeed;
								}else{
									tileMap.setPlayerLocation(hitBox.x + hitBox.width + playerWidth, tileMap.getPlayerLocation()[1]); 
								}
								break; 
							
							case "RIGHT": 
								if(Math.abs(player.deltaX) > 0){
									player.deltaX -= walkingSpeed;
								}else{
									tileMap.setPlayerLocation(hitBox.x - playerWidth, tileMap.getPlayerLocation()[1]); 
								}
								break; 
							
							case "UP":  
								if(Math.abs(player.deltaY) > 0){
									player.deltaY += walkingSpeed;
								}else{
									tileMap.setPlayerLocation(tileMap.getPlayerLocation()[0], hitBox.y + hitBox.height - playerYOffset + playerHeight); 
								}
								break;
							
							}
						}
					}
				}
			}
			
			
			
			
		}
	}

	private double[] spawnLocation(){
		
		for(JFXEvent e: eventList){
			if(e.getEventType().equals(e.spawnEvent())){
				System.out.println("Spawn point found at " + e.getBounds().x + "," + e.getBounds().y);
				return new double[]{e.getBounds().x, e.getBounds().y};
			}
		}
		return new double[]{0,0};
	}
	
	public String getDirection(int walkingDirection){
		switch(walkingDirection){
		
		case 0: return "DOWN";
		case 1: return "LEFT";
		case 2: return "RIGHT";
		case 3: return "UP";
		default: return null;
		
		}
	}

}
