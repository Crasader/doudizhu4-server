package com.randioo.doudizhu_server.module.fight.component.dispatch;

import java.util.ArrayList;
import java.util.List;

import com.randioo.randioo_server_base.utils.RandomUtils;

/**
 * 随即发牌器
 * 
 * @author AIM
 *
 */
public class RandomDispatcher implements Dispatcher {

    @Override
    public List<CardPart> dispatch(List<Integer> totalCards, int everyPartCount, int group) {
        List<CardPart> list = new ArrayList<>();
        for (int i = 0; i < group; i++) {
            CardPart cardPart = new CardPart();
            list.add(cardPart);
            for (int j = 0; j < everyPartCount; j++) {
                int card = RandomUtils.getRandomNum(totalCards.size());
                cardPart.add(card);
            }
        }
        return list;
    }

}
