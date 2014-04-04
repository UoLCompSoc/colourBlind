package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Holds an object's position in the world, and can convert between world and
 * tile coordinates.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Position extends Component {
	public Vector2	position	= null;

	public Position() {
		this(new Vector2(0.0f, 0.0f));
	}

	public Position(float x, float y) {
		this(new Vector2(x, y));
	}

	public Position(Vector2 v) {
		position = v;
	}

	/**
	 * Get the x coordinate of the position. Note that changing this value will
	 * NOT change the position.
	 * 
	 * @return The x coordinate of this position.
	 */
	public float x() {
		return position.x;
	}

	/**
	 * Get the y coordinate of the position. Note that changing this value will
	 * NOT change the position.
	 * 
	 * @return The y coordinate of this position.
	 */
	public float y() {
		return position.y;
	}

	public static Vector2 inTileCoords(Vector2 pos) {
		throw new GdxRuntimeException("Error: ECS Position.inTileCoords NYI.");
	}
}
