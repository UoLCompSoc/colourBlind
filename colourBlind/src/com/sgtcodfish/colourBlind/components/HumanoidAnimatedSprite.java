package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Indicates that an Entity's animation states can be humanoid; that is it can
 * stand, jump or run and has corresponding animations for each.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class HumanoidAnimatedSprite extends Component {
	public static final float	DEFAULT_SCALING_FACTOR	= 1.0f;

	public Animation			stand					= null;
	public Animation			jump					= null;
	public Animation			run						= null;

	public float				stateTime				= 0.0f;

	public float				scalingFactor			= DEFAULT_SCALING_FACTOR;

	public float				width					= 0.0f;
	public float				height					= 0.0f;

	/**
	 * The AnimatedSprite(Animation) constructor should always be used, having a
	 * null animation is bad.
	 */
	protected HumanoidAnimatedSprite() {
	}

	/**
	 * Initialises the HumanoidAnimatedSprite with user provided animations and
	 * a non-changing scaling factor.
	 * 
	 * @param stand
	 *        The standing animation.
	 * @param run
	 *        The running animation.
	 * @param jump
	 *        The jumping animation.
	 */
	public HumanoidAnimatedSprite(Animation stand, Animation run, Animation jump) {
		this(DEFAULT_SCALING_FACTOR, stand, run, jump);
	}

	/**
	 * Initialises the HumanoidAnimatedSprite with user provided animations and
	 * scaling factor.
	 * 
	 * @param scaleFactor
	 *        The scaling factor to apply to this sprite when rendering; a scale
	 *        factor of 0.5 would correspond to the object being drawn half its
	 *        size.
	 * @param stand
	 *        The standing animation.
	 * @param run
	 *        The running animation.
	 * @param jump
	 *        The jumping animation.
	 */
	public HumanoidAnimatedSprite(float scalingFactor, Animation stand, Animation run, Animation jump) {
		if (stand == null || run == null || jump == null) {
			throw new IllegalArgumentException("HumanoidAnimatedSprite must have valid animations in its constructor.");
		}

		this.stand = stand;
		this.run = run;
		this.jump = jump;

		TextureRegion standRegion = stand.getKeyFrames()[0];
		TextureRegion runRegion = run.getKeyFrames()[0];
		TextureRegion jumpRegion = jump.getKeyFrames()[0];

		// Check the widths and heights to make sure they're consistent
		if (standRegion.getRegionWidth() == runRegion.getRegionWidth()
				&& standRegion.getRegionWidth() == jumpRegion.getRegionWidth()
				&& standRegion.getRegionHeight() == runRegion.getRegionHeight()
				&& standRegion.getRegionHeight() == jumpRegion.getRegionHeight()) {
			this.width = standRegion.getRegionWidth();
			this.height = standRegion.getRegionHeight();
		} else {
			throw new IllegalArgumentException(
					"Discrepancy in sizes of animations in HumanoidAnimatedSprite(Animation, Animation, Animation).");
		}

		setScalingFactor(scalingFactor);
	}

	/**
	 * Sets the object's scaling factor and modifies its width and height
	 * accordingly.
	 * 
	 * @param scalingFactor
	 *        The new scaling factor to use.
	 */
	public void setScalingFactor(float scalingFactor) {
		this.scalingFactor = scalingFactor;
		this.width *= this.scalingFactor;
		this.height *= this.scalingFactor;
	}
}
