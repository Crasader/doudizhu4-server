package com.randioo.doudizhu_server.module.fight.component;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SeatCursor {

    public String getNextGameRoleId(List<String> roleIdList, String gameRoleId) {
        int index = roleIdList.indexOf(gameRoleId);
        int size = roleIdList.size();
        int nextIndex = (index + 1) % size;
        String nextGameRoleId = roleIdList.get(nextIndex);
        return nextGameRoleId;
    }
}
