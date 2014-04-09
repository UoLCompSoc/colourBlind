package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;

/**
 * Indicates the entity has weight, i.e. it is affected by gravity. The weight
 * is the multiplier for gravity on this object. A weight of 1.0f indicates
 * gravity is applied uniformly. A weight of 2.0f indicates gravity is twice as
 * strong on this object.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Weight extends Component {
	public static final float	DEFAULT_WEIGHT	= 1.0f;

	public float				weight			= DEFAULT_WEIGHT;

	/**
	 * Create a weight with the default value, that is a weight which applies
	 * gravity constantly.
	 */
	public Weight() {
		this(DEFAULT_WEIGHT);
	}

	/**
	 * Create a weight with the given weight.
	 * 
	 * @param weight
	 *        The weight given to the object.
	 */
	public Weight(float weight) {
		this.weight = weight;
	}
}
