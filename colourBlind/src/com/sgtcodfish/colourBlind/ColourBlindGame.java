package com.sgtcodfish.colourBlind;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.artemis.World;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.sgtcodfish.colourBlind.systems.CollisionSystem;
import com.sgtcodfish.colourBlind.systems.FlashlightSystem;
import com.sgtcodfish.colourBlind.systems.HumanoidAnimatedSpriteRenderingSystem;
import com.sgtcodfish.colourBlind.systems.MovementSystem;
import com.sgtcodfish.colourBlind.systems.PlayerInputSystem;
import com.sgtcodfish.colourBlind.systems.TiledMapRenderingSystem;

/**
 * Colour Blind, game entry for Global Game Jam 2014 by the University of
 * Leicester computing society.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class ColourBlindGame implements ApplicationListener {
	private static ColourBlindGame	instance			= null;
	public static boolean			DEBUG				= false;

	// whether or not to use the glow special effect
	private static boolean			USE_GLOW			= true;

	// note that USE_SOUND is only followed at load-time;
	// if the game was loaded without sounds you can't start them
	private static boolean			USE_SOUND			= true;

	public World					world				= null;
	private PlayerInputSystem		playerInputSystem	= null;
	private MovementSystem			movementSystem		= null;

	private PlayerEntityFactory		playerFactory		= null;

	public Batch					batch				= null;

	private LevelFactory			levelFactory		= null;
	private OrthographicCamera		camera				= null;

	private ShaderProgram			colourShader		= null;

	private BGM						bgm					= null;

	public ColourBlindGame() {
		this(false, true, false);
	}

	public ColourBlindGame(boolean debug, boolean playSound, boolean glow) {
		if (instance == null) {
			ColourBlindGame.instance = this;
		} else {
			throw new GdxRuntimeException("Trying to instantiate a new"
					+ "instance of ColourBlindGame when one already exists.");
		}

		ColourBlindGame.DEBUG = debug;
		ColourBlindGame.USE_SOUND = playSound;
		ColourBlindGame.USE_GLOW = glow;
	}

	@Override
	public void create() {
		ShaderProgram.pedantic = false;

		if (DEBUG) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		} else {
			Gdx.app.setLogLevel(Application.LOG_NONE);
		}

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		batch = new SpriteBatch();

		levelFactory = new LevelFactory(batch, "data/maps/");

		loadShaders();
		setupSound();
		setupGlow();

		playerFactory = new PlayerEntityFactory("data/RaySprites.png",
				PlayerEntityFactory.DEFAULT_PLAYER_TEXTURE_WIDTH, PlayerEntityFactory.DEFAULT_PLAYER_TEXTURE_HEIGHT);

		world = new World();

		world.setSystem(new PlayerInputSystem());
		world.setSystem(new MovementSystem());
		world.setSystem(new FlashlightSystem());
		world.setSystem(new CollisionSystem());
		world.setSystem(new TiledMapRenderingSystem(camera, batch, colourShader));
		world.setSystem(new HumanoidAnimatedSpriteRenderingSystem(camera, batch, colourShader));

		world.initialize();

		world.addEntity(playerFactory.createPlayerEntity(world));
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		world.setDelta(deltaTime);

		Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		playerInputSystem.process();
		movementSystem.process();

		world.process();

	}

	// returns true to exit
	public boolean nextLevel() {
		throw new NotImplementedException();
	}

	@Override
	public void dispose() {
		if (playerFactory != null) {
			playerFactory.dispose();
		}

		if (colourShader != null)
			colourShader.dispose();
		if (bgm != null)
			bgm.dispose();
		if (levelFactory != null)
			levelFactory.dispose();
	}

	protected void setupSound() {
		bgm = new BGM();

		if (USE_SOUND) {
			bgm.create();
			Gdx.app.debug("LOAD_SOUND", "Loaded sounds correctly, playing.");
			bgm.play();
		} else {
			Gdx.app.debug("LOAD_SOUND", "Sounds disabled.");
		}
	}

	protected void setupGlow() {
		if (USE_GLOW) {
			Gdx.app.debug("LOAD_GLOW", "Glow effect enabled.");
		}
	}

	protected void loadShaders() {
		colourShader = new ShaderProgram(Gdx.files.internal("data/lights3.glslv").readString(), Gdx.files.internal(
				"data/lights3.glslf").readString());

		if (colourShader.isCompiled() == false) {
			throw new GdxRuntimeException("Failed to compile lights3.glslf/lights3.glslv:\n" + colourShader.getLog());
		} else {
			Gdx.app.debug("LOAD_SHADERS", "Compiled colour shader.");

			if (colourShader.getLog().length() > 0) {
				Gdx.app.debug("LOAD_SHADERS", "Colour shader log:" + colourShader.getLog());
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		camera.update();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public static ColourBlindGame getInstance() {
		return instance;
	}
}
