package com.randioo.doudizhu_server.entity.po;

import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;

//解散房间
public abstract class CancelRoomTimeEvent extends DefaultTimeEvent {

	private int gameId;

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

}
