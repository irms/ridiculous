package org.cart.igd.models.obj;

import javax.media.opengl.GL;

import org.cart.igd.math.Vector3f;

/**
 * OBJAnimation.java
 * 
 * General Function: Animates a series of OBJ models. 
 */
public class OBJAnimation
{
	/* Frame delay. */
	private long frameDelay;
	
	/* Time left. */
	private long timeLeft;
	
	/* Current index. */
	private int modelIndex = 0;
	
	/* OBJModel array. */
	private OBJModel model[];
	
	/**
	 * Constructor
	 *
	 * General Function: Creates an instance of OBJAnimation.
	 */
	public OBJAnimation( GL gl, int numFrames, String filePath, long delay )
	{
		frameDelay = delay;
		timeLeft = delay;
		model = new OBJModel[numFrames];
		for(int i = 0; i<model.length; i++)
		{
			model[i] = new OBJModel(gl,filePath+i, 2f, true);
		}
	}
	
	/**
	 * Constructor
	 *
	 * General Function: Creates an instance of OBJAnimation.
	 */
	public OBJAnimation( GL gl, int numFrames, String filePath, long delay, float scale)
	{
		frameDelay = delay;
		timeLeft = delay;
		model = new OBJModel[numFrames];
		for(int i = 0; i<model.length; i++)
		{
			model[i] = new OBJModel(gl,filePath+i, scale, true);
		}
	}
	
	/**
	 * update
	 *
	 * General Function: Updates the frame information and current frame.
	 */
	public void update(long elapsedTime)
	{
		timeLeft -=elapsedTime;
		if(timeLeft < 0){
			if(modelIndex < model.length-1)
			{
				modelIndex ++;
			}
			else
			{
				modelIndex = 0;
			}
			
			timeLeft = frameDelay;
		}
	}
	
	/**
	 * render
	 *
	 * General Function: Renders the correct OBJModel to GL.
	 */
	public void render(GL gl, Vector3f pos, float fd)
	{
		gl.glPushMatrix();
		gl.glTranslatef(pos.x, pos.y -2f, pos.z);
		gl.glRotatef( fd , 0f, -1f, 0f);
		//gl.glScalef(scale.x,scale.y,scale.z);
		model[modelIndex].draw(gl);
		gl.glPopMatrix();
	}

}