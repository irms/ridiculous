package org.cart.igd.entity;

import java.io.File;

import java.util.Random;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.cart.igd.util.ColorRGBA;
import org.cart.igd.math.Vector3f;
import org.cart.igd.core.Kernel;
import org.cart.igd.entity.*;
import org.cart.igd.models.obj.OBJModel;
import org.cart.igd.states.*;


/**
 * Object used by player to distract guards by creating a Noise object upon
 * intersection with the ground 
 */
public class PartySnapper extends Entity{
	float gravitypull = -.2f;
	InGameState igs;
	
	public PartySnapper (Vector3f pos, float fD, float bsr, OBJModel model,
			InGameState igs){
		super(pos,fD,bsr, model);
		this.igs = igs;
	}
	
	/**
	 * Render completely with all transformations
	 */
	public void draw(GL gl){
		gl.glPushMatrix();
			gl.glTranslatef(position.x, position.y, position.z);
			gl.glRotatef(facingDirection, 0f, -1f, 0f);
			modelObj.draw(gl);
		gl.glPopMatrix();
	}
	
	/**
	 * update height as the objects makes an arc as it flies
	 */
	public void update(long elapsedTime){
		if(!(position.y<-.1f)){
			position.y-=gravitypull;
				position.x = position.x + ( (float)Math.cos((facingDirection) * 0.0174f) )*2;
				position.z = position.z + ( (float)Math.sin((facingDirection) * 0.0174f) )*2;
				gravitypull+=.02f;
	
			//position.x-=;
			//position.z-=;
		} else {
			igs.popPopper.play((new Random()).nextFloat() + 1f,(new Random()).nextFloat() + 1f);//TODO: enable when sound fixed
			synchronized(igs.entities){
				//TODO: radius of noise
				igs.entities.add(new Noise(position,.2f,20000L,igs));
				igs.entities.remove(this);
			}
		}
	}
}



	
	