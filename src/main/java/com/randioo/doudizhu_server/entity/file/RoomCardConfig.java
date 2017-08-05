package com.randioo.doudizhu_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.doudizhu_server.cache.file.RoomCardConfigCache;

public class RoomCardConfig{
	public static final String urlKey="room_card.tbl";
	/** 游戏ID */
	public int gameType;
	/** 局数 */
	public int round;
	/** 消耗房卡 */
	public int needCards;
	/** 消耗燃点币 */
	public int needRandiooMoney;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			RoomCardConfig config = new RoomCardConfig();
			config.gameType=buffer.getInt();
			config.round=buffer.getInt();
			config.needCards=buffer.getInt();
			config.needRandiooMoney=buffer.getInt();
			
			RoomCardConfigCache.putConfig(config);
		}
	}
}
