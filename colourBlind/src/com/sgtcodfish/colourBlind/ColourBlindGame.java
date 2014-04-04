package com.sgtcodfish.colourBlind;

import java.util.ArrayList;

import com.artemis.World;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Colour Blind, game entry for Global Game Jam 2014 by the University of
 * Leicester computing society.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class ColourBlindGame implements ApplicationListener {
	public World					world			= null;

	private static ColourBlindGame	instance		= null;
	public static boolean			DEBUG			= false;

	private Player					player			= null;
	private Level					level			= null;
	private OrthographicCamera		camera			= null;

	public static final int			LIGHT_SIZE		= 16;
	public static final float		UPSCALE			= 1.0f;

	public static boolean			USE_GLOW		= true;
	// whether or not to use the glow special effect

	private static boolean			USE_SOUND		= true;
	// note that USE_SOUND is only followed at load-time;
	// if the game was loaded without sounds you can't start them

	private ShaderProgram			colourShader	= null;

	private int						currentLevel	= 0;
	private ArrayList<String>		levelList		= null;

	private BGM						bgm				= null;

	private FPSLogger				fpsLogger		= null;

	// for use with glow effect
	private FrameBuffer				glowBuffer		= null;
	private TextureRegion			blurFBORegion	= null;

	private ShaderProgram			blurShader		= null;

	private final float				BLUR_RADIUS		= 2.0f;

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
			fpsLogger = new FPSLogger();
			// ShaderProgram.pedantic = true;
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

		loadShaders();

		bgm = new BGM();
		if (USE_SOUND) {
			bgm.create();
			Gdx.app.debug("LOAD_SOUND", "Loaded sounds correctly, playing.");
			bgm.play();
		} else {
			Gdx.app.debug("LOAD_SOUND", "Sounds disabled.");
		}

		if (USE_GLOW) {
			Gdx.app.debug("GLOW_ENABLED", "The glow effect has been enabled.");
			/* TODO: Change these magic numbers */
			glowBuffer = new FrameBuffer(Format.RGBA8888, (int) 128, (int) 128,
					false);

			blurFBORegion = new TextureRegion(
					glowBuffer.getColorBufferTexture());
			blurFBORegion.flip(false, true);
		}
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		SpriteBatch sb = (SpriteBatch) level.renderer.getSpriteBatch();

		if (DEBUG) {
			if (Gdx.input.isKeyPressed(Keys.F1)) {
				fpsLogger.log();
			}

			if (Gdx.input.isKeyPressed(Keys.F10)) {
				USE_GLOW = !USE_GLOW;
				Gdx.app.debug("GLOW_CHANGE", "Glow is: "
						+ (USE_GLOW ? "on." : "off."));
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
			}

			return;
		}

		if (USE_GLOW) {
			glowBuffer.begin();
			Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

			camera.setToOrtho(false, glowBuffer.getWidth(),
					glowBuffer.getHeight());
			camera.update();
			sb.setProjectionMatrix(camera.combined);

			sb.begin();
			sb.setShader(null);
			player.render(sb, 0.0f, 0.0f);
			sb.end();
			glowBuffer.end();
		}

		Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.setToOrtho(false, level.WIDTH_IN_TILES, level.HEIGHT_IN_TILES);
		camera.position.x = player.position.x;
		camera.position.y = player.position.y;
		camera.update();
		sb.setProjectionMatrix(camera.combined);

		sb.begin();
		sb.setShader(null);
		sb.setColor(Color.WHITE);

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
		if (USE_GLOW) {
			sb.setShader(blurShader);
			blurShader.setUniformf("radius", BLUR_RADIUS);
			blurShader.setUniformf("resolution", player.getPlayerWidth());
			blurShader.setUniformf("timeVal", deltaTime);
			blurShader.setUniformf("inputColour", player.getPlayerColour()
					.toGdxColour());
			sb.draw(blurFBORegion, player.position.x, player.position.y);
		} else {
			sb.setShader(colourShader);
			colourShader.setUniformf("platform", 0.0f);
			colourShader.setUniformf("inputColour", player.getPlayerColour()
					.toGdxColour());
			player.render(sb);
		}
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
		if (glowBuffer != null)
			glowBuffer.dispose();
		if (colourShader != null)
			colourShader.dispose();
		if (bgm != null)
			bgm.dispose();
		if (player != null)
			player.dispose();
		if (level != null)
			level.dispose();
	}

	private void loadShaders() {
		colourShader = new ShaderProgram(Gdx.files.internal(
				"data/lights3.glslv").readString(), Gdx.files.internal(
				"data/lights3.glslf").readString());

		if (colourShader.isCompiled() == false) {
			throw new GdxRuntimeException(
					"Failed to compile lights3.glslf/lights3.glslv:\n"
							+ colourShader.getLog());
		} else {
			Gdx.app.debug("LOAD_SHADERS", "Compiled colour shader.");

			if (colourShader.getLog().length() > 0) {
				Gdx.app.debug("LOAD_SHADERS", "Colour shader log:"
						+ colourShader.getLog());
			}
		}

		if (USE_GLOW) {
			blurShader = new ShaderProgram(Gdx.files.internal(
					"data/lights3.glslv").readString(), Gdx.files.internal(
					"data/blur.glslf").readString());
			if (!blurShader.isCompiled()) {
				throw new GdxRuntimeException(
						"Failed to compile blur shader:\n"
								+ blurShader.getLog());
			} else {
				Gdx.app.debug("LOAD_SHADERS", "Compiled blur shader");

				if (blurShader.getLog().length() > 0) {
					Gdx.app.debug("LOAD_SHADERS", "Blur shader log:"
							+ blurShader.getLog());
				}

				blurShader.begin();
				blurShader.setUniformf("resolution", player.getPlayerWidth());
				blurShader.setUniformf("radius", BLUR_RADIUS);
				blurShader.end();
			}
		}
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
