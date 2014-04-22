package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.sgtcodfish.colourBlind.CBColour;
import com.sgtcodfish.colourBlind.CBColour.GameColour;

/**
 * Indicates this Entity has a colour different from its texture colours, e.g.
 * the player can change between the different colours.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Coloured extends Component {
	public static final CBColour	COLOUR_RED		= new CBColour(GameColour.RED);
	public static final CBColour	COLOUR_BLUE		= new CBColour(GameColour.BLUE);
	public static final CBColour	COLOUR_GREEN	= new CBColour(GameColour.GREEN);
	public static final CBColour	COLOUR_YELLOW	= new CBColour(GameColour.YELLOW);

	public static final CBColour	DEFAULT_COLOUR	= COLOUR_RED;

	public CBColour					colour			= DEFAULT_COLOUR;

	/**
	 * Creates the Coloured component in the DEFAULT_COLOUR.
	 */
	public Coloured() {
		this(DEFAULT_COLOUR);
	}

	/**
	 * Creates the Coloured component with the given colour.
	 * 
	 * @param c
	 *        An initialised CBColour.
	 */
	public Coloured(CBColour c) {
		colour = c;
	}
}
