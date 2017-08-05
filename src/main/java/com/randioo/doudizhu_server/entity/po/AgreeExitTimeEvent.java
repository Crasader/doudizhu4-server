package com.randioo.doudizhu_server.entity.po;

import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;

public abstract class AgreeExitTimeEvent extends DefaultTimeEvent {
	private int gameId;
	private String gameRoleId;
	private int applyExitId;

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getGameRoleId() {
		return gameRoleId;
	}

	public void setGameRoleId(String gameRoleId) {
		this.gameRoleId = gameRoleId;
	}

	public int getApplyExitId() {
		return applyExitId;
	}

	public void setApplyExitId(int applyExitId) {
		this.applyExitId = applyExitId;
	}

}
