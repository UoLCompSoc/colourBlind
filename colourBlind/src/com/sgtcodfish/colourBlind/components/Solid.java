package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.math.Rectangle;

/**
 * Indicates that the {@link Entity} is solid, i.e. it collides with other solid
 * objects.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Solid extends Component {
	public final Rectangle	rect;

	/**
	 * Constructs a new Solid component with the specified dimensions of the
	 * collision rectangle which is used for detection. Coordinates should be
	 * relative to the Entity's position entity.
	 * 
	 * @param x
	 *        The x coordinate of the rectangle, relative to the Entity's
	 *        position.
	 * @param y
	 *        The y coordinate of the rectangle, relative to the Entity's
	 *        position.
	 * @param width
	 *        The width of the rectangle.
	 * @param height
	 *        The height of the rectangle.
	 */
	public Solid(float x, float y, float width, float height) {
		rect = new Rectangle(x, y, width, height);
	}
}
