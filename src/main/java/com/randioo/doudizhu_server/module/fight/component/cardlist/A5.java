package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.component.CardTools;

public class A5 extends A1 {

	private int length;

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}
	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A8.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();
		A5 a5 = (A5) arg0;
		if (getLength() != a5.getLength())
			throw new CardTypeComparableException();
		
		return getNum() - ((A5) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() < 5)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(4);
		if (set.size() == 0)
			throw new CardListPatternException();
		int num = set.iterator().next();
		A5 a = new A5();
		a.setNum(num);
		return a;

	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,List<Integer> arr) {
		if (arr.size() < 5 || cardSort.getCardSort().get(4).size() < 1)
			return;
		cardSort = cardSort.clone();
		if (lastCardList == null) {
			// 主动出牌
			List<List<Integer>> lists = new ArrayList<>();
			Set<Integer> set = cardSort.getCardSort().get(4);				
			for(int pai : set){
				List<Integer> list = new ArrayList<>(5);
				for (int k = 0; k < 5; k++)
					list.add(pai);
				lists.add(list);
			}
			recommandList.addAll(recommandList.size(), lists);
			
			
		} else {
			// 被动出牌
			if (lastCardList.getClass() == A8.class) {
				return;
			}else{
				boolean bomb = (lastCardList.getClass() == A5.class);
				List<List<Integer>> lists = new ArrayList<>();
				Set<Integer> set = cardSort.getCardSort().get(3);				
				for(int pai : set){
					if(!( bomb && ((A5)lastCardList).getNum() > pai)){
						List<Integer> list = new ArrayList<>(5);
						for (int k = 0; k < 5; k++)
							list.add(pai);
						lists.add(list);
					}					
				}
				recommandList.addAll(recommandList.size(), lists);
			}
			
		}

	}
	public static void main(String[] args) {
		A5 a5 = new A5();
		List<List<Integer>> recommandList = new ArrayList<>();
		List<Integer> list = new ArrayList<>();
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x1B);
		
		CardSort cardSort = new CardSort();
		CardTools.fillCardSort(cardSort, list);

		A5 lastCardList = new A5();
		lastCardList.setNum(1);
		a5.recommand(recommandList, cardSort, lastCardList, list);

		System.out.println(recommandList);
	}
}
