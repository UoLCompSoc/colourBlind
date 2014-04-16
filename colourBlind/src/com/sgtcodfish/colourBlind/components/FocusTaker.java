package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.artemis.Entity;

/**
 * Indicates that an entity takes the focus of the camera when drawn; that is,
 * the camera's x and y coordinates are set to the FocusTaker's coordinates.
 * 
 * Useful for making the camera follow a given player.
 * 
 * Only 1 {@link Entity} at a time can be a FocusTaker (it doesn't make sense to
 * have more than one). Having multiple FocusTakers leads to undefined
 * behaviour.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class FocusTaker extends Component {
	public FocusTaker() {
	}
}
