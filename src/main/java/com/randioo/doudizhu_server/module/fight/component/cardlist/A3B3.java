package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.CardTools;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardTypeComparableException;

public class A3B3 extends ABCDE {
    @Override
    public int compareTo(CardList arg0) {
        if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
            return -1;

        if (getClass() != arg0.getClass())
            throw new CardTypeComparableException();

        A3B3 a3b3 = (A3B3) arg0;
        if (getLength() != a3b3.getLength())
            throw new CardTypeComparableException();

        return getNum() - ((A3B3) arg0).getNum();
    }

    @Override
    public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
        if (arr.size() < 6 && (arr.size() - 6) % 3 == 0)
            throw new CardListPatternException();
        Set<Integer> set0 = cardSort.get(0);
        Set<Integer> set1 = cardSort.get(1);
        Set<Integer> set2 = cardSort.get(2);
        Set<Integer> set3 = cardSort.get(3);
        if (set0.size() != set1.size() || set0.size() != set2.size() || set0.contains(13) || set0.contains(14)
                || set0.contains(15) || set3.size() != 0)
            throw new CardListPatternException();

        int tmp = 0;
        int first = 0;
        for (int value : set2) {
            if (tmp == 0) {
                first = value;
                tmp = value;
            } else if (value != (tmp + 1))
                throw new CardListPatternException();
            else
                tmp = value;
        }
        A3B3 a = new A3B3();
        a.setNum(first);
        a.setLength(set2.size());

        return a;
    }

    @Override
    public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {

        if (arr.size() < 6 || cardSort.get(2).size() < 2)
            return;

        if (lastCardList != null) {
            CardTools.recommandStartNumAndLenCommonTemplate(recommandList, cardSort, lastCardList, arr, 2, getClass());
        }
    }
}
