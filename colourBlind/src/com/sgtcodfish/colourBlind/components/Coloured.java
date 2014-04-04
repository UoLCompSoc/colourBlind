package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.sgtcodfish.colourBlind.CBColour;

/**
 * Indicates this Entity has a colour different from its texture colours, e.g.
 * the player can change between the different colours.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Coloured extends Component {
	public CBColour	colour	= null;

	/**
	 * Creates the Coloured component with a random colour.
	 */
	public Coloured() {
		this(new CBColour());
	}

	/**
	 * Creates the Coloured component with the given colour.
	 * 
	 * @param c
	 *            An initialised CBColour.
	 */
	public Coloured(CBColour c) {
		colour = c;
	}
}
