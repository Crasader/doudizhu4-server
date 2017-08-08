package com.randioo.doudizhu_server.module.fight.component.send;

import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.cardlist.CardList;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.MustSendCardException;

/**
 * 主动出牌
 * 
 * @author wcy 2017年8月8日
 *
 */
@Component
public class InitiativeSendAction extends AbstractSendAction {

    @Override
    public void execute(Game game, RoleGameInfo roleGameInfo, CardSort cardSort, CardList lastCardList,
            List<Integer> paiList) throws MustSendCardException, CardListPatternException {
        // 获得匹配到的牌型
        CardList cardList = this.getCardList(cardSort, paiList);
        // 没有匹配到就报出异常
        if (cardList == null) {
            throw new CardListPatternException();
        }

        // 设置上一轮出牌
        game.setLastCardList(cardList);
        // 过的计数器重置
        game.setPassCount(0);

        List<Integer> cards = roleGameInfo.cards;
        // 从手牌中移除该牌
        for (int pai : paiList) {
            int index = cards.indexOf(pai);
            cards.remove(index);
        }
    }

    /**
     * 寻找可以匹配的牌型
     * 
     * @param cardSort
     * @param paiList
     * @return
     * @author wcy 2017年8月8日
     */
    private CardList getCardList(CardSort cardSort, List<Integer> paiList) {
        for (CardList cardList : GameCache.getCardLists().values()) {
            CardList sendCardList = this.patternCardList(cardList, cardSort, paiList);
            if (sendCardList != null)
                return sendCardList;
        }
        return null;
    }
}
