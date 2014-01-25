package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Colour Blind, game entry for Global Game Jam 2014 by the University of
 * Leicester computing society.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class ColourBlindGame implements ApplicationListener {
	private Player				player	= null;
	private Level				level	= null;
	private OrthographicCamera	camera	= null;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 15, 10);

		player = new Player();
		level = new Level("level1.tmx");
	}

	@Override
	public void dispose() {
		player.dispose();
		level.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();

		player.update(level, deltaTime);

		camera.position.x = player.position.x;
		camera.position.y = player.position.y;
		camera.update();

		level.render(camera);
		player.render(level);
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
