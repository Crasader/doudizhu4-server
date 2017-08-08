package com.randioo.doudizhu_server.module.fight.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {

    /**
     * 判断当前卡组是否能报道
     * 
     * @param cardList
     * @return
     */
    public Map<Integer, Integer> canReport(List<Integer> cardList) {
        Map<Integer, Integer> cardMap = new HashMap<>();

        for (int i : cardList) {
            i = CardTools.toNum(i);
            if (!cardMap.keySet().contains(i)) {
                cardMap.put(i, 0);
            }
            cardMap.put(i, cardMap.get(i) + 1);
        }

        Map<Integer, Integer> resultMap = new HashMap<>();
        // 现判定4个王的情况
        if (cardMap.get(CardTools.C_QUEUE) != null && cardMap.get(CardTools.C_KING) != null) {
            if (cardMap.get(CardTools.C_QUEUE) + cardMap.get(CardTools.C_KING) == 4) {
                resultMap.put(CardTools.C_QUEUE, 4);
            }
        }
        for (int key : cardMap.keySet()) {
            if (cardMap.get(key) == 7 || cardMap.get(key) == 8) {
                resultMap.put(key, cardMap.get(key));
            }
        }
        // 能报道的结果集
        return resultMap;
    }

    /**
     * 报道
     * 
     * cadrdList: 地主手中的牌 ; canReporCardtList:能报道牌的集合; reportCardList:报道的牌
     */
    public void report(List<Integer> canReporCardtList, List<Integer> reportCardList, List<Integer> cardList) {
        List<List<Integer>> resultList = reportCard(canReporCardtList, cardList);
        for (List<Integer> list : resultList) {
            reportCardList.addAll(list);
        }
        // 从手中牌中 移除报道的牌
        cardList.removeAll(reportCardList);
    }

    public boolean isCardSEqual(int cardOne, int cardTwo) {
        return CardTools.toNum(cardOne) == CardTools.toNum(cardTwo);
    }

    /**
     * 报道
     * 
     * cadrdList:地主手中的牌
     * 
     * @param cadrdList
     */
    public List<List<Integer>> reportCard(List<Integer> reportList, List<Integer> cardList) {

        List<List<Integer>> resultList = new ArrayList<>();
        for (int card : reportList) {
            List<Integer> oneCardList = new ArrayList<>();
            // 移除7 ， 8个或 王
            for (int i : cardList) {
                if (isCardSEqual(i, card)) {
                    oneCardList.add(i);
                }
            }
            // 是小王的话 移除大王
            if (card == CardTools.C_QUEUE) {
                oneCardList.add(CardTools.C_KING);
                oneCardList.add(CardTools.C_KING);
            }
            resultList.add(oneCardList);
        }
        // 从手中牌 满足报道的集合
        return resultList;
    }

}
