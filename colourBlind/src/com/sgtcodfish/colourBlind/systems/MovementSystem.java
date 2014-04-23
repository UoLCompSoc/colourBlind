package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.sgtcodfish.colourBlind.components.Facing;
import com.sgtcodfish.colourBlind.components.Position;
import com.sgtcodfish.colourBlind.components.Velocity;
import com.sgtcodfish.colourBlind.components.Weight;

/**
 * Very simply handles the movement of entities by adding their velocity to
 * their position, after handling the effects of gravity or any other
 * accelerations.
 * 
 * Also handles changing an entitiy's Facing, if it has one.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class MovementSystem extends EntityProcessingSystem {
	public static final float	GRAVITY	= 1.0f;

	@Mapper
	ComponentMapper<Position>	pm		= null;
	@Mapper
	ComponentMapper<Velocity>	vm		= null;
	@Mapper
	ComponentMapper<Weight>		wm		= null;
	@Mapper
	ComponentMapper<Facing>		fm		= null;

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
		Weight w = wm.get(e);

		if (w != null) {
			velocity.y -= w.weight * GRAVITY;
		}

		Facing f = fm.get(e);
		if (f != null && velocity.x != 0.0f) {
			f.facingLeft = (velocity.x < 0.0f);
		}

		velocity.x *= 0.75f;

		if (Math.abs(velocity.x) < 0.25f) {
			velocity.x = 0.0f;
		}

		if (Math.abs(velocity.y) < 0.25f) {
			velocity.y = 0.0f;
		}

		position.add(velocity);
	}
}
