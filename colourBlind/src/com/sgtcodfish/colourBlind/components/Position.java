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

	/**
	 * Calculates a given position vector's tile coordinates, from (0,0) to the
	 * size of the current level.
	 * 
	 * @param pos
	 *        The vector to be translated into tile coordinates.
	 * @return A vector containing tile coordinates.
	 */
	public static Vector2 inTileCoords(Vector2 pos) {
		throw new GdxRuntimeException("Error: ECS Position.inTileCoords NYI.");
	}
}
