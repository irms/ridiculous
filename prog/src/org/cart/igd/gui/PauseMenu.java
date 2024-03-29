package org.cart.igd.gui;

import java.awt.event.KeyEvent;

import org.cart.igd.core.Kernel;
import org.cart.igd.gl2d.GLGraphics;
import org.cart.igd.gl2d.UILabel;
import org.cart.igd.input.UserInput;
import org.cart.igd.states.GameState;
import org.cart.igd.states.InGameState;

public class PauseMenu extends GUI{
	
	UILabel menuSelection[] = new UILabel[3];
	int selection = 0;
	
	private UserInput input;
	
	public PauseMenu(GameState gs){
		super(gs);
		input = Kernel.userInput;
		loadImages();
	}
	
	public void render(GLGraphics g){
		g.glgBegin();
		menuSelection[0].draw(g);
		menuSelection[1].draw(g);
		menuSelection[2].draw(g);
		g.glgEnd();
		
	}
	
	public void loadImages(){
		
		
		menuSelection[0] = new UILabel("Resume", 200, 400, 200, 16);
		menuSelection[0].size[0]=4;
		menuSelection[0].size[1]=4;
		menuSelection[1] = new UILabel("Options", 200, 300, 200, 16);
		menuSelection[1].size[0]=4;
		menuSelection[1].size[1]=4;
		menuSelection[2] = new UILabel("Quit", 200, 200, 200, 16);
		menuSelection[2].size[0]=4;
		menuSelection[2].size[1]=4;
			
	}
	
	public void update(long elapsedTime){
		
	}
	
	public void handleInput(long lapsedTime){
		if(input.keys[KeyEvent.VK_ENTER])
		{
			if(selection == 0)
			{
				((InGameState)gameState).changeGuiState(0);
			}
		}
	}

}
