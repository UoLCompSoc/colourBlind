package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class Platform {
	private CBColour	colour		= null;
	private Cell		startCell	= null;
	private int			cellWidth	= 0;

	public Platform(CBColour nColour, Cell nStartCell, int nCellWidth) {
		setColour(nColour);
		setStartCell(nStartCell);
		setCellWidth(nCellWidth);
	}

	public void render(SpriteBatch sb, OrthographicCamera camera,
			ShaderProgram shader) {
		sb.begin();
		shader.setUniformf("platformColour", colour.toGdxColour());

		sb.end();
	}

	public CBColour getColour() {
		return colour;
	}

	public void setColour(CBColour colour) {
		this.colour = colour;
	}

	public Cell getStartCell() {
		return startCell;
	}

	public void setStartCell(Cell startCell) {
		this.startCell = startCell;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}
}
