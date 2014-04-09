package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.sgtcodfish.colourBlind.components.Flashlight;
import com.sgtcodfish.colourBlind.components.Position;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class FlashlightSystem extends EntityProcessingSystem {
	public static final String	GDX_DEBUG_TAG	= "FLASHLIGHT_SYSTEM";

	@Mapper
	ComponentMapper<Position>	pm;
	@Mapper
	ComponentMapper<Flashlight>	fm;

	@SuppressWarnings("unchecked")
	public FlashlightSystem() {
		super(Aspect.getAspectForAll(Flashlight.class, Position.class));
	}

	public FlashlightSystem(Aspect aspect) {
		super(aspect);
	}

	@Override
	protected void process(Entity e) {
		Flashlight f = fm.get(e);
		float delta = world.getDelta();

		if (f.onTime >= 0.0f) {
			f.onTime += delta;

			handleFlashlightOn(e);

			if (f.onTime >= f.duration) {
				Gdx.app.debug(GDX_DEBUG_TAG, f + " time up. Cooldown starting.");
				f.onTime = Flashlight.NOT_ON;
				f.cooldownRemaining = f.cooldown;
			}
		} else if (f.cooldownRemaining >= 0.0f) {
			f.cooldownRemaining -= delta;

			if (f.cooldownRemaining <= 0.0f) {
				Gdx.app.debug(GDX_DEBUG_TAG, f + " finished cooldown.");
				f.cooldownRemaining = Flashlight.NOT_ON;
			}
		} else {
			if (f.flaggedForStart) {
				if (f.usable()) {
					Gdx.app.debug(GDX_DEBUG_TAG, f + " started.");
					f.onTime = 0.0f;
				}

				f.flaggedForStart = false;
			}
		}
	}

	protected void handleFlashlightOn(Entity e) {

	}
}
