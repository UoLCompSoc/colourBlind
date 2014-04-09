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

	private static final String	DEFAULT_NAME		= "Unnamed Flashlight";

	/** A value is not on (not on cooldown, not turned on) if it is <= NOT_ON. */
	public static final float	NOT_ON				= -1.0f;

	private String				name				= DEFAULT_NAME;

	// how long until cooldown is finished
	public float				cooldownRemaining	= NOT_ON;

	// how long the light has been on for
	public float				onTime				= NOT_ON;

	// how long the light takes to cool down
	public final float			cooldown;

	// how long the light stays on for
	public final float			duration;

	public boolean				flaggedForStart		= false;

	/**
	 * Creates a flashlight with a default (not very descriptive) name and
	 * sensible defaults for cooldown and duration.
	 */
	public Flashlight() {
		this(DEFAULT_NAME, DEFAULT_DURATION, DEFAULT_COOLDOWN);
	}

	/**
	 * Creates a new flashlight with the given name, and sensible defaults for
	 * cooldown and duration.
	 * 
	 * @param name
	 *        The descriptive name to give this flashlight.
	 */
	public Flashlight(String name) {
		this(name, DEFAULT_DURATION, DEFAULT_COOLDOWN);
	}

	/**
	 * Creates a flashlight with a default (not very descriptive) name and the
	 * given duration and cooldown.
	 * 
	 * @param duration
	 *        The amount of time the flashlight stays on for.
	 * @param cooldown
	 *        The amount of time the flashlight needs to cool down.
	 */
	public Flashlight(float duration, float cooldown) {
		this(DEFAULT_NAME, duration, cooldown);
	}

	/**
	 * Creates a flashlight with the given name, duration and cooldown.
	 * 
	 * @param name
	 *        The descriptive name to give this flashlight.
	 * @param duration
	 *        The amount of time the flashlight stays on for.
	 * @param cooldown
	 *        The amount of time the flashlight needs to cool down.
	 */
	public Flashlight(String name, float duration, float cooldown) {
		this.duration = duration;
		this.cooldown = cooldown;
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
		return cooldownRemaining <= 0.0f;
	}

	/**
	 * Resets the flashlight to the off position, and makes it usable again.
	 */
	public void reset() {
		cooldownRemaining = NOT_ON;
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
