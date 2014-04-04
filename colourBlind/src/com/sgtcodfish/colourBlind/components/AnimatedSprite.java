package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Holds an animated sprite for an Entity.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class AnimatedSprite extends Component {
	public Animation	animation	= null;

	/**
	 * The AnimatedSprite(Animation) constructor should always be used, having a
	 * null animation is bad.
	 */
	protected AnimatedSprite() {
	}

	/**
	 * Initialises the animation in this AnimatedSprite with a pre-made Gdx
	 * Animation.
	 * 
	 * @param a
	 *            The animation to use.
	 */
	public AnimatedSprite(Animation a) {
		animation = a;
	}
}
