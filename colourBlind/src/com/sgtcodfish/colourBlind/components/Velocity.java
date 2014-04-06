package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Holds a movable Entity's velocity.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Velocity extends Component {
	public Vector2	velocity	= null;

	public Velocity() {
		this(new Vector2(0.0f, 0.0f));
	}

	public Velocity(float x, float y) {
		this(new Vector2(x, y));
	}

	public Velocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	/**
	 * Get the x component of the velocity. Note that changing this value will
	 * NOT change the velocity; change this.velocity.x.
	 * 
	 * @return The x component of this velocity.
	 */
	public float getX() {
		return velocity.x;
	}

	/**
	 * Get the y component of the velocity. Note that changing this value will
	 * NOT change the velocity; change this.velocity.y.
	 * 
	 * @return The y component of this velocity.
	 */
	public float getY() {
		return velocity.y;
	}
}
