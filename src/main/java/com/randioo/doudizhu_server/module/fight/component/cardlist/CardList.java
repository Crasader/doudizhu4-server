package com.randioo.doudizhu_server.module.fight.component.cardlist;

import java.util.List;

import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;

public abstract class CardList implements Comparable<CardList> {

	public abstract CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException;

	public abstract void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr);
}
