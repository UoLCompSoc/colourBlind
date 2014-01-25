package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
	private Player				player				= null;
	private Level				level				= null;
	private OrthographicCamera	camera				= null;

	public static final int		LIGHT_SIZE			= 256;
	public static final float	UPSCALE				= 1.0f;

	private FrameBuffer			occludersFBO		= null;
	private TextureRegion		occluders			= null;
	private FrameBuffer			shadowMapFBO		= null;
	private Texture				shadowMapTex		= null;
	private TextureRegion		shadowMap1D			= null;
	private ShaderProgram		shadowMapShader		= null;
	private ShaderProgram		shadowRenderShader	= null;

	private String				VERTEX_SHADER		= null;

	@Override
	public void create() {
		ShaderProgram.pedantic = false;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		camera = new OrthographicCamera();
		// camera.setToOrtho(false, Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight());

		camera.setToOrtho(false, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		player = new Player();
		level = new Level("level1.tmx");

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
		}

		shadowRenderShader = new ShaderProgram(VERTEX_SHADER, Gdx.files
				.internal("data/lights2.glslf").readString());

		if (shadowRenderShader.isCompiled() == false) {
			throw new GdxRuntimeException("Failed to compile lights2.glslf:\n"
					+ shadowRenderShader.getLog());
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 0.25f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();
		SpriteBatch sb = level.renderer.getSpriteBatch();
		sb.setShader(null);

		player.update(level, deltaTime);

		camera.position.x = player.position.x;
		camera.position.y = player.position.y;
		camera.update();

		// OCCLUDER -----------------------------------
		occludersFBO.begin();

		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.setToOrtho(false, occludersFBO.getWidth(),
				occludersFBO.getHeight());

		camera.translate(player.getX() - (LIGHT_SIZE / 2f), player.getY()
				- (LIGHT_SIZE / 2f));

		camera.update();

		sb.setProjectionMatrix(camera.combined);
		sb.setShader(null);

		level.render(camera);

		occludersFBO.end();

		// SHADOW MAP ------------------------------------
		shadowMapFBO.begin();

		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		sb.setShader(shadowMapShader);

		sb.begin();
		shadowMapShader.setUniformf("resolution", LIGHT_SIZE, LIGHT_SIZE);
		shadowMapShader.setUniformf("upScale", UPSCALE);

		camera.setToOrtho(false, shadowMapFBO.getWidth(),
				shadowMapFBO.getHeight());
		sb.setProjectionMatrix(camera.combined);

		sb.draw(occluders.getTexture(), 0, 0, LIGHT_SIZE,
				shadowMapFBO.getHeight());
		sb.end();

		shadowMapFBO.end();

		camera.setToOrtho(false, 20, 15);
		sb.setProjectionMatrix(camera.combined);

		sb.setShader(shadowRenderShader);
		sb.begin();

		shadowRenderShader.setUniformf("resolution", LIGHT_SIZE, LIGHT_SIZE);
		shadowRenderShader.setUniformf("softShadows", 1f);

		sb.setColor(1f, 0f, 0f, 0.5f);

		float FINAL_SIZE = LIGHT_SIZE * UPSCALE;

		camera.setToOrtho(false, level.WIDTH_IN_TILES, level.HEIGHT_IN_TILES);
		camera.update();

		sb.draw(shadowMap1D.getTexture(), player.getX() - FINAL_SIZE / 2f,
				player.getY() - FINAL_SIZE / 2f, FINAL_SIZE, FINAL_SIZE);

		sb.end();

		sb.setColor(Color.WHITE);
		sb.setShader(null);

		level.render(camera);

		sb.begin();
		player.render(sb);
		sb.end();
	}

	@Override
	public void dispose() {
		shadowRenderShader.dispose();
		shadowMapShader.dispose();
		occludersFBO.dispose();
		shadowMapTex.dispose();
		shadowMapFBO.dispose();
		player.dispose();
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
