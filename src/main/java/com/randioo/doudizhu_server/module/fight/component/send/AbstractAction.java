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
 * 出牌的抽象动作
 * 
 * @author wcy 2017年8月8日
 *
 */
public abstract class AbstractAction {
    public abstract void execute(Game game, RoleGameInfo roleGameInfo, CardSort cardSort, CardList lastCardList,
            List<Integer> paiList) throws MustSendCardException, CardListPatternException, CardTypeComparableException,
            CardListTooSmallException;
}
