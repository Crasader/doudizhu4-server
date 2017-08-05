package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.component.CardTools;

public class A6 extends A1{

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
		A6 a6 = (A6) arg0;
		if (getLength() != a6.getLength())
			throw new CardTypeComparableException();
		
		return getNum() - ((A6) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() < 6)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(5);
		if (set.size() == 0)
			throw new CardListPatternException();
		int num = set.iterator().next();
		A6 a = new A6();
		a.setNum(num);
		return a;

	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,List<Integer> arr) {
		if (arr.size() < 6 || cardSort.getCardSort().get(5).size() < 1)
			return;
		cardSort = cardSort.clone();
		if (lastCardList == null) {
			// 主动出牌
			List<List<Integer>> lists = new ArrayList<>();
			Set<Integer> set = cardSort.getCardSort().get(5);				
			for(int pai : set){
				List<Integer> list = new ArrayList<>(6);
				for (int k = 0; k < 6; k++)
					list.add(pai);
				lists.add(list);
			}
			recommandList.addAll(recommandList.size(), lists);
			
			
		} else {
			// 被动出牌
			if (lastCardList.getClass() == A8.class) {
				return;
			}else{
				boolean bomb = (lastCardList.getClass() == A6.class);
				List<List<Integer>> lists = new ArrayList<>();
				Set<Integer> set = cardSort.getCardSort().get(5);				
				for(int pai : set){
					if(!( bomb && ((A6)lastCardList).getNum() > pai)){
						List<Integer> list = new ArrayList<>(6);
						for (int k = 0; k < 6; k++)
							list.add(pai);
						lists.add(list);
					}					
				}
				recommandList.addAll(recommandList.size(), lists);
			}
			
		}

	}
	public static void main(String[] args) {
		A6 a6 = new A6();
		
		List<List<Integer>> recommandList = new ArrayList<>();
		List<Integer> list = new ArrayList<>();
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x0E);
		list.add(0x1B);
		
		CardSort cardSort = new CardSort();
		CardTools.fillCardSort(cardSort, list);

		A6 lastCardList = new A6();
		lastCardList.setNum(1);
		a6.recommand(recommandList, cardSort, lastCardList, list);

		System.out.println(recommandList);
	}
}
