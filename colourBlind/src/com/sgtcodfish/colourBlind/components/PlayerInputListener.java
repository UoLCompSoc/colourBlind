package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Signifies that this component should react to input in the way a player
 * character should i.e. moving about with arrow keys/WASD, jumping, changing
 * colour, using a door.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class PlayerInputListener extends Component {
	public static final int[]	DEFAULT_JUMP_KEYS		= { Keys.SPACE };

	public static final int[]	DEFAULT_LEFT_KEYS		= { Keys.A, Keys.LEFT };
	public static final int[]	DEFAULT_RIGHT_KEYS		= { Keys.D, Keys.RIGHT };

	public static final int[]	DEFAULT_FLASHLIGHT_KEYS	= { Keys.E };
	public static final int[]	DEFAULT_USE_KEYS		= { Keys.UP, Keys.W, Keys.F };

	public static final int[]	DEFAULT_RED_KEYS		= { Keys.I };
	public static final int[]	DEFAULT_BLUE_KEYS		= { Keys.K };
	public static final int[]	DEFAULT_GREEN_KEYS		= { Keys.J };
	public static final int[]	DEFAULT_YELLOW_KEYS		= { Keys.L };

	public int[]				jumpKeys				= DEFAULT_JUMP_KEYS;

	public int[]				leftKeys				= DEFAULT_LEFT_KEYS;
	public int[]				rightKeys				= DEFAULT_RIGHT_KEYS;

	public int[]				flashlightKeys			= DEFAULT_FLASHLIGHT_KEYS;
	public int[]				useKeys					= DEFAULT_USE_KEYS;

	public int[]				redKeys					= DEFAULT_RED_KEYS;
	public int[]				blueKeys				= DEFAULT_BLUE_KEYS;
	public int[]				greenKeys				= DEFAULT_GREEN_KEYS;
	public int[]				yellowKeys				= DEFAULT_YELLOW_KEYS;

	/**
	 * Creates an input listener with sensible default keys, as defined as
	 * public static final int[] types in this class.
	 */
	public PlayerInputListener() {
	}

	/**
	 * Creates an input listener using user-specifed keys, which must have at
	 * least one key for each key type.
	 * 
	 * @param jumpKeys
	 *        The keys that cause a jump (increase y velocity).
	 * @param leftKeys
	 *        The keys that increase the -x velocity.
	 * @param rightKeys
	 *        The keys that increase the x velocity.
	 * @param flashlightKeys
	 *        The keys that activate the entity's flashlight.
	 * @param useKeys
	 *        The keys which cause an interaction (e.g. use a door).
	 * @param redKeys
	 *        The keys which change the colour to red.
	 * @param blueKeys
	 *        The keys which change the colour to blue.
	 * @param greenKeys
	 *        The keys which change the colour to green.
	 * @param yellowKeys
	 *        The keys which change the colour to yellow.
	 */
	public PlayerInputListener(int[] jumpKeys, int[] leftKeys, int[] rightKeys, int[] flashlightKeys, int[] useKeys,
			int[] redKeys, int[] blueKeys, int[] greenKeys, int[] yellowKeys) {
		if (jumpKeys.length == 0 || leftKeys.length == 0 || rightKeys.length == 0 || flashlightKeys.length == 0
				|| useKeys.length == 0 || redKeys.length == 0 || blueKeys.length == 0 || greenKeys.length == 0
				|| yellowKeys.length == 0) {
			throw new GdxRuntimeException("Trying to create PlayerInputListener component with invalid keys.");
		}

		this.jumpKeys = jumpKeys;

		this.leftKeys = leftKeys;
		this.rightKeys = rightKeys;

		this.flashlightKeys = flashlightKeys;
		this.useKeys = useKeys;

		this.redKeys = redKeys;
		this.blueKeys = blueKeys;
		this.greenKeys = greenKeys;
		this.yellowKeys = yellowKeys;
	}
}
