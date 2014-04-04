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

	public Facing() {
		this(DEFAULT_FACING);
	}

	public Facing(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}
}
