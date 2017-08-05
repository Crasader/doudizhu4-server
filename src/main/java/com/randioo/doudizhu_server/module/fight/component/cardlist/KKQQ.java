package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.component.CardTools;


public class KKQQ extends A1{
	private int length;

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 4)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(0);
		Set<Integer> set2 = cardSort.getCardSort().get(1);
		if (set.size() == 2 && set.contains(14) && set.contains(15)&&set2.size() == 2 && set2.contains(14) && set2.contains(15))
			return new KKQQ(); 
		throw new CardListPatternException();
	}

	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A8.class)
			return -1;
		
		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();
		
		KKQQ kqkq = (KKQQ) arg0;
		
		if (getLength() != kqkq.getLength())
			throw new CardTypeComparableException();
		
		return getNum() - ((KKQQ) arg0).getNum();
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		if (arr.size() < 2 || cardSort.getCardSort().get(0).size() < 2)
			return;
		
		cardSort = cardSort.clone();
		List<List<Integer>> lists = new ArrayList<>();
		Set<Integer> set = cardSort.getCardSort().get(0);
		Set<Integer> set2 = cardSort.getCardSort().get(1);
		if (set.size() == 2 && set.contains(14) && set.contains(15)&&set2.size() == 2 && set2.contains(14) && set2.contains(15)) {
			List<Integer> list = new ArrayList<>(4);
			list.add(0xE);
			list.add(0xF);
			list.add(0xE);
			list.add(0xF);
			lists.add(list);
		}
		recommandList.addAll(recommandList.size(), lists);	

	}
	public static void main(String[] args) {
		KKQQ a1 = new KKQQ();
		List<List<Integer>> recommandList = new ArrayList<>();
		List<Integer> list = new ArrayList<>();
		list.add(0x0F);
		list.add(0x0F);
		list.add(0x0E);
		list.add(0x0E);

		CardSort cardSort = new CardSort();
		CardTools.fillCardSort(cardSort, list);
		KKQQ lastCardList = new KKQQ();
		lastCardList.setNum(3);
		a1.recommand(recommandList, cardSort, lastCardList, list);
		System.out.println(recommandList);
	}
}
