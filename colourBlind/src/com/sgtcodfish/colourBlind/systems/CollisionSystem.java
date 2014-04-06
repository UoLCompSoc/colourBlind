package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

/**
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
