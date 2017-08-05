package com.randioo.doudizhu_server.entity.po;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;

public abstract class GameOverTimeEvent extends DefaultTimeEvent {

	private SC sc;
    public SC getSc() {
        return sc;
    }
    public void setSc(SC sc) {
        this.sc = sc;
    }
}
