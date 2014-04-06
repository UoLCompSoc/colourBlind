package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Indicates that an Entity's animation states can be humanoid; that is it can
 * stand, jump or run and has corresponding animations for each.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class HumanoidAnimatedSprite extends Component {
	public Animation	stand	= null;
	public Animation	jump	= null;
	public Animation	run		= null;

	/**
	 * The AnimatedSprite(Animation) constructor should always be used, having a
	 * null animation is bad.
	 */
	protected HumanoidAnimatedSprite() {
	}

	/**
	 * Initialises the HumanoidAnimatedSprite with user provided animations.
	 * 
	 * @param stand
	 *        The standing animation.
	 * @param run
	 *        The running animation.
	 * @param jump
	 *        The jumping animation.
	 */
	public HumanoidAnimatedSprite(Animation stand, Animation run, Animation jump) {
		if (stand == null || run == null || jump == null) {
			throw new IllegalArgumentException("HumanoidAnimatedSprite must have valid animations in its constructor.");
		}

		this.stand = stand;
		this.run = run;
		this.jump = jump;
	}
}
