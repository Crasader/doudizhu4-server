package com.randioo.doudizhu_server.module.fight.component.dispatch;

import java.util.List;

public interface Dispatcher {
	/**
	 * 发牌
	 * 
	 * @param totalCards
	 *            卡牌的总数
	 * @param everyPartCount
	 *            每组卡牌的数量
	 * @param group
	 *            分组数
	 * @return
	 */
	List<CardPart> dispatch(List<Integer> totalCards, int everyPartCount, int group);
}
