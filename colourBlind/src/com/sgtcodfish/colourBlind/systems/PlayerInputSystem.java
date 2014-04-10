package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.sgtcodfish.colourBlind.Player;
import com.sgtcodfish.colourBlind.components.Coloured;
import com.sgtcodfish.colourBlind.components.Facing;
import com.sgtcodfish.colourBlind.components.Flashlight;
import com.sgtcodfish.colourBlind.components.PlayerInputListener;
import com.sgtcodfish.colourBlind.components.Velocity;

/**
 * Handles input for Entities with PlayerInputListener components.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class PlayerInputSystem extends EntityProcessingSystem {
	@Mapper
	ComponentMapper<PlayerInputListener>	pim	= null;
	@Mapper
	ComponentMapper<Velocity>				vm	= null;
	@Mapper
	ComponentMapper<Flashlight>				fm	= null;
	@Mapper
	ComponentMapper<Coloured>				cm	= null;
	@Mapper
	ComponentMapper<Facing>					fam	= null;

	@SuppressWarnings("unchecked")
	public PlayerInputSystem() {
		super(Aspect.getAspectForAll(PlayerInputListener.class, Velocity.class, Flashlight.class, Coloured.class,
				Facing.class));
	}

	public PlayerInputSystem(Aspect aspect) {
		super(aspect);
	}

	protected void handleJump(Entity e) {
		vm.get(e).velocity.y += Player.JUMP_VELOCITY;
	}

	/**
	 * Increases the -x velocity of the Entity, analogous to running left.
	 * 
	 * @param e
	 *        The entity whose velocity is to be changed.
	 */
	protected void handleMoveLeft(Entity e) {
		Velocity v = vm.get(e);
		v.velocity.x -= Player.RUN_VELOCITY;
	}

	/**
	 * Increases the x velocity of the Entity, analogous to running right.
	 * 
	 * @param e
	 *        The entity whose velocity is to be changed.
	 */
	protected void handleMoveRight(Entity e) {
		Velocity v = vm.get(e);
		v.velocity.x += Player.RUN_VELOCITY;
	}

	/**
	 * Turns on the Entity's flash light, if it can be turned on.
	 * 
	 * @param e
	 *        The entity whose flashlight should be turned on if possible.
	 */
	protected void handleActivateFlashlight(Entity e) {
		Flashlight f = fm.get(e);

		if (f.usable()) {
			f.flagForStart();
		}
	}

	/**
	 * If there's something with which the player can interact, interact with
	 * it. For example, go through a door.
	 * 
	 * @param e
	 *        The entity which should do the interacting.
	 */
	protected void handleUse(Entity e) {
		throw new GdxRuntimeException("PlayerInputSystem.handleUse NYI.");
	}

	/**
	 * Turns the given Entity red.
	 * 
	 * @param e
	 *        The entity whose colour is to be changed.
	 */
	protected void handleTurnRed(Entity e) {
		cm.get(e).colour = Coloured.COLOUR_RED;
	}

	/**
	 * Turns the given Entity blue.
	 * 
	 * @param e
	 *        The entity whose colour is to be changed.
	 */
	protected void handleTurnBlue(Entity e) {
		cm.get(e).colour = Coloured.COLOUR_BLUE;
	}

	/**
	 * Turns the given Entity green.
	 * 
	 * @param e
	 *        The entity whose colour is to be changed.
	 */
	protected void handleTurnGreen(Entity e) {
		cm.get(e).colour = Coloured.COLOUR_GREEN;
	}

	/**
	 * Turns the given Entity yellow.
	 * 
	 * @param e
	 *        The entity whose colour is to be changed.
	 */
	protected void handleTurnYellow(Entity e) {
		cm.get(e).colour = Coloured.COLOUR_YELLOW;
	}

	/**
	 * Handles any input as described by the arrays of keys in the associated
	 * PlayerInputListener.
	 */
	@Override
	protected void process(Entity e) {
		PlayerInputListener inputListener = pim.get(e);

		for (int key : inputListener.jumpKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleJump(e);
				break;
			}
		}

		for (int key : inputListener.leftKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleMoveLeft(e);
				break;
			}
		}

		for (int key : inputListener.rightKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleMoveRight(e);
				break;
			}
		}

		for (int key : inputListener.flashlightKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleActivateFlashlight(e);
				break;
			}
		}

		for (int key : inputListener.useKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleUse(e);
				break;
			}
		}

		for (int key : inputListener.redKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleTurnRed(e);
				break;
			}
		}

		for (int key : inputListener.blueKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleTurnBlue(e);
				break;
			}
		}

		for (int key : inputListener.greenKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleTurnGreen(e);
				break;
			}
		}

		for (int key : inputListener.yellowKeys) {
			if (Gdx.input.isKeyPressed(key)) {
				handleTurnYellow(e);
				break;
			}
		}
	}
}
