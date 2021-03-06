package com.randioo.doudizhu_server.entity.bo;

import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.entity.DefaultRole;

public class Role extends DefaultRole {

	private int money;
	private int gameId;
	private int sex;
	private int volume = 0;
	private int musicVolume = 0;
	private String moneyExchangeTimeStr;
	private String headImgUrl;
	private int randiooMoney;
	private int moneyExchangeNum;
	private String matchRuleId;
	private int heartbeatTime;
	private SC sc;
    private int roomCards ;//房卡
    
	public int getRoomCards() {
		return roomCards;
	}

	public void setRoomCards(int roomCards) {
		this.roomCards = roomCards;
	}

	public int getRandiooMoney() {
		return randiooMoney;
	}

	public void setRandiooMoney(int randiooMoney) {
		this.randiooMoney = randiooMoney;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(int musicVolume) {
		this.musicVolume = musicVolume;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		setChange(true);
		this.money = money;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getMoneyExchangeTimeStr() {
		return moneyExchangeTimeStr;
	}

	public void setMoneyExchangeTimeStr(String moneyExchangeTimeStr) {
		this.moneyExchangeTimeStr = moneyExchangeTimeStr;
	}

	public int getMoneyExchangeNum() {
		return moneyExchangeNum;
	}

	public void setMoneyExchangeNum(int moneyExchangeNum) {
		this.moneyExchangeNum = moneyExchangeNum;
	}

	public String getMatchRuleId() {
		return matchRuleId;
	}

	public void setMatchRuleId(String matchRuleId) {
		this.matchRuleId = matchRuleId;
	}

	public int getHeartbeatTime() {
		return heartbeatTime;
	}

	public void setHeartbeatTime(int heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
	}

	public SC getSc() {
		return sc;
	}

	public void setSc(SC sc) {
		this.sc = sc;
	}
	

}
