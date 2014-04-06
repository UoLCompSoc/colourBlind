package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.sgtcodfish.colourBlind.components.Position;
import com.sgtcodfish.colourBlind.components.Velocity;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class MovementSystem extends EntityProcessingSystem {
	@Mapper
	ComponentMapper<Position>	pm;
	@Mapper
	ComponentMapper<Velocity>	vm;

	@SuppressWarnings("unchecked")
	public MovementSystem() {
		super(Aspect.getAspectForAll(Position.class, Velocity.class));
	}

	public MovementSystem(Aspect aspect) {
		super(aspect);
	}

	@Override
	protected void process(Entity e) {
		Vector2 position = pm.get(e).position;
		Vector2 velocity = vm.get(e).velocity;

		position.add(velocity.scl(world.getDelta()));
	}

}
