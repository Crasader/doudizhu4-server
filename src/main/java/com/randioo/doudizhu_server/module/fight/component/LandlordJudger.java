package com.randioo.doudizhu_server.module.fight.component;

import java.util.List;

import com.randioo.randioo_server_base.utils.RandomUtils;

/**
 * 地主判断器
 * 
 * @author AIM
 *
 */
public class LandlordJudger {
    /**
     * 获得地主索引
     * 
     * @param roleIdList
     * @return
     */
    public int getLandlord(List<String> roleIdList) {
        return RandomUtils.getRandomNum(roleIdList.size());
    }
}
