package com.randioo.doudizhu_server.module.fight.component;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.entity.bo.Game;

/**
 * 游戏查找器
 * 
 * @author wcy 2017年8月7日
 *
 */
@Component
public class GameFinder {
    /**
     * 通过游戏id查找
     * 
     * @param gameId
     * @return
     * @author wcy 2017年8月7日
     */
    public Game find(int gameId) {
        return GameCache.getGameMap().get(gameId);
    }

    /**
     * 通过游戏房间号查找
     * 
     * @param roomId
     * @return
     * @author wcy 2017年8月7日
     */
    public Game find(String roomId) {
        Integer gameId = GameCache.getGameLockStringMap().get(roomId);
        if (gameId == null)
            return null;
        return find(gameId);
    }
}
