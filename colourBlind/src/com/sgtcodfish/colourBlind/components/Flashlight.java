package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.sgtcodfish.colourBlind.systems.FlashlightSystem;

/**
 * Indicates that an Entity can have a "flashlight" coming from it, as in the
 * game flashlight which reveals "true" colours.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Flashlight extends Component {
	public static final float	DEFAULT_COOLDOWN	= 2.0f;
	public static final float	DEFAULT_DURATION	= 2.0f;
	public static final float	DEFAULT_RADIUS		= 8.0f;

	/** A value is not on (not on cooldown, not turned on) if it is <= NOT_ON. */
	public static final float	NOT_ON				= -1.0f;

	private String				name				= "Unnamed Flashlight";

	public float				cooldown			= NOT_ON;
	public float				onTime				= NOT_ON;
	public float				duration			= DEFAULT_COOLDOWN;

	public boolean				flaggedForStart		= false;

	public Flashlight() {
		this.name = "Unnamed Flashlight";
	}

	/**
	 * Creates a new flashlight with the given name.
	 * 
	 * @param name
	 *        The descriptive name to give this flashlight.
	 */
	public Flashlight(String name) {
		this.name = name;
	}

	/**
	 * Indicates that the {@link FlashlightSystem} should start this flashlight
	 * on its next pass. This function does NOT immediately start the
	 * flashlight, although it should start fairly soon.
	 */
	public void flagForStart() {
		flaggedForStart = true;
	}

	/**
	 * @return True if this flashlight can be activated, false otherwise.
	 */
	public boolean usable() {
		return cooldown <= 0.0f;
	}

	/**
	 * Resets the flashlight to the off position, and makes it usable again.
	 */
	public void reset() {
		cooldown = NOT_ON;
		onTime = NOT_ON;
	}

	/**
	 * Overriden to allow for naming individual flashlights; useful for
	 * debugging.
	 */
	@Override
	public String getComponentName() {
		return name;
	}
}
