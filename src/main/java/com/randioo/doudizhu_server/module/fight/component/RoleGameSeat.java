package com.randioo.doudizhu_server.module.fight.component;

import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;

/**
 * 玩家座位
 * 
 * @author wcy 2017年8月7日
 *
 */
@Component
public class RoleGameSeat {
    /**
     * 获得座位
     * 
     * @param game
     * @param gameRoleId
     * @return
     * @author wcy 2017年8月7日
     */
    public int getSeat(Game game, String gameRoleId) {
        List<String> roleIdList = game.getRoleIdList();
        int seat = roleIdList.indexOf(gameRoleId);
        return seat;
    }

    /**
     * 获得座位
     * 
     * @param game
     * @param roleGameInfo
     * @return
     * @author wcy 2017年8月7日
     */
    public int getSeat(Game game, RoleGameInfo roleGameInfo) {
        String gameRoleId = roleGameInfo.gameRoleId;
        int seat = this.getSeat(game, gameRoleId);
        return seat;
    }
}
