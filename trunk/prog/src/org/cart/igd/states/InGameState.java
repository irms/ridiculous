package org.cart.igd.states;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import java.util.Random;

import org.cart.igd.Camera;
import org.cart.igd.Renderer;
import org.cart.igd.core.Kernel;
import org.cart.igd.entity.Entity;
import org.cart.igd.gl2d.GLGraphics;
import org.cart.igd.gui.Dialogue;
import org.cart.igd.gui.*;
import org.cart.igd.input.GameAction;
import org.cart.igd.input.PickingHandler;
import org.cart.igd.input.UserInput;
import org.cart.igd.math.Vector3f;
import org.cart.igd.models.obj.OBJAnimation;
import org.cart.igd.models.obj.OBJModel;
import org.cart.igd.game.*;
import org.cart.igd.entity.*;
import org.cart.igd.sound.*;
import org.cart.igd.media.CutscenePlayer;

/**
 * InGameState.java
 *
 * General Function: Handles almost all of the game play.
 */
public class InGameState extends GameState
{	
	public String[] infoText = { "", "", "", "", "","","","" };
	
	/* Collection of all in-game entities. */
	public List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());
	
	/* Collection of items. */
	public ArrayList<Item> items = new ArrayList<Item>();
	
	/* Collection of interactive entities in-game. */
	public ArrayList<Entity> interactiveEntities = new ArrayList<Entity>();
	
	/* Collection of unnecessary explorable objects. */
	public ArrayList<UnnecessaryExplore> unnecessaryExplores = new ArrayList<UnnecessaryExplore>();	

	/* Collection of entities to remove from the all-entity collection. */
	public ArrayList<Entity> removeList = new ArrayList<Entity>();
	
	/* The player entity. */
	public Entity player;
	
	/* The in-game camera. */
	public Camera camera;
	
	/* Previous camera zoom. */
	public int previousCameraZoom;
	
	/* Party Snapper OBJ Model Data. */
	private OBJModel partySnapper;
	
	/* Bush OBJ Model Data. */
	private OBJModel bushModel;
	
	/* Toothpaste OBJ Model Data. */
	private OBJModel zoopaste;
	
	/* ExplorationBox OBJ Model Data. */
	private OBJModel explorationBox;
	
	/* WaterAffinity OBJ Model Data. */
	private OBJModel waterAffinity;
	
	/* FoodAffinity OBJ Model Data. */
	private OBJModel foodAffinity;
	
	/* Constant gravity variable. */
	private final float GRAVITY = 0.025f;
	
	/* Player's state. */
	private int playerState = 0;
	
	/* Game GUI index. */
	public static final int GUI_GAME = 1;
	
	/* Dialogue GUI index. */
	public static final int GUI_DIALOGUE = 0;
	
	/* Collection of GUI states */
	public ArrayList<GUI> gui = new ArrayList<GUI>();
	
	/* The GameAction that handles mouse wheel scrolling. */
	private GameAction mouseWheelScroll;
	
	/* Bush entity. */
	public Entity bush;
	
	/* Flag that says if the player is near the bush. */
	public boolean nearBush = false;
	
	/* Instance of Inventory object. */
	public Inventory inventory;
	
	/* Instance of QuestLog object. */
	public QuestLog questlog;
	
	/* Instance of CutscenePlayer object. */
	public CutscenePlayer cutscenePlayer;
	
	/* Instance of Terrain object. */
	private Terrain terrain;
	
	private boolean loaded = false;
	private boolean showInfoText = true;
	
	/* Instance of GLGraphics object. */
	private GLGraphics glg;

	public Sound backgroundMusic = null,throwPopper = null,popPopper = null,
		giveItem = null,openQuestLog = null, closeQuestLog = null,
		questLogMusic = null,freeAnimalTune = null;
	
	public Sound turnPage[] = new Sound[4];
	
	public GuardSquad guardSquad;
	public OBJAnimation flamingoWalk;
	public OBJAnimation flamingoIdle;
	public OBJAnimation turtleIdle;
	public OBJAnimation kangarooIdle;
	public OBJAnimation giraffeIdle;//NOT exported yet
	public OBJAnimation pandaIdle;
	public OBJAnimation tigerIdle;//NOT exported yet
	public OBJAnimation penguinIdle;
	public OBJAnimation meerkatIdle;
	public OBJAnimation woodpeckerIdle;
	public OBJAnimation elephantIdle;
	
	/** create a sound manager with current sound settings */
	private SoundManager sm = new SoundManager(Kernel.soundSettings);
	
	public InGameState(GL gl)
	{
		super(gl);
		
		/* load common sounds */
		try{
			backgroundMusic = new Sound("data/sounds/music/zoo_music.ogg");
			questLogMusic = new Sound("data/sounds/music/questlog_music.ogg");
			throwPopper = new Sound("data/sounds/effects/throw_popper.ogg");
			popPopper = new Sound("data/sounds/effects/pop_popper.ogg");
			giveItem = new Sound("data/sounds/effects/give_item.ogg");
			openQuestLog = new Sound("data/sounds/effects/open_quest_log.ogg");
			closeQuestLog = new Sound("data/sounds/effects/close_quest_log.ogg");
			freeAnimalTune = new Sound("data/sounds/music/free_animal_tune.ogg");
			for(int i = 0;i<turnPage.length;i++){
				turnPage[i] = new Sound("data/sounds/effects/turn_page-" + i + ".ogg");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		bushModel	= new OBJModel(gl,"bush");
		partySnapper = new OBJModel(gl,"party_snapper");
		OBJModel partySnapper = new OBJModel(gl,"party_snapper");
		
		explorationBox = new OBJModel(gl,"exploration_box",5f,false);
		waterAffinity = new OBJModel(gl,"water_affinity",5f,false);
		foodAffinity = new OBJModel(gl,"food_Affinity",5f,false);
		
		
		/* init container/gamelocic classes*/
		terrain = new Terrain();
		terrain.load(gl);

		questlog = new QuestLog("Quest Log",20,10);
		questlog.load();

		inventory = new Inventory(this);
		cutscenePlayer = new CutscenePlayer();
		cutscenePlayer.loadMovie("data/movies/test.avi");
		
		/* create objAnimations */
		flamingoWalk = new OBJAnimation(gl,1,"flamingo_walking_",105);
		flamingoIdle = new OBJAnimation(gl,1,"flamingo_idle_",500);
		turtleIdle = new OBJAnimation(gl,1,"turtle_idle_",300);
		kangarooIdle = new OBJAnimation(gl,1,"kangaroo_idle_",300);
		giraffeIdle = new OBJAnimation(gl,1,"giraffe_idle_",1000);
		tigerIdle = new OBJAnimation(gl,10,"tiger_idle_",1000);
		penguinIdle = new OBJAnimation(gl,1,"penguin_idle_",300);
		pandaIdle = new OBJAnimation(gl,1,"panda_idle_",200);
		meerkatIdle = new OBJAnimation(gl,1,"meerkat_idle_",300);
		woodpeckerIdle = new OBJAnimation(gl,1,"woodpecker_idle_",300);
		elephantIdle = new OBJAnimation(gl,1,"elephant_idle_",100);
		
		player = new Player(new Vector3f(-20f,0f,-20f), 0f, .2f, 
				flamingoWalk,flamingoIdle);
		camera = new Camera(player, 10f, 4f);
		
		/* special entity where animals are hidden after rescue place rescued 
		 * animals in a position relative to this */
		bush = new Bush( new Vector3f(0,0,0), 20f, bushModel,this);	
		
		/* guards as a whole unit */
		guardSquad = new GuardSquad(this);

		
		

		/* add collectable object to the map */
		
		
		/*
		 public UnnecessaryExplore(Vector3f pos, float fD, float bsr, OBJModel model,InGameState igs,boolean display){
		super(pos, fD, bsr, model);
		this.display = display;
	}
		 */
		
		unnecessaryExplores.add(new UnnecessaryExplore("Nothing",new Vector3f(50f,0f,50f),0f,10f,explorationBox,this,true));
		unnecessaryExplores.add(new UnnecessaryExplore("Water",new Vector3f(-50f,0f,-50f),0f,10f,waterAffinity,this,true));
		unnecessaryExplores.add(new UnnecessaryExplore("Food",new Vector3f(50f,0f,-50f),0f,10f,foodAffinity,this,true));	
		unnecessaryExplores.add(new UnnecessaryExplore("Left",new Vector3f(0f,0f,-21f),0f,20f,foodAffinity,this,false));
		unnecessaryExplores.add(new UnnecessaryExplore("Right",new Vector3f(0f,0f,21f),0f,20f,foodAffinity,this,false));
		unnecessaryExplores.add(new UnnecessaryExplore("Up",new Vector3f(21f,0f,0f),0f,20f,foodAffinity,this,false));
		unnecessaryExplores.add(new UnnecessaryExplore("Down",new Vector3f(-21f,0f,0f),0f,20f,foodAffinity,this,false));
		
		
		items.add(new Item("Fish",Inventory.FISH,1,0f,1f,
				partySnapper,
				new Vector3f(-15f,0f,0f),true,true));
				
		items.add(new Item("Hotdog",Inventory.HOTDOG,1,0f,1f,
				partySnapper,
				new Vector3f(-15f,0f,10f),true,true));
				
		items.add(new Item("Disguise Glasses",Inventory.DISGUISEGLASSES,1,0f,1f,
				partySnapper,
				new Vector3f(-15f,0f,20f),true,true));
				
		items.add(new Item("Medication",Inventory.MEDICATION,1,0f,1f,
				partySnapper,
				new Vector3f(-15f,0f,30f),true,true));
				
		items.add(new Item("Paddle Ball",Inventory.PADDLEBALL,1,0f,1f,
				partySnapper,
				new Vector3f(-15f,0f,40f),false,false));
				
		items.add(new Item("Zoo Paste",Inventory.ZOOPASTE,1,0f,1f,
				partySnapper,
				new Vector3f(-15f,0f,50f),false,false));
				
		items.add(new Item("Party Snapper",Inventory.POPPERS,50,0f,1f,
				partySnapper,
				new Vector3f(-15f,0f,60f),true,true));
				
		items.add(new Item("Party Snapper Hidden",Inventory.POPPERS,50,0f,1f,
				partySnapper,
				new Vector3f(-15f,0f,80f),true,true));

		/* add animals to the map */
		interactiveEntities.add(new Animal("Turtles",Inventory.TURTLES,0f,3f,
				turtleIdle, 
				new Vector3f(10f,0f,-20f),this,0,new Vector3f(10f,0f,10f)));
				
		interactiveEntities.add(new Animal("Panda",Inventory.PANDA,0f,3f,
				pandaIdle, 
				new Vector3f(10f,0f,-30f),this,0,new Vector3f(10f,0f,5f)));
				
		interactiveEntities.add(new Animal("Kangaroo",Inventory.KANGAROO,0f,3f,
				kangarooIdle, 
				new Vector3f(10f,0f,-40f),this,Inventory.DISGUISEGLASSES,new Vector3f(5f,0f,10f)));
		
		interactiveEntities.add(new Animal("Giraffe",Inventory.GIRAFFE,0f,5f,
				giraffeIdle, 
				new Vector3f(10f,0f,-50f),this,Inventory.MEDICATION,new Vector3f(10f,0f,0f)));
				
		interactiveEntities.add(new Animal("Tiger",Inventory.TIGER,0f,5f,
				tigerIdle, 
				new Vector3f(10f,0f,-60f),this,Inventory.ZOOPASTE,new Vector3f(0f,0f,10f)));
		
		interactiveEntities.add(new Animal("Penguin",Inventory.PENGUIN,0f,5f,
				penguinIdle, 
				new Vector3f(10f,0f,-70f),this,Inventory.FISH,new Vector3f(-10f,0f,0f)));
				
		interactiveEntities.add(new Animal("Meerkat",Inventory.MEERKAT,0f,3f,
				meerkatIdle, 
				new Vector3f(10f,0f,-80f),this,Inventory.HOTDOG,new Vector3f(0f,0f,-10f)));
				
		interactiveEntities.add(new Animal("WoodPecker",Inventory.WOODPECKER,0f,3f,
				woodpeckerIdle, 
				new Vector3f(10f,0f,-90f),this,Inventory.PADDLEBALL,new Vector3f(-10f,0f,-10f)));
				
		interactiveEntities.add(new Animal("Elephant",Inventory.ELEPHANT,0f,3f,
				elephantIdle, 
				new Vector3f(10f,0f,-100f),this,0,new Vector3f(-10f,0f,-5f)));

		
		/* add interactive terrain items*/
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-20f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.TURTLES,Inventory.FLAMINGO,this));
				
				
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(30f,0f,-20f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.TURTLES,Inventory.FLAMINGO,this,true));
				
				
						
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-30f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.PANDA,Inventory.TURTLES,this));
				
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-40f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.KANGAROO,Inventory.TURTLES,this));
				
				
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-50f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.GIRAFFE,Inventory.TURTLES,this));
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-60f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.TIGER,new int[]{Inventory.GIRAFFE,Inventory.PANDA},this));
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-70f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.PENGUIN,Inventory.TIGER,this));
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-80f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.MEERKAT,new int[]{Inventory.GIRAFFE,Inventory.KANGAROO},this));
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-90f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.WOODPECKER,Inventory.MEERKAT,this));
		interactiveEntities.add(new TerrainEntity(
				new Vector3f(20f,0f,-100f), 0f, 3f,
				new OBJModel(gl,"save_animal_thing", 2f,false),Inventory.ELEPHANT,new int[]{Inventory.WOODPECKER,Inventory.PENGUIN},this));
		
		mouseWheelScroll = new GameAction("zoom out", false);
		
		Kernel.userInput.bindToMouse(mouseWheelScroll,UserInput.MOUSE_WHEEL_DOWN);
	}
	
	public void init(GL gl, GLU glu)
	{
		glg = Kernel.display.getRenderer().getGLG();
		
		/* add different gui segments */
		gui.add(new InGameGUI(this,gl,glu));
		gui.add(new Dialogue(this));
		gui.add(new PauseMenu(this));
		
		if(backgroundMusic != null){
			backgroundMusic.loop(1f,.5f);//TODO: enable when sound fixed
		} else {
			System.out.println("backgroundMusic loop is null");
		}
	
		
		guardSquad.init( gl, glu );
		
		loaded = true;
	}
	
	public void rotateCamera(float amt)
	{
		camera.facingOffset += amt;
	}
	
	/** 
	 * moved some of the character movement input due to a faster update 
	 * which made character jitter forward when walking 
	 **/
	public void updateItems(long elapsedTime){
		for(int i = 0;i<items.size();i++){
			Item item = items.get(i);
			item.update(player.position,this, elapsedTime);
		}
	}
	
	public void updateInteractiveEntities(long elapsedTime){
		for( Entity e : interactiveEntities ){
			if(e instanceof Animal){
				((Animal)e).update(player.position);
				((Animal)e).update(elapsedTime);
			} else {
				e.update(elapsedTime);
			}
		}
	}
	
	public void updateUnnecessaryExplores(long elapsedTime){
		for(int i = 0;i<unnecessaryExplores.size();i++){
			UnnecessaryExplore unnecessaryExplore = unnecessaryExplores.get(i);
			unnecessaryExplore.update(player.position,elapsedTime);
		}
	}
	
	
	public void updateQuestLog(long elapsedTime){
		questlog.update(this,elapsedTime);
	}
	 
	public Animal getAnimal(String name){
		for( Entity e : interactiveEntities ){
			if(e instanceof Animal){
				Animal a = (Animal)e;
				if(a.name.equals(name)){
					return a;
				}
			}
		}
		return null;
	}
	
	public void removePartyAnimals(){
		for( Entity e : interactiveEntities ){
			if(e instanceof Animal){
				Animal a = (Animal)e;
				if(a.state==Inventory.SAVED_IN_PARTY){
					a.state=Inventory.SAVED_IN_BUSH;
				}
			}
		}
		inventory.animals.clear();
		inventory.currentCursor=Inventory.FLAMINGO;	
	}
	 
	public void update(long elapsedTime)
	{
		player.update(elapsedTime);

		
		/* reset guads be removing Noise entities TODO: make sure to call this once */
		if(Kernel.userInput.keys[KeyEvent.VK_R]) guardSquad.reset();
		guardSquad.reset = true;
		
		inventory.update();
		gui.get(currentGuiState).update(elapsedTime);

		updateItems(elapsedTime);
		updateInteractiveEntities(elapsedTime);
		updateQuestLog(elapsedTime);
		updateUnnecessaryExplores(elapsedTime);
		
		((Bush)bush).update(elapsedTime);
		
		player.lastPosition.x = player.position.x;
		player.lastPosition.y = player.position.y;
		player.lastPosition.z = player.position.z;
		if(playerState==1)
		{
			if(player.position.y<5f)
				player.position.y+=0.5f;
			else if(player.position.y>=5f)
				playerState=2;
		}
		else if(playerState==2)
		{
			if(player.position.y>0f)
				player.position.y -= (float)elapsedTime * GRAVITY;
			else if(player.position.y<0f)
				player.position.y = 0f;
			if(player.position.y==0f)
				playerState = 0;
		}
		
		entities.removeAll(removeList);
		removeList.clear();
		
	}
	
	/** select differen gui subsets */
	public void changeGuiState( int guiState){
		if(gui.size()>= guiState){
			currentGuiState =guiState;
		} else {
			System.out.println(
				"InGameState.changeGuiState(int guiState) ->no such guiState"
					+ gui.size());
		}
	}
	
	public synchronized void display(GL gl, GLU glu)
	{
		if(!cutscenePlayer.isStopped)
		{
			cutscenePlayer.render(glg);
			return;
		}
		
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		gl.glLoadIdentity();
		
		/* Setup Camera */
		camera.lookAt(glu, player);
		
		/* render the bush */
		bush.render(gl);
		

		//objAnimation.render(gl);
		

		/* Render Player Model */
		if(currentGuiState!=4){
			player.render(gl);
		}
		
		
		/* render all entities: partySnappers, guards*/
		synchronized ( entities ){
			for( Entity e: entities )
			{
				e.render(gl);
			}	
		}
		Renderer.info[1]="# entities: "+entities.size();
		
		
		/* render all animals */
		for( Entity e : interactiveEntities ){
			int animCount = 0;
			int terrCount = 0;
			if( e instanceof Animal){
				animCount ++;
				((Animal)e).display(gl);
			}
			if(e instanceof TerrainEntity){
				e.render(gl);
				terrCount++;
			}
			
			Renderer.info[2]="# animals: "+animCount;
			Renderer.info[3]="# terrObj: "+terrCount;
		}
		
		
		/* render all items in 3d space */
		for(int i = 0;i<items.size();i++){
			Item item = items.get(i);
			item.display3d(gl);
		}
		
		for(int i = 0;i<unnecessaryExplores.size();i++){
			UnnecessaryExplore unnecessaryExplore = unnecessaryExplores.get(i);
			unnecessaryExplore.display(gl);
		}
		
		
		
		Renderer.info[4]="# items: "+items.size();
		
		/* render the world map and sky*/
		terrain.render( gl, player);
		
		/* Render GUI */
		gui.get(currentGuiState).render( Kernel.display.getRenderer().getGLG() );

	}
	
	public synchronized void throwPartyPopper(){
		for(int i = 0;i<inventory.items.size();i++){
			Item item = inventory.items.get(i);
			if(item.itemId ==8 && item.amount>0){
				if(throwPopper != null){
					throwPopper.play((new Random()).nextFloat() 
						+ .5f,(new Random()).nextFloat() + .5f);
				}
				
				entities.add(new PartySnapper(
					new Vector3f(player.position.x, player.position.y, player.position.z),
					player.facingDirection, 
					0f,
					partySnapper,this)
				);
				item.amount--;
			}
		}
	}
	
	public void handleInput(long elapsedTime)
	{
		gui.get(currentGuiState).handleInput(elapsedTime);
		previousCameraZoom = (int)camera.distance;
		camera.zoom((float)Kernel.userInput.getMouseWheelMovement()*2f);
		
		if(currentGuiState==0){
			if(mouseWheelScroll.isActive())
			{
				camera.zoom(mouseWheelScroll.getAmount());
			}
			
		
			/* Check for Escape key to end program */
			if(Kernel.userInput.keys[KeyEvent.VK_ESCAPE]) Kernel.display.stop();
			
			if(Kernel.userInput.keys[KeyEvent.VK_0])
			{
				Kernel.userInput.keys[KeyEvent.VK_0] = false;
				cutscenePlayer.playMovie();
			}
			
			/* PAGEUP/PAGEDOWN - Inc./Dec. how far above the ground the camera is. */
			if(Kernel.userInput.keys[KeyEvent.VK_PAGE_UP])
				camera.changeVerticalAngleDeg( 1);
			else if(Kernel.userInput.keys[KeyEvent.VK_PAGE_DOWN])
				camera.changeVerticalAngleDeg(-1);
			
			/* HOME/END - Inc./Dec. distance from camera to player. */
			if(Kernel.userInput.keys[KeyEvent.VK_HOME])
				camera.zoom(-1);
			else if(Kernel.userInput.keys[KeyEvent.VK_END])
				camera.zoom( 1);
				
			if(Kernel.userInput.keys[KeyEvent.VK_SPACE])
			{
				if(playerState==0)
					playerState=1;
			}
			
			if(Kernel.userInput.keys[KeyEvent.VK_CONTROL]){
				throwPartyPopper();
				Kernel.userInput.keys[KeyEvent.VK_CONTROL] = false;
			}
			
			if(Kernel.userInput.keys[KeyEvent.VK_M]){
				//((MoviePlayer)gui.get(3)).playMovie(0);
				this.changeGameState("MiniGame");
			}	
		}
		

	}
}
