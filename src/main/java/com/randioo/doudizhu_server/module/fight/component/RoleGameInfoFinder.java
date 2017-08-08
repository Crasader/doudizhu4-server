package com.randioo.doudizhu_server.module.fight.component;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;

@Component
public class RoleGameInfoFinder {

    /**
     * 获取玩家信息
     * 
     * @param game
     * @param gameRoleId 玩家id
     * @return
     * @author wcy 2017年8月7日
     */
    public RoleGameInfo find(Game game, String gameRoleId) {
        Map<String, RoleGameInfo> roleGameInfoMap = game.getRoleIdMap();
        RoleGameInfo roleGameInfo = roleGameInfoMap.get(gameRoleId);
        return roleGameInfo;
    }

    /**
     * 获取玩家信息
     * 
     * @param game
     * @param seat 座位号
     * @return
     * @author wcy 2017年8月7日
     */
    public RoleGameInfo find(Game game, int seat) {
        List<String> roleIdList = game.getRoleIdList();
        String gameRoleId = roleIdList.get(seat);
        RoleGameInfo roleGameInfo = this.find(game, gameRoleId);

        return roleGameInfo;
    }
}
