package com.randioo.doudizhu_server.module.fight.component.send;

import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.cardlist.CardList;
import com.randioo.doudizhu_server.module.fight.component.send.exception.MustSendCardException;

/**
 * 过牌动作
 * 
 * @author wcy 2017年8月8日
 *
 */
@Component
public class GuoAction extends AbstractAction {

    /**
     * 必须出牌检查
     * 
     * @param passCount
     * @param maxRoleCount
     * @return
     * @author wcy 2017年8月8日
     * @throws MustSendCardException
     */
    private void checkMustSendCard(int passCount, int maxRoleCount) throws MustSendCardException {
        if (passCount >= maxRoleCount - 1) {
            throw new MustSendCardException();
        }
    }

    @Override
    public void execute(Game game, RoleGameInfo roleGameInfo, CardSort cardSort, CardList lastCardList,
            List<Integer> paiList) throws MustSendCardException {
        this.checkMustSendCard(game.getPassCount(), game.getMaxRoleCount());
        // 过的次数+1
        game.setPassCount(game.getPassCount() + 1);

        // 轮了一圈都不叫,则把比较使用的牌清空
        if (game.getPassCount() >= game.getMaxRoleCount() - 1) {
            game.setLastCardList(null);
        }
    }

}
