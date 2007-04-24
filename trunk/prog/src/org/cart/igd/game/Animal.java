package org.cart.igd.game;

import org.cart.igd.models.obj.OBJModel;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.cart.igd.math.Vector3f;
import org.cart.igd.entity.*;
import org.cart.igd.gl2d.*;
import org.cart.igd.util.*;
import org.cart.igd.gui.*;
import org.cart.igd.states.*;



public class Animal extends Entity{
	/*
	//			 states
	 //0 = incage not talked to
	 //1 = incage waiting for item
	 //2 = incage ready to be saved
	 //3 = incage ready to be saved after item given
	 //4 = saved by bush
	 //5 = saved in party
	 
	 
	 ids
	 0 flamingo
	 1 turtles
	 2 panda
	 3 kangaroo
	 4 giraffe
	 5 tiger
	 6 penguin
	 7 meerkat
	 8 woodpecker 
	 9 elephant
	 */
	public String name;
	public int id;
	public int state = 0;
	private boolean Collide = false;
	private InGameState igs;
	
	public Animal(String name,int id,float fd, float bsr, OBJModel model, Vector3f location,InGameState igs){
		super(location,fd,bsr, model);
		this.name = name;
		this.id = id;
		this.igs = igs;
	}	
		
	public void update(Vector3f playerPosition){
		System.out.println("state = " + state);
		if(state < 4){
			float xDiff = Math.abs(playerPosition.x - this.position.x);
			float zDiff = Math.abs(playerPosition.z - this.position.z);
			if(xDiff < boundingSphereRadius && zDiff<boundingSphereRadius){
				if(!Collide&&igs.currentGuiState!=1){
					//if(igs.engageTalk){
						((Dialogue)igs.gui.get(1)).createDialogue(this);
						igs.changeGuiState(1);
					//}
				}
				Collide = true;
			} else{
				Collide = false;
			}
		}
	}
	
	public void display(GL gl){
		System.out.println("state HERE " + state);
		if(state<5)render(gl);
	}
}