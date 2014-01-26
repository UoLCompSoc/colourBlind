package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class BGM implements Audio {

	Music	music;

	public void create() {

		music = Gdx.audio.newMusic(Gdx.files.internal("src/com/sgtcodfish/colourBlind/Techno_Dream.mp3"));

		music.setLooping(true);
		music.setVolume(0.5f);
		music.play();
	}

	public void dispose() {
		music.dispose();
	}

	@Override
	public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sound newSound(FileHandle fileHandle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Music newMusic(FileHandle file) {
		// TODO Auto-generated method stub
		return null;
	}

}
