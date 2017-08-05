package com.randioo.doudizhu_server.entity.po;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;

public abstract class RoundOverTimeEvent extends DefaultTimeEvent {

	private SC sc;
	private Game game;
    public SC getSc() {
        return sc;
    }
    public Game getGame() {
        return game;
    }
    public void setSc(SC sc) {
        this.sc = sc;
    }
    public void setGame(Game game) {
        this.game = game;
    }
}
