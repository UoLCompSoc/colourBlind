package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.sgtcodfish.colourBlind.CBColour;

/**
 * Defines that this object can be "illuminated" by a light, which in the game
 * implies it displays as black/blank but has a "true" colour hidden.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Illuminable extends Component {
	public CBColour	colour	= null;
}
