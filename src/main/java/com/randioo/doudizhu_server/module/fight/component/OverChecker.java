package com.randioo.doudizhu_server.module.fight.component;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.bo.Game;

/**
 * 结束检查
 * 
 * @author wcy 2017年8月8日
 *
 */
@Component
public class OverChecker {
    public boolean isOver(Game game) {
        return false;
    }
}
