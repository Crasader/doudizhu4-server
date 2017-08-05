package com.randioo.doudizhu_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.doudizhu_server.entity.file.RoomCardConfig;

public class RoomCardConfigCache {

	private static final int  gameType =  1 ; //斗地主 类型id为 1 ，房卡.xlsx | 游戏类型
	
	//局数为 key 
	private static Map<Integer,RoomCardConfig> roomCardMapByRound = new HashMap<>(); 
	
	public static void putConfig(RoomCardConfig config) {
		 if(config.gameType == gameType){
			 roomCardMapByRound.put(config.round, config);
		 }
	}

	public static Map<Integer, RoomCardConfig> getRoomCardMapByRound() {
		return roomCardMapByRound;
	}
}
