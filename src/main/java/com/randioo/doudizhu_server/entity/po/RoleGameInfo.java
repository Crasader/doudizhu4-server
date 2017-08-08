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
    public List<SC> headList = new ArrayList<>(); // 开始加入房间的sc
    public List<SC> scoreList = new ArrayList<>();// 还原分数SC
    public List<SC> scList = new ArrayList<>(); // 本局sc
    public List<SC> scList2 = new ArrayList<>();
    public List<Integer> videoRoundPoint = new ArrayList<>();
    public Map<Integer, Integer> reportMap; // 可报道牌
    public List<Integer> reportCardList;// 报道的牌
    public boolean isReport; // 是否报道
    public int canUseBombCount; // 能使用炸弹的数量档
                                // (PASS和1档的角色最多只能使用1把炸弹。2档的玩家可以使用2把炸弹，3档的玩家可以不限制使用炸弹次数。没有轮到叫档的玩家默认为不限制使用炸弹次数。)

}
