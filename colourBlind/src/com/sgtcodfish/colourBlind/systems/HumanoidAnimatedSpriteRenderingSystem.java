package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.sgtcodfish.colourBlind.components.Coloured;
import com.sgtcodfish.colourBlind.components.Facing;
import com.sgtcodfish.colourBlind.components.HumanoidAnimatedSprite;
import com.sgtcodfish.colourBlind.components.Position;
import com.sgtcodfish.colourBlind.components.Velocity;

/**
 * Handles drawing a coloured animated humanoid sprite, capable of animating
 * running, standing or jumping. Uses a shader to achieve the colour; the shader
 * should have a uniform called "inputColour" for the player's colour.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class HumanoidAnimatedSpriteRenderingSystem extends EntityProcessingSystem {
	@Mapper
	private ComponentMapper<HumanoidAnimatedSprite>	hasm	= null;
	@Mapper
	private ComponentMapper<Position>				pm		= null;
	@Mapper
	private ComponentMapper<Velocity>				vm		= null;
	@Mapper
	private ComponentMapper<Facing>					fm		= null;
	@Mapper
	private ComponentMapper<Coloured>				cm		= null;

	public Batch									batch	= null;
	public ShaderProgram							program	= null;

	/**
	 * Creates a new HumanoidAnimatedSpriteRenderingSystem with the given batch
	 * and shader.
	 * 
	 * NB: Use the HumanoidAnimatedSpriteRenderingSystem(SpriteBatch) form to
	 * actually create the system.
	 * 
	 * @param aspect
	 *        The aspect to use for this system.
	 * @param batch
	 *        The batch to which we should render.
	 * @param shader
	 *        The shader to use. Must have a uniform called inputColour for the
	 *        Coloured component.
	 */
	protected HumanoidAnimatedSpriteRenderingSystem(Aspect aspect, Batch batch, ShaderProgram program) {
		super(aspect);
		if (batch == null) {
			throw new IllegalArgumentException(
					"Initialising HumanoidAnimatedSpriteRenderingSystem with null batch, did you use blank constructor of HumanoidAnimatedSpriteRenderingSystem called.");
		}

		if (program == null) {
			Gdx.app.debug("HUMANOID_ANIMATION_SYSTEM", "Warning: humanoid animation system created with null shader.");
		}

		this.batch = batch;
		this.program = program;
	}

	/**
	 * Creates a new HumanoidAnimatedSpriteRenderingSystem with the given batch
	 * and shader.
	 * 
	 * @param batch
	 *        The batch to which we should render.
	 * @param shader
	 *        The shader to use. Must have a uniform called inputColour for the
	 *        Coloured component.
	 */
	@SuppressWarnings("unchecked")
	public HumanoidAnimatedSpriteRenderingSystem(Batch batch, ShaderProgram program) {
		this(Aspect.getAspectForAll(HumanoidAnimatedSprite.class, Position.class, Velocity.class, Facing.class), batch,
				program);
	}

	/**
	 * Will throw an exception, do not use.
	 */
	public HumanoidAnimatedSpriteRenderingSystem() {
		this(null, null, null);
	}

	@Override
	protected void process(Entity e) {
		HumanoidAnimatedSprite has = hasm.get(e);
		Position p = pm.get(e);
		Velocity v = vm.get(e);
		boolean facingLeft = fm.get(e).facingLeft;

		has.stateTime += world.getDelta();

		TextureRegion frame = null;

		// calculate state
		if (v.velocity.y != 0.0f) {
			frame = has.jump.getKeyFrame(has.stateTime);
		} else if (v.velocity.x != 0.0f) {
			frame = has.run.getKeyFrame(has.stateTime);
		} else {
			frame = has.stand.getKeyFrame(has.stateTime);
		}

		batch.begin();
		batch.setShader(program);
		program.setUniformf("inputColour", cm.get(e).colour.toGdxColour());
		program.setUniformf("platform", 0.0f);
		batch.draw(frame, p.position.x + (facingLeft ? (float) has.width : 0.0f), p.position.y, (float) has.width
				* (facingLeft ? -1.0f : 1.0f), (float) has.height);
		batch.end();
	}
}
