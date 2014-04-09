package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

/**
 * Checks to see if solid entities are in contact with other solid entities.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class CollisionSystem extends EntityProcessingSystem {

	public CollisionSystem() {
		// FIXME: Proper aspec
		super(Aspect.getEmpty());
	}

	public CollisionSystem(Aspect aspect) {
		super(aspect);
	}

	@Override
	protected void process(Entity e) {
	}

}
