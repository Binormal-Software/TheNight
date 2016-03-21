package xyz.binormal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Map {

	public JFXTileMap tileMap;
	
	private int[] clock;
	private ArrayList<JFXItem> itemList;
	
	public Map(String filePath) throws SAXException, IOException, ParserConfigurationException{ // constructor for map
		
		tileMap = new JFXTileMap(filePath);
		itemList = tileMap.getItems();
		clock = new int[]{11,4};
		
	}
	
	public void setBounds(double screenWidth, double screenHeight){
		tileMap.setBounds(new double[]{200,200}, screenWidth, screenHeight, 0, 0);
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
	
	public Boolean itemUsable(JFXItem item){
		
		if(item.getName().equals("cellphone")){
			return true;
		}
		
		return false;
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
	
	
}
