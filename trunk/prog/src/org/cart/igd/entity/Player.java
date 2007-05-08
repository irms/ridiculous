package org.cart.igd.entity;

import org.cart.igd.math.Vector3f;
import org.cart.igd.models.obj.OBJAnimation;
import org.cart.igd.models.obj.OBJModel;

public class Player extends Entity
{
	float walkNoiseRadius;
	float runNoiseRadius;
	float viewDistance;
	float hearDistance;//show pings of movement on "radar"
	
	public Player(Vector3f pos, float fD, float bsr,OBJAnimation model){
		super(pos,fD,bsr,model);
	}
}
