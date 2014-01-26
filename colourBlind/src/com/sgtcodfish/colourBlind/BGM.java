package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class BGM {
	Music	music	= null;

	public void create() {

		music = Gdx.audio.newMusic(Gdx.files
				.internal("data/audio/Techno_Dream.mp3"));

		music.setLooping(true);
		music.setVolume(0.5f);
	}

	public void play() {
		music.play();
	}

	public void pause() {
		music.pause();
	}

	public void dispose() {
		music.dispose();
	}

}
