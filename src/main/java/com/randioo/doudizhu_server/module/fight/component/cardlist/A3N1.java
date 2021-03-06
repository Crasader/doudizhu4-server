package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.CardTools;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardTypeComparableException;

public class A3N1 extends A1 {

	private int addNum;

	public void setAddNum(int addNum) {
		this.addNum = addNum;
	}

	public int getAddNum() {
		return addNum;
	}

	@Override
	public int compareTo(CardList o) {
		if (o.getClass() == A4.class || o.getClass() == KQ.class)
			return -1;
		if (o.getClass() != getClass())
			throw new CardTypeComparableException();

		return getNum() - ((A3N1) o).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 4)
			throw new CardListPatternException();

		Set<Integer> set0 = cardSort.get(0);
		Set<Integer> set2 = cardSort.get(2);

		if (set0.size() == 2 && set2.size() == 1) {
			int num = set2.iterator().next();
			int addNum = addNum(cardSort, num);
			A3N1 a = new A3N1();
			a.setNum(num);
			a.setAddNum(addNum);
			return a;
		}

		throw new CardListPatternException();
	}

	private int addNum(CardSort cardSort, int value) {
		for (int n : cardSort.get(0)) {
			if (n != value) {
				return n;
			}
		}
		return 0;
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		if (lastCardList == null) {
			return;
		}
		if (arr.size() < 4 || cardSort.get(2).size() < 1 || cardSort.get(0).size() < 2)
			return;
		if (lastCardList.getClass() != getClass()) {
			return;
		}
		A3N1 a1 = (A3N1) lastCardList;
		int tnum = a1.getNum();

		List<List<Integer>> lists = new ArrayList<>();
		Set<Integer> tset = null;
		CardSort tcardSort = cardSort.clone();
		
		Set<Integer> set = tcardSort.get(3);				
		List<Integer> temp = new ArrayList<>(set);
		tcardSort.remove(temp);

		set = tcardSort.get(1);				
		temp = new ArrayList<>(set);
		for (int value : temp) {
			tcardSort.get(0).remove(value);
		}
			
		tset = tcardSort.get(2);
		Set<Integer> set0 = tcardSort.get(0);
		if(!set0.iterator().hasNext())
			return;	
		for(int pai : tset){
			if(pai > tnum){
				List<Integer> list = new ArrayList<>(4);
				for (int k = 0; k < 3; k++)
					list.add(pai);
				list.add(set0.iterator().next());
				lists.add(list);
			}
		}
		recommandList.addAll(recommandList.size(), lists);
	}


}
