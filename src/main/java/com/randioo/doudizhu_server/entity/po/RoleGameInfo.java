package com.randioo.doudizhu_server.entity.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.randioo.doudizhu_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;

public class RoleGameInfo {
	public String gameRoleId;
	public int roleId;
	public boolean ready;
	// public int seatIndex;
	public FightVoteApplyExit vote;
	public List<Integer> cards = new ArrayList<>();
	public int auto;
	public int allMark;
	public int currentMark;
	public int farmerNum;
	public int landLordNum;
	public boolean online = true;
	/** 申请退出时间 */
	public int lastRejectedExitTime;
	public int callLandlordScore = -1;
	public List<SC> headList = new ArrayList<>(); //开始加入房间的sc
	public List<SC> scoreList = new ArrayList<>();//还原分数SC
	public List<SC> scList = new ArrayList<>(); //本局sc
	public List<SC> scList2 = new ArrayList<>();
	public List<Integer> videoRoundPoint = new ArrayList<>();
	public Map<Integer,Integer> reportMap ; //可报道牌
	public List<Integer> reportCardList ;//报道的牌
}
