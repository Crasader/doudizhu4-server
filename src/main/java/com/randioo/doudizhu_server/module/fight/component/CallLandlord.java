package com.randioo.doudizhu_server.module.fight.component;

import org.springframework.stereotype.Component;

import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.module.fight.FightConstant;

@Component
public class CallLandlord {

    /**
     * 叫档
     * 
     * @param game
     * @param gameRoleId
     * @param callScore
     * @return
     * @author wcy 2017年8月7日
     */
    public int callLandlord(Game game, String gameRoleId, int callScore) {
        // 检查是否是该人叫分
        int index = game.getRoleIdList().indexOf(gameRoleId);
        int currentScore = game.getScore();
        // 安全检查
        if (this.safeCheck(game, index, currentScore)) {
            return FightConstant.CALL_LANDLORD_ERROR;
        }
        if (currentScore > callScore && callScore != 0) {
            return FightConstant.CALL_LANDLORD_ERROR;
        }
        // 如果叫了三分就是地主,加牌开始比赛
        if (callScore == FightConstant.SCORE_3) {
            game.setMultiple(callScore);
            game.setLandlordGameRoleId(gameRoleId);
            if (game.getCallLandlordCount() == 0) {
                game.setFirst(true);
            }
            game.getRoleIdMap().get(gameRoleId).canUseBombCount = 3;
            return FightConstant.CALL_LANDLORD_OVER; // 叫挡完成
        }
        if (callScore > currentScore) {
            currentScore = callScore;
            game.setMultiple(callScore);
            game.setLandlordGameRoleId(gameRoleId);
            game.getRoleIdMap().get(gameRoleId).canUseBombCount = callScore;
        }
        int callCount = game.getCallLandlordCount();
        callCount++;
        game.setCallLandlordCount(callCount);
        // 荒番和强迫地主
        if (callCount >= game.getMaxRoleCount() && currentScore == 0) {
            if (game.getGameConfig().getHuangFang()) {
                game.setHuangFanCount(game.getHuangFanCount() + 1);
                return FightConstant.DISPATCH_CARD_AGAIN; // 重新发牌
            }
            game.setHasCallScoreRound(game.getHasCallScoreRound() + 1);
            if (game.getHasCallScoreRound() >= 4) {
                game.setLandlordGameRoleId(game.getStartGameRoleId());// 强迫地主
                game.setMultiple(1);
                return FightConstant.CALL_LANDLORD_OVER; // 叫挡完成
            } else {
                return FightConstant.DISPATCH_CARD_AGAIN; // 重新发牌
            }
        }

        if (callScore == 0) {
            game.getRoleIdMap().get(gameRoleId).canUseBombCount = 1;
            return FightConstant.NOT_CALL_SCORE; // 不叫
        }

        // 检查是否每个人都叫过了
        if (callCount >= game.getMaxRoleCount()) {
            game.setFirst(false);
            return FightConstant.CALL_LANDLORD_OVER; // 叫挡完成
        } else {
            // 如果人数没有到,则通知下一个人
            return FightConstant.NEXT_CALL_SCORE; // 下个人
        }
    }

    /**
     * 安全检查
     * 
     * @param game
     * @param index
     * @param currentScore
     * @return
     * @author wcy 2017年8月7日
     */
    private boolean safeCheck(Game game, int index, int currentScore) {
        return index != game.getCurrentRoleIdIndex() || game.getCallLandlordCount() == game.getMaxRoleCount()
                || currentScore == FightConstant.SCORE_3;
    }
}
