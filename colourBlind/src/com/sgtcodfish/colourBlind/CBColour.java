package com.sgtcodfish.colourBlind;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Holds a single colour which can be the colour of the flashlight or the
 * player, or a platform. The design is immutable.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class CBColour {
	public enum GameColour {
		RED, GREEN, BLUE, YELLOW, BLACK;

		public static String asString(GameColour gc) {
			switch (gc) {
			case RED:
				return "RED";
			case BLACK:
				return "BLACK";
			case BLUE:
				return "BLUE";
			case GREEN:
				return "GREEN";
			case YELLOW:
				return "YELLOW";
			default:
				return "Whattheflyingfuck?";
			}
		}
	}

	private GameColour		col		= null;

	private static Random	random	= new Random();

	/**
	 * Creates a random CBColour in RED, GREEN, BLUE or YELLOW.
	 */
	public CBColour() {
		int randCol = random.nextInt(4);
		GameColour ncol = null;

		switch (randCol) {
		case 0:
			ncol = GameColour.RED;
			break;

		case 1:
			ncol = GameColour.BLUE;
			break;

		case 2:
			ncol = GameColour.GREEN;
			break;

		case 3:
			ncol = GameColour.YELLOW;
			break;
		}

		if (ncol == null) {
			throw new GdxRuntimeException(
					"Fatal error creating random CBColour.");
		} else {
			this.col = ncol;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other.getClass() == CBColour.class) {
			if (((CBColour) other).getColour() == col) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Creates a CBColour with the specified GameColour
	 * 
	 * @param col
	 *            The colour to use.
	 */
	public CBColour(GameColour col) {
		this.col = col;
	}

	/**
	 * @param a
	 *            The alpha value of the colour returned.
	 * @return A Gdx Color associated with this GameColour, with an alpha value
	 *         of a.
	 */
	public Color toGdxColour(float a) {
		switch (col) {
		case RED:
			return new Color(1.0f, 0.0f, 0.0f, a);

		case BLUE:
			return new Color(0.0f, 0.0f, 1.0f, a);

		case GREEN:
			return new Color(0.0f, 0.8f, 0.0f, a);

		case YELLOW:
			return new Color(1.0f, 1.0f, 0.0f, a);

		case BLACK:
			return new Color(0.0f, 0.0f, 0.0f, a);

		default:
			return null;
		}
	}

	/**
	 * @return Returns the Gdx Color associated with this GameColour with an
	 *         alpha value of 1.0f.
	 */
	public Color toGdxColour() {
		return toGdxColour(1.0f);
	}

	public GameColour getColour() {
		return col;
	}
}
