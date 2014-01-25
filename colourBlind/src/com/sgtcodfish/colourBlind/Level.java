package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Level {
	public OrthogonalTiledMapRenderer renderer = null;
	private TiledMap tiledMap = null;
	
	/**
	 * Creates a new level, loaded the tmx file called "levelFileName"
	 * in the "data/maps" directory.
	 * @param levelFileName The file name of the level to load.
	 */
	public Level(String levelFileName) {
		String fullFileName = "data/maps/" + levelFileName;
		FileHandle levelHandle = Gdx.files.internal(fullFileName);
		
		Gdx.app.debug("LEVEL_LOAD", "Level file \"" + levelFileName +
				"\" exists: " + levelHandle.exists());
		tiledMap = new TmxMapLoader().load(fullFileName);
		renderer = new OrthogonalTiledMapRenderer(tiledMap);
	}
	
	public void render(OrthographicCamera camera) {
		renderer.setView(camera);
		renderer.render();
	}
	
	public void dispose() {
		
	}
}
