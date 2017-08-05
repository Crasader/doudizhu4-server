package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.component.CardTools;

public class A8 extends A1{

	private int length;

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}
	@Override
	public int compareTo(CardList arg0) {
		return 1;
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() < 8)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(7);
		if (set.size() == 0)
			throw new CardListPatternException();
		int num = set.iterator().next();
		A8 a = new A8();
		a.setNum(num);
		return a;

	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,List<Integer> arr) {
		if (arr.size() < 8 || cardSort.getCardSort().get(7).size() < 1)
			return;
		cardSort = cardSort.clone();
		if (lastCardList == null) {
			// 主动出牌
			List<List<Integer>> lists = new ArrayList<>();
			Set<Integer> set = cardSort.getCardSort().get(7);				
			for(int pai : set){
				List<Integer> list = new ArrayList<>(7);
				for (int k = 0; k < 7; k++)
					list.add(pai);
				lists.add(list);
			}
			recommandList.addAll(recommandList.size(), lists);		
		} else {
			// 被动出牌
				boolean bomb = (lastCardList.getClass() == A8.class);
				List<List<Integer>> lists = new ArrayList<>();
				Set<Integer> set = cardSort.getCardSort().get(7);				
				for(int pai : set){
					if(!( bomb && ((A8)lastCardList).getNum() > pai)){
						List<Integer> list = new ArrayList<>(7);
						for (int k = 0; k < 8; k++)
							list.add(pai);
						lists.add(list);
					}					
				}
				recommandList.addAll(recommandList.size(), lists);	
		}
	}
	public static void main(String[] args) {
		A8 a8 = new A8();
		
		List<List<Integer>> recommandList = new ArrayList<>();
		List<Integer> list = new ArrayList<>();
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x1B);
		list.add(0x1B);
		list.add(0x1B);
		list.add(0x1B);
		list.add(0x1B);
		list.add(0x1B);
		list.add(0x1B);
		list.add(0x1B);
		
		
		CardSort cardSort = new CardSort();
		CardTools.fillCardSort(cardSort, list);

		A8 lastCardList = new A8();
		lastCardList.setNum(1);
		a8.recommand(recommandList, cardSort, lastCardList, list);

		System.out.println(recommandList);
	}
}
