package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;

/**
 * Indicates that an Entity has a facing direction - left or right.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Facing extends Component {
	public static final boolean	DEFAULT_FACING	= false;			// right

	public boolean				facingLeft		= DEFAULT_FACING;

	/**
	 * Creates a facing that points in the default direction, which is right by
	 * default.
	 */
	public Facing() {
		this(DEFAULT_FACING);
	}

	/**
	 * Creates a facing in the specified direction.
	 * 
	 * @param facingLeft
	 *        True if facing left, false otherwise.
	 */
	public Facing(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}
}
