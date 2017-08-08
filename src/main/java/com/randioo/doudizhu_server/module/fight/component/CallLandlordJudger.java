package com.randioo.doudizhu_server.module.fight.component;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.po.RoleGameInfo;

/**
 * 地主判断器
 * 
 * @author AIM
 * 
 */
@Component
public class CallLandlordJudger {
    /**
     * 获得地主索引
     * 
     * @param roleIdList
     * @return
     */
    public String getStartCallLandlordGameRoleId(Collection<RoleGameInfo> roleGameInfoList, int flagCatrd) {
        for (RoleGameInfo roleGameInfo : roleGameInfoList) {
            if (roleGameInfo.cards.contains(flagCatrd)) {
                return roleGameInfo.gameRoleId;
            }
        }
        return null;
    }
}
