package com.randioo.doudizhu_server.module.fight.component;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.FightConstant;
import com.randioo.randioo_server_base.utils.RandomUtils;

@Component
public class GameInit {
    public void init(Game game) {
        // 倍数清空
        game.setMultiple(1);
        //底分清空
        game.setScore(0);
        // 重置地主
        game.setLandlordGameRoleId(null);
        // 重置叫地主的次数
        game.setCallLandlordCount(0);
        // 重置明牌状态
        game.setMingPaiState(false);
        // FIXME 最后查一下手牌数量就知道是否出过牌了,和下面的地主春天都可以取消标记
        // 设置农民春天
        game.setFarmerSpring(true);
        // 设置地主春天
        game.setLandLordSpring(true);
        // 重置炸弹
        game.setBomb(0);
        // 重置摸到王
        game.setHuangFan(false);
        // 重置上一轮卡组
        game.setLastCardList(null);
        // 玩家卡牌清空
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            info.cards.clear();
        }
        int cardIndex = RandomUtils.getRandomNum(FightConstant.CARDS.length);
        int flagCard = FightConstant.CARDS[cardIndex]; // 选出的明牌
        // 设置明牌
        game.setFlagCard(flagCard);
    }
}
