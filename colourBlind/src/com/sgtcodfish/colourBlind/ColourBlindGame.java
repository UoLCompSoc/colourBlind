package com.sgtcodfish.colourBlind;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Colour Blind, game entry for Global Game Jam 2014 by the University of
 * Leicester computing society.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class ColourBlindGame implements ApplicationListener {
	private static ColourBlindGame	instance		= null;
	public static boolean			DEBUG			= false;

	private Player					player			= null;
	private Level					level			= null;
	private OrthographicCamera		camera			= null;

	public static final int			LIGHT_SIZE		= 16;
	public static final float		UPSCALE			= 1.0f;

	private static boolean			USE_SOUND		= true;
	// note that USE_SOUND is only followed at load-time;
	// if the game was loaded without sounds you can't start them

	private ShaderProgram			colourShader	= null;

	private int						currentLevel	= 0;
	private ArrayList<String>		levelList		= null;

	private BGM						bgm				= null;

	private FPSLogger				fpsLogger		= null;
	private int						fpsPrintCounter	= 0;

	public ColourBlindGame() {
		this(false, true);
	}

	public ColourBlindGame(boolean debug, boolean playSound) {
		if (instance == null) {
			ColourBlindGame.instance = this;
		} else {
			throw new GdxRuntimeException("Trying to instantiate a new"
					+ "instance of ColourBlindGame when one already exists.");
		}

		ColourBlindGame.DEBUG = debug;
		ColourBlindGame.USE_SOUND = playSound;
	}

	@Override
	public void create() {
		ShaderProgram.pedantic = false;

		if (DEBUG) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
			fpsLogger = new FPSLogger();
		} else {
			Gdx.app.setLogLevel(Application.LOG_NONE);
		}

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		player = new Player();

		levelList = new ArrayList<String>();
		while (true) {
			String fname = "level" + (levelList.size() + 1) + ".tmx";
			String pathName = "data/maps/" + fname;
			FileHandle fh = Gdx.files.internal(pathName);

			if (fh.exists()) {
				levelList.add(fname);
			} else {
				break;
			}
		}

		if (levelList.size() == 0) {
			throw new GdxRuntimeException("Couldn't load any levels.");
		}

		Gdx.app.debug("LEVEL_COUNT", "" + levelList.size() + " levels loaded.");
		level = new Level(levelList.get(currentLevel));

		colourShader = new ShaderProgram(Gdx.files.internal(
				"data/lights3.glslv").readString(), Gdx.files.internal(
				"data/lights3.glslf").readString());

		if (colourShader.isCompiled() == false) {
			throw new GdxRuntimeException("Failed to compile lights3.glslf:\n"
					+ colourShader.getLog());
		} else {
			Gdx.app.debug("LOAD_SHADERS", "Compiled colour shader.");
		}

		bgm = new BGM();
		if (USE_SOUND) {
			bgm.create();
			Gdx.app.debug("LOAD_SOUND", "Loaded sounds correctly, playing.");
			bgm.play();
		} else {
			Gdx.app.debug("LOAD_SOUND", "Sounds disabled.");
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();
		SpriteBatch sb = (SpriteBatch) level.renderer.getSpriteBatch();
		sb.setShader(null);

		if (DEBUG) {
			fpsPrintCounter++;

			if (fpsPrintCounter >= 100) {
				fpsLogger.log();
				fpsPrintCounter = 0;
			} else if (Gdx.input.isKeyPressed(Keys.F1)) {
				fpsLogger.log();
			}
		}

		if (Gdx.input.isKeyPressed(Keys.F5)) {
			bgm.toggle();
		}

		if (player.update(level, deltaTime)) {
			player.resetFlashLight();
			// true if player went through a door
			if (nextLevel()) {
				// true if there's no next level
				// we're done
				Gdx.app.exit();
			} else {
				// onwards
			}

			return;
		}

		camera.position.x = player.position.x;
		camera.position.y = player.position.y;
		camera.update();

		camera.setToOrtho(false, level.WIDTH_IN_TILES, level.HEIGHT_IN_TILES);
		camera.position.x = player.position.x;
		camera.position.y = player.position.y;
		camera.update();

		sb.begin();
		sb.setShader(null);
		sb.setColor(Color.WHITE);
		sb.setProjectionMatrix(camera.combined);

		level.renderLevel(camera);
		level.renderDoor(camera);
		sb.end();

		sb.begin();
		sb.setShader(colourShader);

		colourShader.setUniformf("flashLightSize", ((float) LIGHT_SIZE / 2.0f));
		colourShader.setUniformf("platform", 1.0f);
		colourShader.setUniformf("lightCoord", player.position);
		colourShader.setUniformf("flashLight", (player.isLightOn() ? 1.0f
				: 0.0f));

		level.renderPlatforms(camera);
		sb.end();

		sb.begin();
		colourShader.setUniformf("platform", 0.0f);
		colourShader.setUniformf("inputColour", player.getPlayerColour()
				.toGdxColour());
		player.render(sb);
		sb.end();
	}

	// returns true to exit
	public boolean nextLevel() {
		currentLevel++;
		if (currentLevel >= levelList.size()) {
			// we're done
			return true;
		} else {
			level.dispose();

			level = new Level(levelList.get(currentLevel));
			player.position.set(3, 1);
			return false;
		}
	}

	@Override
	public void dispose() {
		if (colourShader != null)
			colourShader.dispose();
		if (bgm != null)
			bgm.dispose();
		if (player != null)
			player.dispose();
		if (level != null)
			level.dispose();
	}

	@Override
	public void resize(int width, int height) {
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

	public Player getPlayer() {
		return player;
	}

	public Level getLevel() {
		return level;
	}
}
