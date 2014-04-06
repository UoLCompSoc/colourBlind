package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;

/**
 * Indicates the entity has weight, i.e. it is affected by gravity.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Weight extends Component {
	public static final float	DEFAULT_WEIGHT	= 1.0f;

	public float				weight			= DEFAULT_WEIGHT;

	public Weight() {
		this(DEFAULT_WEIGHT);
	}

	public Weight(float weight) {
		this.weight = weight;
	}
}
