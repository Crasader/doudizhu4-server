package com.randioo.doudizhu_server.module.match.component;

import java.text.MessageFormat;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.match.MatchConstant;

/**
 * 玩家游戏id生成器
 * 
 * @author wcy 2017年8月7日
 *
 */
@Component
public class RoleGameIdCreator {
    /**
     * 获得玩家id
     * 
     * @param game
     * @param roleId
     * @return
     * @author wcy 2017年8月7日
     */
    public String getRoleId(Game game, int roleId) {
        int gameId = game.getGameId();
        String gameRoleId = MessageFormat.format(MatchConstant.GAME_REAL_ROLE_ID_FORMAT, gameId, roleId);
        return gameRoleId;
    }

    public String createAIRoleId(Game game) {
        int gameId = game.getGameId();
        int aiCount = 0;
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            if (roleGameInfo.roleId == 0) {
                aiCount++;
            }
        }

        String gameRoleId = MessageFormat.format(MatchConstant.GAME_VIRTUAL_ROLE_ID_FORMAT, gameId, aiCount);
        return gameRoleId;
    }
}
