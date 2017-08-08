package com.randioo.doudizhu_server.module.fight.component;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.po.RoleGameInfo;

/**
 * 游戏开始检查
 * 
 * @author wcy 2017年8月7日
 *
 */
@Component
public class RoleCountChecker {
    /**
     * 检查是否所有人都点了准备
     * 
     * @param map
     * @param maxCount
     * @return
     * @author wcy 2017年8月7日
     */
    public boolean checkAllReady(Map<String, RoleGameInfo> map, int maxCount) {
        if (map.size() < maxCount)
            return false;

        for (RoleGameInfo info : map.values()) {
            if (!info.ready)
                return false;
        }
        return true;
    }
}
