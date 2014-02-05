package com.sgtcodfish.colourBlind;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
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
	public static boolean		DEBUG				= false;

	private Player				player				= null;
	private Level				level				= null;
	private OrthographicCamera	camera				= null;

	public static final int		LIGHT_SIZE			= 16;
	public static final float	UPSCALE				= 1.0f;

	public static final boolean	USE_FANCY_LIGHTS	= true;
	public static final boolean	USE_SOUND			= false;

	private FrameBuffer			occludersFBO		= null;
	private TextureRegion		occluders			= null;
	private FrameBuffer			shadowMapFBO		= null;
	private Texture				shadowMapTex		= null;
	private TextureRegion		shadowMap1D			= null;
	private ShaderProgram		shadowMapShader		= null;
	private ShaderProgram		shadowRenderShader	= null;
	private ShaderProgram		colourShader		= null;

	private String				VERTEX_SHADER		= null;

	private int					currentLevel		= 0;
	private int					levelCount			= 0;
	private ArrayList<String>	levelList			= null;

	private BGM					bgm					= null;

	private FPSLogger			fpsLogger			= null;
	private int					fpsPrintCounter		= 0;

	public ColourBlindGame() {
		this(false);
	}

	public ColourBlindGame(boolean debug) {
		ColourBlindGame.DEBUG = debug;
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
			String fname = "level" + (levelCount + 1) + ".tmx";
			String pathName = "data/maps/" + fname;
			FileHandle fh = Gdx.files.internal(pathName);

			if (fh.exists()) {
				levelList.add(fname);

				levelCount++;
			} else {
				break;
			}
		}

		if (levelList.size() == 0) {
			throw new GdxRuntimeException("Couldn't load any levels.");
		}

		Gdx.app.debug("LEVEL_COUNT", "" + levelCount + " levels loaded.");
		level = new Level(levelList.get(currentLevel));

		occludersFBO = new FrameBuffer(Format.RGBA8888, LIGHT_SIZE, LIGHT_SIZE,
				false);
		occluders = new TextureRegion(occludersFBO.getColorBufferTexture());
		occluders.flip(false, true);

		shadowMapFBO = new FrameBuffer(Format.RGBA8888, LIGHT_SIZE, 1, false);
		shadowMapTex = shadowMapFBO.getColorBufferTexture();

		shadowMapTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		shadowMapTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		shadowMap1D = new TextureRegion(shadowMapTex);
		shadowMap1D.flip(false, true);

		VERTEX_SHADER = Gdx.files.internal("data/pass.glslv").readString();

		shadowMapShader = new ShaderProgram(VERTEX_SHADER, Gdx.files.internal(
				"data/lights1.glslf").readString());

		if (shadowMapShader.isCompiled() == false) {
			throw new GdxRuntimeException("Failed to compile lights1.glslf:\n"
					+ shadowMapShader.getLog());
		} else {
			Gdx.app.debug("LOAD_SHADERS", "Compiled shadow map shader.");
		}

		shadowRenderShader = new ShaderProgram(VERTEX_SHADER, Gdx.files
				.internal("data/lights2.glslf").readString());

		if (shadowRenderShader.isCompiled() == false) {
			throw new GdxRuntimeException("Failed to compile lights2.glslf:\n"
					+ shadowRenderShader.getLog());
		} else {
			Gdx.app.debug("LOAD_SHADERS", "Compiled shadow render shader.");
		}

		colourShader = new ShaderProgram(Gdx.files.internal(
				"data/lights3.glslv").readString(), Gdx.files.internal(
				"data/lights3.glslf").readString());

		if (colourShader.isCompiled() == false) {
			throw new GdxRuntimeException("Failed to compile lights3.glslf:\n"
					+ colourShader.getLog());
		} else {
			Gdx.app.debug("LOAD_SHADERS", "Compiled colour shader.");
		}

		if (USE_SOUND) {
			bgm = new BGM();
			bgm.create();
			Gdx.app.debug("LOAD_SOUND", "Loaded sounds correctly, playing.");
			bgm.play();
		} else {
			Gdx.app.debug("LOAD_SOUND", "Sounds disabled.");
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 0.25f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();
		SpriteBatch sb = level.renderer.getSpriteBatch();
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

		if (player.update(level, deltaTime)) {
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

		if (player.isLightOn()) {
			if (USE_FANCY_LIGHTS) {
				// OCCLUDER -----------------------------------
				occludersFBO.begin();

				sb.setShader(shadowMapShader);
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

				camera.setToOrtho(false, occludersFBO.getWidth(),
						occludersFBO.getHeight());

				camera.translate(player.getX() - (LIGHT_SIZE / 2f),
						player.getY() - (LIGHT_SIZE / 2f));

				camera.update();

				sb.setProjectionMatrix(camera.combined);

				level.renderAll(camera);

				occludersFBO.end();

				// SHADOW MAP ------------------------------------
				shadowMapFBO.begin();

				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

				sb.begin();
				shadowMapShader.setUniformf("resolution", LIGHT_SIZE,
						LIGHT_SIZE);
				shadowMapShader.setUniformf("upScale", UPSCALE);

				camera.setToOrtho(false, shadowMapFBO.getWidth(),
						shadowMapFBO.getHeight());
				sb.setProjectionMatrix(camera.combined);

				sb.draw(occluders.getTexture(), 0, 0, LIGHT_SIZE,
						shadowMapFBO.getHeight());
				sb.end();

				shadowMapFBO.end();

				camera.setToOrtho(false, level.WIDTH_IN_TILES,
						level.HEIGHT_IN_TILES);
				camera.position.x = player.position.x;
				camera.position.y = player.position.y;
				camera.update();
				sb.setProjectionMatrix(camera.combined);

				sb.setShader(shadowRenderShader);
				sb.begin();

				shadowRenderShader.setUniformf("resolution", LIGHT_SIZE,
						LIGHT_SIZE);
				shadowRenderShader.setUniformf("softShadows", 1f);

				final float GREY_BRIGHTNESS = 0.8f;
				sb.setColor(GREY_BRIGHTNESS, GREY_BRIGHTNESS, GREY_BRIGHTNESS,
						0.8f);

				float FINAL_SIZE = LIGHT_SIZE * UPSCALE;

				sb.draw(shadowMap1D.getTexture(),
						player.getX() + (player.getPlayerWidth() / 2.0f)
								- (LIGHT_SIZE / 2.0f), player.getY()
								- (LIGHT_SIZE / 2.0f), FINAL_SIZE, FINAL_SIZE);

				sb.end();
			}
		}

		// REGULAR DRAWING ----------
		// sb.setColor(Color.WHITE);

		camera.setToOrtho(false, level.WIDTH_IN_TILES, level.HEIGHT_IN_TILES);
		camera.position.x = player.position.x;
		camera.position.y = player.position.y;
		camera.update();
		sb.setProjectionMatrix(camera.combined);
		sb.setShader(null);
		sb.begin();
		level.renderLevel(camera);
		sb.end();

		sb.begin();
		sb.setShader(colourShader);
		colourShader.setUniformf("flashLightSize", (float) LIGHT_SIZE / 2);
		colourShader.setUniformf("flashLight", (player.isLightOn() ? 1.0f
				: 0.0f));
		colourShader.setUniformf("platform", 1.0f);
		colourShader.setUniformf("lightCoord", player.position.x,
				player.position.y);
		Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
		colourShader.setUniformi("u_colourTex", 3);

		level.renderPlatforms(camera);

		sb.end();

		sb.begin();

		colourShader.setUniformf("platform", 0.0f);
		colourShader.setUniformf("inputColour", player.getPlayerColour()
				.toGdxColour());
		player.render(sb);

		sb.end();

		sb.setShader(null);
		sb.begin();
		level.renderDoor(camera);

		sb.end();
	}

	// returns true to exit
	public boolean nextLevel() {
		currentLevel++;
		if (currentLevel >= levelCount) {
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
		if (shadowRenderShader != null)
			shadowRenderShader.dispose();
		if (shadowMapShader != null)
			shadowMapShader.dispose();
		if (occludersFBO != null)
			occludersFBO.dispose();
		if (shadowMapTex != null)
			shadowMapTex.dispose();
		if (shadowMapFBO != null)
			shadowMapFBO.dispose();
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
}
