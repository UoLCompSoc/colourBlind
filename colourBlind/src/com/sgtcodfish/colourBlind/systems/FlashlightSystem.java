package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.sgtcodfish.colourBlind.components.Flashlight;
import com.sgtcodfish.colourBlind.components.Position;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class FlashlightSystem extends EntityProcessingSystem {
	public static final String	GDX_DEBUG_TAG		= "FLASHLIGHT_SYSTEM";
	/** The maximum number of lights that can be handled by the shader. */
	public static final int		MAX_SHADER_LIGHTS	= 8;
	/** The number of floats which are needed to describe a flashlight */
	public static final int		FLASHLIGHT_FLOATS	= 3;
	public static final int		SHADER_ARRAY_SIZE	= MAX_SHADER_LIGHTS * FLASHLIGHT_FLOATS;

	@Mapper
	ComponentMapper<Position>	pm;
	@Mapper
	ComponentMapper<Flashlight>	fm;

	public float[]				lightList			= null;
	public int					lightsForShader		= 0;

	@SuppressWarnings("unchecked")
	public FlashlightSystem() {
		this(Aspect.getAspectForAll(Flashlight.class, Position.class));
	}

	public FlashlightSystem(Aspect aspect) {
		super(aspect);

		// (x, y, radius) for each of the 8 possible lights
		lightList = new float[SHADER_ARRAY_SIZE];
	}

	@Override
	protected void begin() {
		lightsForShader = 0;
	}

	@Override
	protected void process(Entity e) {
		Flashlight f = fm.get(e);
		Vector2 p = pm.get(e).position;
		float delta = world.getDelta();

		if (f.onTime >= 0.0f) {
			f.onTime += delta;

			handleFlashlightOn(p.x, p.y, f.radius);

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

	/**
	 * Updates the lightList array to hold a given position and radius of an
	 * "on" flashlight.
	 * 
	 * @param x
	 *        The x coordinate of the light.
	 * @param y
	 *        The y coordinate of the light.
	 * @param radius
	 *        The radius of the light.
	 */
	protected void handleFlashlightOn(float x, float y, float radius) {
		int lfs3 = lightsForShader * 3;
		lightList[lfs3 + 0] = x;
		lightList[lfs3 + 1] = y;
		lightList[lfs3 + 2] = radius;
		lightsForShader++;
	}

	/**
	 * Sets up a LibGDX {@link ShaderProgram} with a uniform called
	 * "float flashlights[MAX_SHADER_LIGHTS];" which contains the coordinates
	 * and radii of all the flashlights which are "on" during this tick.
	 * 
	 * Also sets a uniform called "lightsOn" which is the number of lights in
	 * the array, 0 <= lightsOn <= MAX_SHADER_LIGHTS. This should be used to
	 * iterate over the lights.
	 * 
	 * @param program
	 *        The program whose uniforms will be modified.
	 */
	public void setupShaderUniform(ShaderProgram program) {
		program.setUniform3fv("flashlights", lightList, 0, SHADER_ARRAY_SIZE);
		program.setUniformf("lightsOn", lightsForShader);
	}
}
