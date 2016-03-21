package xyz.binormal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Narrator {

	private StackPane textPane;
	private ArrayList<Text> messageText;
	private ArrayList<String> messageString;
	private int messageIndex;
	private int speed;
	private Animation animation;
	private Map map;
	private Boolean transitionDone;
	private Boolean narrationDone = true;
	private Text nextIndicator;
	
	public Narrator(){
		textPane = new StackPane();
		textPane.setStyle("-fx-background-color: #002b53aa; -fx-border-color:white; -fx-border-width: 2; -fx-border-style: solid;");
		textPane.setPadding(new Insets(10));
		textPane.setOnMouseClicked(e -> {
			this.continueNarration();
		});
		textPane.setVisible(false);
	}
	
	public void bind(ReadOnlyDoubleProperty widthProperty){
		textPane.maxWidthProperty().bind(widthProperty.divide(2));	
		textPane.setMinHeight(80);
	}
	
	public Node getTextNode(){
		return textPane;
	}
	
	public void narrate(String input, int speed, Map map){
		this.map = map;
		startNarration(input, speed);
	}
	
	public void narrate(File stringFile, int speed, Map map){
		this.map = map;
		try {
			startNarration(readFile(stringFile.getPath(), Charset.defaultCharset()), speed);
		} catch (IOException e) {
			System.err.println("Unable to read narration!");
			e.printStackTrace();
		}
	}
	
	private void startNarration(String sInput, int speed){ // scrolling text effect

		if(!narrationDone){
			return;
		}
		
		clean();
		
		this.speed = speed;
		narrationDone = false;
		textPane.setVisible(true);
		
		
		String delimitedMessage[] = sInput.split("<#>");
		messageString = new ArrayList<String>();
		
		for(String s: delimitedMessage){
			
			s = s.replaceAll("<t>", map.getFormattedTime() + "\r\n");
			//messageString.addAll(Arrays.asList(splitStringEvery(s, charsOnScreen)));
			messageString.add(s);
		}
		
		messageText = new ArrayList<Text>();
		fadeIn(textPane);
		
		continueNarration();
		 
	}
	
	public void continueNarration(){
		
		if(messageString==null)
			return;
		
		if(!transitionDone){
			animation.setRate(5);
			return;
		}

		if(messageIndex > -1){
			transitionDone = false;

			if (messageIndex < messageString.size()){ ////////////////////////////////// FADE OUT PREVIOUS MESSAGE

				setIndicatorVisible(false);
				
				if(messageIndex == messageString.size() - 1){ // end of msg
					fadeOut(textPane);
					narrationDone = true;
				}else{
					fadeOut(messageText.get(messageIndex));
				}
			}
		}else{
			transitionDone = false;
		}

		if(messageIndex++ < messageString.size()-1){ /////////////////////////////////////////////////////////  LOAD NEW MESSAGE

			messageText.add(messageTextFormat());

			String nextMessage = messageString.get(messageIndex);
			Text nextText = messageText.get(messageIndex);
			textPane.getChildren().add(nextText);

			animation = new Transition() {{
				setCycleDuration(Duration.millis((300/speed)*nextMessage.length()));
			}

			protected void interpolate(double frac) {

				final int n = Math.round(nextMessage.length() * (float) frac);
				nextText.setText(nextMessage.substring(0, n));
			}

			};

			animation.setOnFinished(new EventHandler<ActionEvent>() {// this slide is done
				@Override
				public void handle(ActionEvent event) {
					transitionDone = true;
					setIndicatorVisible(true);
				}
			});

			animation.play();

		}


	}
	
	public String[] splitStringEvery(String s, int interval) {
	    int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
	    String[] result = new String[arrayLength];

	    int j = 0;
	    int lastIndex = result.length - 1;
	    for (int i = 0; i < lastIndex; i++) {
	        result[i] = s.substring(j, j + interval);
	        j += interval;
	    } //Add the last bit
	    result[lastIndex] = s.substring(j);

	    return result;
	}
	
	private Text messageTextFormat(){
		Text text = new Text();
		text.setFont(Font.font ("Segoe UI", 16));
		text.setFill(Color.WHITE);
		text.wrappingWidthProperty().bind(textPane.widthProperty().subtract(50));
		return text;
	}
	
	private Text nextIndicator(){
		
		Text nextText = new Text("\u2936");
		nextText.setFont(Font.font (java.awt.Font.SANS_SERIF, 25));
		nextText.setFill(Color.WHITE);
		nextText.setOpacity(0.0);
		fadeCycle(nextText);
		StackPane.setAlignment(nextText, Pos.BOTTOM_RIGHT);
		
		return nextText;
	}
	
	private String readFile(String path, Charset encoding) throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	private void clean(){
		
		messageIndex = -1;
		transitionDone = true;
		textPane.getChildren().clear();
		
	}
	
	private void fadeIn(Node node){
		
		FadeTransition ft = new FadeTransition();
		ft.setDuration(new Duration(300));
		ft.setNode(node);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		
		TranslateTransition tt = new TranslateTransition();
		tt.setDuration(Duration.millis(300));
		tt.setNode(node);
		tt.setFromY(30f);
		tt.setToY(0f);
		tt.setInterpolator(Interpolator.EASE_OUT);
		
		ParallelTransition pt = new ParallelTransition(ft, tt);
		pt.play();
	}
	
	private void fadeOut(Node node){
		
		FadeTransition ft = new FadeTransition();
		ft.setDuration(new Duration(300));
		ft.setFromValue(1.0);
		ft.setToValue(0.0);
		ft.setNode(node);
		
		TranslateTransition tt = new TranslateTransition();
		tt.setDuration(Duration.millis(300));
		tt.setFromY(node.getTranslateY());
		tt.setToY(-30f);
		tt.setNode(node);
		tt.setInterpolator(Interpolator.EASE_OUT);
		
		ParallelTransition pt = new ParallelTransition(ft, tt);
		pt.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//textPane.getChildren().remove(0);
			}
		});
		pt.play();
	}
	
	private void setIndicatorVisible(boolean show){
		
		
		
		if(show){
			nextIndicator = nextIndicator();
			textPane.getChildren().add(nextIndicator);
		}else{
			textPane.getChildren().remove(nextIndicator);
		}
		
		
	}
	
	private void fadeCycle(Node node){
		
		FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
	     ft.setFromValue(0.0);
	     ft.setToValue(1.0);
	     ft.setInterpolator(Interpolator.EASE_OUT);
	     ft.setCycleCount(Animation.INDEFINITE);
	     ft.setAutoReverse(true);
	 
	     ft.play();
		
	}
}
