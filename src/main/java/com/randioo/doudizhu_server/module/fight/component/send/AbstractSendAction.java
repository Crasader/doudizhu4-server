package com.randioo.doudizhu_server.module.fight.component.send;

import java.util.List;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.cardlist.CardList;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListTooSmallException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.MustSendCardException;

/**
 * 发送牌的动作
 * 
 * @author wcy 2017年8月8日
 *
 */
public abstract class AbstractSendAction extends AbstractAction {
    public abstract void execute(Game game, RoleGameInfo roleGameInfo, CardSort cardSort, CardList lastCardList,
            List<Integer> paiList) throws MustSendCardException, CardListPatternException, CardTypeComparableException, CardListTooSmallException;

    /**
     * 获得牌的匹配类型
     * 
     * @param targetCardList
     * @param cardSort
     * @param paiList
     * @return
     * @author wcy 2017年8月8日
     */
    protected CardList patternCardList(CardList targetCardList, CardSort cardSort, List<Integer> paiList) {
        try {
            return targetCardList.pattern(cardSort, paiList);
        } catch (CardListPatternException e) {
        }
        return null;
    }

}
