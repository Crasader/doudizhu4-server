package com.randioo.doudizhu_server.module.fight.component.send;

import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.cardlist.CardList;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListTooSmallException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.MustSendCardException;

/**
 * 被动出牌
 * 
 * @author wcy 2017年8月8日
 *
 */
@Component
public class PassiveSendAction extends AbstractSendAction {

    @Override
    public void execute(Game game, RoleGameInfo roleGameInfo, CardSort cardSort, CardList lastCardList,
            List<Integer> paiList) throws MustSendCardException, CardListPatternException, CardTypeComparableException,
            CardListTooSmallException {
        Class<? extends CardList> cardListClass = lastCardList.getClass();
        // 获得别人出的牌组的牌型
        CardList cardList = GameCache.getCardLists().get(cardListClass);
        // 检查要出的牌是否符合这个牌型
        CardList sendCardList = this.patternCardList(cardList, cardSort, paiList);

        // 没有匹配到牌型，则检查是否是特殊牌型
        if (sendCardList == null) {
            // 没有匹配成功，再查炸弹
            for (CardList checkCardList : GameCache.getSendCardSeqCheckerList()) {
                sendCardList = this.patternCardList(checkCardList, cardSort, paiList);
                if (sendCardList != null)
                    break;
            }
        }

        if (sendCardList == null) {
            throw new CardListPatternException();
        }

        try {
            boolean bigger = this.bigger(sendCardList, lastCardList);
            if (!bigger) {
                throw new CardListTooSmallException();
            }
        } catch (CardTypeComparableException e) {
            throw e;
        }

        // 设置本次出牌为上一轮出牌
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
     * 比较大小
     * 
     * @param sendCardList
     * @param lastCardList
     * @return
     * @author wcy 2017年8月8日
     */
    private boolean bigger(CardList sendCardList, CardList lastCardList) {
        int compare = sendCardList.compareTo(lastCardList);
        if (compare <= 0)
            return false;

        return true;
    }

}
