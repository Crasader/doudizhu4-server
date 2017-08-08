package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.CardTools;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardTypeComparableException;

public class ABCDE extends A1 {
	private int length;

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	@Override
	public int compareTo(CardList o) {
		if (o.getClass() == A4.class || o.getClass() == KQ.class)
			return -1;
		if (o.getClass() != getClass())
			throw new CardTypeComparableException();
		ABCDE abcde = (ABCDE) o;
		if (length != abcde.length)
			throw new CardTypeComparableException();

		return this.getNum() - abcde.getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() < 5)
			throw new CardListPatternException();
		Set<Integer> set1 = cardSort.get(1);
		Set<Integer> set0 = cardSort.get(0);
		if (set1.size() > 0 || (set1.iterator().hasNext() && set1.iterator().next() > CardTools.C_10))
			throw new CardListPatternException();

		int tmp = 0;
		int first = 0;
		for (int value : set0) {
			if (tmp == 0) {
				first = value;
				tmp = value;
			} else if (value != (tmp + 1) || value > CardTools.C_A)
				throw new CardListPatternException();
			else
				tmp = value;
		}

		ABCDE abcde = new ABCDE();
		abcde.setNum(first);
		abcde.setLength(set0.size());

		return abcde;
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {
		if (arr.size() < 5 || cardSort.get(0).size() < 5)
			return;

		if (lastCardList != null) {
			CardTools.recommandStartNumAndLenCommonTemplate(recommandList, cardSort, lastCardList, arr, 0, getClass());
		}
	}
}
