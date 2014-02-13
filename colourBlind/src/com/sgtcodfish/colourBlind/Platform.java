package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class Platform {
	private CBColour	colour			= null;
	private int			widthInCells	= 0;
	private Vector2		startPos		= null;

	public Platform(CBColour nColour, Vector2 nStartPos, int nCellWidth) {
		setColour(nColour);
		setStartPosition(nStartPos);
		setWidthInCells(nCellWidth);
	}

	public void render(Level level, SpriteBatch sb, OrthographicCamera camera,
			ShaderProgram shader) {
		shader.setUniformf("platformColour", colour.toGdxColour());
		sb.begin();

		for (int i = 0; i < widthInCells; i++) {
			TextureRegion tr = null;
			if (i == 0) {
				// start
				tr = level.getPlatformStartTexture();
			} else if (i == widthInCells - 1) {
				// end
				tr = level.getPlatformEndTexture();
			} else {
				// middle
				tr = level.getPlatformMiddleTexture();
			}

			sb.draw(tr, startPos.x * i, startPos.y, 0.0f, 0.0f,
					tr.getRegionWidth(), tr.getRegionHeight(),
					1.0f / level.WIDTH_IN_TILES, 1.0f / level.HEIGHT_IN_TILES,
					0.0f);
		}

		sb.end();
	}

	public CBColour getColour() {
		return colour;
	}

	public void setColour(CBColour colour) {
		this.colour = colour;
	}

	public Vector2 getStartPosition() {
		return startPos;
	}

	public void setStartPosition(Vector2 nv) {
		startPos = nv.cpy();
	}

	public void setStartPosition(float x, float y) {
		if (startPos == null) {
			startPos = new Vector2(x, y);
		} else {
			startPos.x = x;
			startPos.y = y;
		}
	}

	public int getWidthInCells() {
		return widthInCells;
	}

	public void setWidthInCells(int cellWidth) {
		this.widthInCells = cellWidth;
	}
}
