package com.randioo.doudizhu_server.module.fight.component;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.randioo_server_base.utils.SessionUtils;

/**
 * 游戏广播器
 * 
 * @author wcy 2017年8月7日
 *
 */
public class GameBroadcast {
    /**
     * 通知消息给游戏中的所有人
     * 
     * @param game
     * @param sc
     * @author wcy 2017年8月7日
     */
    public void notice(Game game, GeneratedMessage sc) {
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            int roleId = roleGameInfo.roleId;
            SessionUtils.sc(roleId, sc);
        }
    }

}
