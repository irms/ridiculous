package org.cart.igd;

import org.cart.igd.util.Texture;
import org.cart.igd.ui.UIButton;
import org.cart.igd.ui.UIWindow;
import org.cart.igd.ui.UIComponent;
import org.cart.igd.input.*;
import org.cart.igd.states.InGameState;
import org.cart.igd.input.GUIEvent;
import org.cart.igd.core.Kernel;

import java.awt.event.*;

public class InGameGUI extends GUI
{
	private Texture texBush;
	private Texture texQuestLog;
	private Texture texItemIco[] = new Texture[8];
	private Texture texAnimalIco[] = new Texture[9];
	
	
	private UIWindow hudBottom;//quest log and item buttons
	private UIWindow hudLeft; //bush button with animals
	private UIWindow hudGroup;
	
	private UIButton btBush;
	private UIButton btQuestLog;
	
	private GameAction useItem[] = new GameAction[8];
	private UIButton btItems[] = new UIButton[8];
	
	private GameAction selectBushAnimal[] = new GameAction[9];
	private UIButton btBushAnimals[] = new UIButton[9];
	
	private GameAction addGroupAnimal[] = new GameAction[4];
	private GameAction activateGroupAnimal[] = new GameAction[4];
	private UIButton btGroupAnimals[] = new UIButton[4];
	
	private GameAction mouseSelect;
	private GameAction mouseReleased;
	private GameAction pressQuestLog;

	private InGameState inGameState;

    public InGameGUI(InGameState igs)
    {
    	inGameState = igs;
    	loadGameActions();
    	loadImages();
    	loadGUI();
    }
    
    public void render(GLGraphics g)
    {    
    	//move this outside to game state input check and render separate process
    	handleInput();
    		
    	g.glgBegin();
    	
    	//g.drawImageHue(texUIButton, 0, 0, new float[] { 1f, 0f, 0f });
    	//g.drawBitmapString("Button", 3, 3);
    	//g.drawImage(texAnimalButton, 200,200);
    	hudLeft.updateAndDraw();
    	hudBottom.updateAndDraw();
    	
    	hudGroup.x = (Kernel.display.getScreenWidth() - 200);
    	hudGroup.updateAndDraw();
    	
    	
    	g.glgEnd();
    }
    
    public void handleInput()
    {
    	//check bottom hud input
    	if(mouseSelect.isActive())
    	{
    		//check for bottom hud buttons
    		for(int i = 0;i<hudBottom.components.size();i++ ){
    			if(Kernel.userInput.isSquareButtonPressed(
    				hudBottom.components.get(i).rel_x,
    				hudBottom.components.get(i).rel_y,
    				64,64,
    				Kernel.userInput.mousePress[0],
    				Kernel.userInput.mousePress[1]) )
    			{
    				hudBottom.components.get(i).activate();//triger GameAction with the button
    			}
    		}
    		//check for left bud buttons
    		for(int i = 0;i<hudLeft.components.size();i++ ){
    			if(Kernel.userInput.isRoundButtonPressed(
    				hudLeft.components.get(i).rel_x+32,
    				hudLeft.components.get(i).rel_y+32,
    				32,
    				Kernel.userInput.mousePos[0],
    				Kernel.userInput.mousePos[1]) )
    			{
    				hudLeft.components.get(i).activate();//triger GameAction with the button
    					//check for release over the group buttons
    				int mdx = Kernel.userInput.mouseDragged[0];
    				int mdy = Kernel.userInput.mouseDragged[1]; 
    					
    				for(int iG = 0 ; iG<hudGroup.components.size();iG++ ){
    					if(Kernel.userInput.isRoundButtonPressed(
    						hudGroup.x+hudGroup.components.get(iG).rel_x+32,
    						hudGroup.y+hudGroup.components.get(iG).rel_y+32,
    						32,
    						mdx,
    						mdy) )
    					{
    						addGroupAnimal[iG].activate();
    						//hudGroup.components.get(iG).activate();
    						System.out.println(	Kernel.userInput.mouseDragged[0] +" / " +Kernel.userInput.mouseDragged[1]	);
    					}
    				}
    			}
    		}
    				
    		
    	
    	}
    	
    	if(addGroupAnimal[1].isActive()){
    		System.out.println("added animal to group slot 1");
    	}
    	
    	
    	if(mouseReleased.isActive()){
    		System.out.println("release");
    	}
    }// end handleInput()
    
    /** load game actions before adding them to UIButtons*/
    public void loadGameActions()
    {
    	//GameAction( String details, boolean continuous )
    	pressQuestLog = new GameAction("open the quest log",false);
    	mouseSelect = new GameAction("mouse press",false);
    	mouseReleased = new GameAction("mouse release", false,GameAction.ON_RELEASE_ONLY);
    	
    	for(int iEvt = 0; iEvt<useItem.length; iEvt++){
    		useItem[iEvt]= new GameAction("use item: "+(iEvt+1) ,false);
    		Kernel.userInput.bindToButton(useItem[iEvt], 31+iEvt );
    	}
    	
    	//select animal on press 
    	for(int iEvt = 0; iEvt<selectBushAnimal.length; iEvt++){
    		selectBushAnimal[iEvt]= new GameAction("select animal: "+(iEvt+1),false ,GameAction.ON_PRESS_ONLY);
    		Kernel.userInput.bindToButton(selectBushAnimal[iEvt], 11+iEvt );
    	}
    	
    	addGroupAnimal[0] = new GameAction("activate leader", false,GameAction.ON_RELEASE_ONLY);
    	addGroupAnimal[1] = new GameAction("activate animal: 1",false,GameAction.ON_RELEASE_ONLY);
    	addGroupAnimal[2] = new GameAction("activate animal: 2",false,GameAction.ON_RELEASE_ONLY);
    	addGroupAnimal[3] = new GameAction("activate animal: 3",false,GameAction.ON_RELEASE_ONLY);
    	
    	//activating animal in the group
    	Kernel.userInput.bindToButton(activateGroupAnimal[0],GUIEvent.BT_GROUP_0);
    	Kernel.userInput.bindToButton(activateGroupAnimal[1],GUIEvent.BT_GROUP_1);
    	Kernel.userInput.bindToButton(activateGroupAnimal[2],GUIEvent.BT_GROUP_2);
    	Kernel.userInput.bindToButton(activateGroupAnimal[3],GUIEvent.BT_GROUP_3);
    	
    	
    	
    	Kernel.userInput.bindToButton(pressQuestLog, GUIEvent.BT_QUEST_LOG);
    	
    	
    	Kernel.userInput.bindToButton(pressQuestLog, GUIEvent.BT_QUEST_LOG);
    	Kernel.userInput.bindToKey(pressQuestLog, KeyEvent.VK_L);
    	Kernel.userInput.bindToKey(pressQuestLog, KeyEvent.VK_TAB);
    	Kernel.userInput.bindToMouse(mouseSelect, MouseEvent.BUTTON1 );
    	//Kernel.userInput.bindToMouse(mouseReleased, MouseEvent.BUTTON1 );
    }// end loadGameActions()
    
    /** load texture for the gui components */
    public void loadImages()
    {
		texBush = Display.renderer.loadImage("data/images/buttons/bush_ico_big.png");
    	texQuestLog = Display.renderer.loadImage("data/images/buttons/questlog_ico.png");
    	
    	for(int iItm = 0; iItm<texItemIco.length; iItm++)
    	{
    		texItemIco[iItm] = Display.renderer.loadImage("data/images/buttons/item_ico_0"+(iItm+1)+".png");
    	}
    	
    	for(int iAnm = 0; iAnm<texAnimalIco.length; iAnm++)
    	{
    		texAnimalIco[iAnm] = Display.renderer.loadImage("data/images/buttons/animal_ico_0"+(iAnm+1)+".png");
    	}
    	
    }
    
    
    /** load the gui components after Textures and GameActions are loaded*/
    public void loadGUI()
    {
    	/**** init gui elements ****/
    	// invisible pane that holds the elements
    	hudBottom = new UIWindow("",0,0,false);
    	hudLeft = new UIWindow("",0,0,false);
    	hudGroup = new UIWindow("",0,0,false);
    	
    	// add buttons
    	btBush = new UIButton(texBush, pressQuestLog, 0,0,128,128);
    	btQuestLog = new UIButton(texQuestLog, pressQuestLog,128,0,64,64);
    	
    	for(int iItm = 0;iItm<btItems.length; iItm++){
    		btItems[iItm]= new UIButton( texItemIco[iItm], useItem[iItm], 192+(64*iItm), 0, 64, 64 );
    	}
    	
    	for(int iAnm = 0;iAnm<btBushAnimals.length; iAnm++){
    		btBushAnimals[iAnm]= new UIButton( texAnimalIco[iAnm], selectBushAnimal[iAnm], 0, 128+(64*iAnm), 64, 64 );
    	}
    	
    	
    	btGroupAnimals[0] = new UIButton( texItemIco[0], activateGroupAnimal[0],32,0,128,128);
    	btGroupAnimals[1] = new UIButton( texItemIco[0], activateGroupAnimal[1],-32,120,64,64);
    	btGroupAnimals[2] = new UIButton( texItemIco[0], activateGroupAnimal[2],32,130,64,64);
    	btGroupAnimals[3] = new UIButton( texItemIco[0], activateGroupAnimal[3],96,120,64,64);
    	
    	// add gui elements 
    	
    	hudBottom.add(btQuestLog);
    	
    	for(UIButton b: btItems){
    		hudBottom.add(b);
    	}
    	
    	hudLeft.add(btBush);
    	
    	for(UIButton b: btBushAnimals){
    		hudLeft.add(b);
    	}
    	
    	for(UIButton b: btGroupAnimals){
    		hudGroup.add(b);
    	}
    }// end load gui
}