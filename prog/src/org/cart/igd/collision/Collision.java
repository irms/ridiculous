package org.cart.igd.collision;

import org.cart.igd.math.*;

public class Collision
{
	// if result < 0 then collision
	public static float SphereToSphere(Vector3f A, float rA, Vector3f B, float rB)
	{
		return ( A.subtract(B).length() - (rA+rB) );
	}
	
	public static Vector3f StS(Vector3f A, float rA, Vector3f B, float rB)
	{
		float p = SphereToSphere(A, rA, B, rB);
		Vector3f v = A.subtract(B);
		//Vector3f N = v.divide(v.length());
		
		return v;
	}
}