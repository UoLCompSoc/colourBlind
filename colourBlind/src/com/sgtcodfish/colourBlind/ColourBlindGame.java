package com.sgtcodfish.colourBlind;

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
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(1, h / w);
		// batch = new SpriteBatch();
		//
		// texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		// texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		//
		// TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		//
		// sprite = new Sprite(region);
		// sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		// sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		// sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);

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
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();

		player.update(level, deltaTime);
		player.render(level);

		camera.position.x = player.position.x;
		camera.position.y = player.position.y;
		camera.update();

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
