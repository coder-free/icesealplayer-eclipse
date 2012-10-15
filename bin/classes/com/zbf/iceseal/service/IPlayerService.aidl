package com.zbf.iceseal.service;
oneway interface IPlayerService {
	void changeState();
	void next();
	void last();
	void seekTo(int msec);
	void changeMode();
	void setVolume(int leftVolume, int rightVolume);
	void playThis(int position, int listType, String listParamete);
	void broadcastSongChange();
	void stopSelf();
}