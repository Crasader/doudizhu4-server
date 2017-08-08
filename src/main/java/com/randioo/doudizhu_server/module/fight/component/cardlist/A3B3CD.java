package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.CardTools;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardTypeComparableException;

public class A3B3CD extends A3B3 {

    private List<Integer> addNumList = new ArrayList<>();

    public List<Integer> getAddNumList() {
        return addNumList;
    }

    public void setAddNumList(List<Integer> addNumList) {
        this.addNumList = addNumList;
    }

    @Override
    public int compareTo(CardList arg0) {
        if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
            return -1;

        if (getClass() != arg0.getClass())
            throw new CardTypeComparableException();

        A3B3CD a3b3cd = (A3B3CD) arg0;
        if (getLength() != a3b3cd.getLength())
            throw new CardTypeComparableException();

        return getNum() - ((A3B3CD) arg0).getNum();
    }

    @Override
    public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
        if (((arr.size() >= 8) && (arr.size() - 8) % 4 != 0))
            throw new CardListPatternException();

        Set<Integer> set2 = cardSort.get(2);
        if (set2.size() < 2)
            throw new CardListPatternException();
        cardSort = cardSort.clone();

        Set<Integer> set0 = cardSort.get(0);
        Set<Integer> set1 = cardSort.get(1);
        Set<Integer> set3 = cardSort.get(3);
        set2 = cardSort.get(2);

        int tmp = 0;
        int first = 0;
        int len = 0;
        for (int value : set2) {
            if (tmp == 0) {
                if (value > CardTools.C_K)
                    throw new CardListPatternException();
                first = value;
                tmp = value;
                len++;
            } else if (value != (tmp + 1) || value > CardTools.C_A)
                throw new CardListPatternException();
            else {
                tmp = value;
                len++;
            }
        }

        for (int n = first; n < first + len; n++) {
            cardSort.remove(n, n, n);
        }

        if (set0.size() + set3.size() + set1.size() != len) {
            throw new CardListPatternException();
        }

        A3B3CD a = new A3B3CD();
        a.setNum(first);
        a.setLength(len);
        a.getAddNumList().addAll(set0);
        a.getAddNumList().addAll(set1);

        return a;
    }

    @Override
    public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {
        if (arr.size() < 8) {
            return;
        }
        if (lastCardList == null) {
            return;
        }
        if (lastCardList.getClass() != getClass()) {
            return;
        }
        A3B3CD a1 = (A3B3CD) lastCardList;
        int num = a1.getNum();
        List<List<Integer>> lists = new ArrayList<>();
        Set<Integer> tset = null;
        CardSort tcardSort = cardSort.clone();

        Set<Integer> set = tcardSort.get(3);
        List<Integer> temp = new ArrayList<>(set);
        cardSort.remove(temp);

        set = tcardSort.get(2);
        temp = new ArrayList<>(set);
        for (int value : temp) {
            tcardSort.get(0).remove(value);
            tcardSort.get(1).remove(value);
        }
        List<Integer> extra = new ArrayList<>(tcardSort.get(0));
        extra.addAll(tcardSort.get(1));
        Collections.sort(extra);
        if (extra.size() < a1.getLength()) {
            return;
        }

        tset = tcardSort.get(2);

        for (int pai : tset) {
            if (pai > num) {
                boolean flag = true;
                for (int count = 0; count < a1.getLength(); count++) {
                    if (!tset.contains(pai + count) || pai + count > CardTools.C_A) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    List<Integer> list = new ArrayList<>();
                    for (int count = 0; count < a1.getLength(); count++) {
                        for (int k = 0; k < 3; k++)
                            list.add(pai + count);
                    }
                    for (int count = 0; count < a1.getLength(); count++) {
                        list.add(extra.get(count));
                    }
                    lists.add(list);
                }
            }
        }
        recommandList.addAll(recommandList.size(), lists);
    }

}
