package com.randioo.doudizhu_server.entity.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardRecord;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.component.cardlist.CardList;
import com.randioo.doudizhu_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.doudizhu_server.protocol.Entity.GameConfig;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;

public class Game {
    private int gameId;
    // 玩家id集合
    private Map<String, RoleGameInfo> roleIdMap = new LinkedHashMap<>();
    // 房主id
    private int masterRoleId;
    // 房间锁
    private String lockString;
    // 最大玩家数量
    private int maxRoleCount;
    // 游戏开始
    private GameState gameState;
    // 游戏类型
    private GameType gameType;
    // 游戏配置
    private GameConfig gameConfig;
    // 地主牌
    private List<Integer> landlordCards = new ArrayList<>();
    // 在线玩家数量
    private int onlineRoleCount;
    // 玩家id列表，用于换人
    private List<String> roleIdList = new ArrayList<>();
    // 当前玩家id
    private int currentRoleIdIndex;
    // 出牌的类型
    private CardList lastCardList;
    // 不要
    private int passCount;
    // 地主的玩家id
    private String landlordGameRoleId;
    // 游戏倍数
    private int multiple;
    // 叫地主的分数
    private int score;
    // 叫分的次数
    private int callLandlordCount;
    // 头撂(本场第一个人直接叫3档算4当，赢了算4分，输了算3分）
    private boolean isFirst;
    // 选出的一张明牌
    private int flagCard;
    // 出牌计数
    private int sendCardCount;
    // 出牌的时间戳
    private int sendCardTime;

    private boolean mingPaiState = false;

    private boolean farmerSpring = true;

    private boolean LandLordSpring = true;

    private int bomb;

    private boolean isHuangFan; // 是否为荒番局

    private int huangFanCount;// 剩余荒番局数

    private int round;

    private SC currentStatusSC;

    private List<List<CardRecord>> records = new ArrayList<>();

    private int randiooMoney; // 花费燃点币
    // 房卡
    private int roomCards; // 花费房卡
    private boolean isUseCard;// 是否使用了房卡
    private List<SC> roundoverList = new ArrayList<>();

    private List<String> startTime = new ArrayList<>();
    /** 点击返回菜单按钮的人 */
    private Set<Integer> backMenuSet = new HashSet<>();
    /** 正在申请退出的玩家id */
    private String applyExitGameRoleId;
    /** 申请退出的次数 */
    private int applyExitId;
    /** 表决表 */
    private Map<String, FightVoteApplyExit> voteMap = new HashMap<>();
    /** 是否结算 */
    private boolean isSaveData = false;
    /** 已经叫了几轮分数 */
    private int hasCallScoreRound;
    /** 叫地主开始的人 */
    private String startGameRoleId;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStartGameRoleId() {
        return startGameRoleId;
    }

    public void setStartGameRoleId(String startGameRoleId) {
        this.startGameRoleId = startGameRoleId;
    }

    public int getHasCallScoreRound() {
        return hasCallScoreRound;
    }

    public void setHasCallScoreRound(int hasCallScoreRound) {
        this.hasCallScoreRound = hasCallScoreRound;
    }

    public int getHuangFanCount() {
        return huangFanCount;
    }

    public void setHuangFanCount(int huangFanCount) {
        this.huangFanCount = huangFanCount;
    }

    public int getFlagCard() {
        return flagCard;
    }

    public void setFlagCard(int flagCard) {
        this.flagCard = flagCard;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isSaveData() {
        return isSaveData;
    }

    public void setSaveData(boolean isSaveData) {
        this.isSaveData = isSaveData;
    }

    public int getOnlineRoleCount() {
        return onlineRoleCount;
    }

    public void setOnlineRoleCount(int onlineRoleCount) {
        this.onlineRoleCount = onlineRoleCount;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

    public Map<String, RoleGameInfo> getRoleIdMap() {
        return roleIdMap;
    }

    public int getMasterRoleId() {
        return masterRoleId;
    }

    public void setMasterRoleId(int masterRoleId) {
        this.masterRoleId = masterRoleId;
    }

    public String getLockString() {
        return lockString;
    }

    public void setLockString(String lockString) {
        this.lockString = lockString;
    }

    public int getMaxRoleCount() {
        return maxRoleCount;
    }

    public void setMaxRoleCount(int maxRoleCount) {
        this.maxRoleCount = maxRoleCount;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

    public List<Integer> getLandlordCards() {
        return landlordCards;
    }

    public List<String> getRoleIdList() {
        return roleIdList;
    }

    public int getCurrentRoleIdIndex() {
        return currentRoleIdIndex;
    }

    public void setCurrentRoleIdIndex(int currentRoleIdIndex) {
        System.out.println("currentRoleIdIndex=" + currentRoleIdIndex);
        this.currentRoleIdIndex = currentRoleIdIndex;
    }

    public CardList getLastCardList() {
        return lastCardList;
    }

    public void setLastCardList(CardList lastCardList) {
        this.lastCardList = lastCardList;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        System.out.println("passCount=" + passCount);
        this.passCount = passCount;
    }

    public void setLandlordGameRoleId(String landlordGameRoleId) {
        this.landlordGameRoleId = landlordGameRoleId;
    }

    public String getLandlordGameRoleId() {
        return landlordGameRoleId;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public int getCallLandlordCount() {
        return callLandlordCount;
    }

    public void setCallLandlordCount(int callLandlordCount) {
        this.callLandlordCount = callLandlordCount;
    }

    public int getSendCardCount() {
        return sendCardCount;
    }

    public void setSendCardCount(int sendCardCount) {
        this.sendCardCount = sendCardCount;
    }

    public int getSendCardTime() {
        return sendCardTime;
    }

    public void setSendCardTime(int sendCardTime) {
        this.sendCardTime = sendCardTime;
    }

    public boolean isMingPaiState() {
        return mingPaiState;
    }

    public void setMingPaiState(boolean mingPaiState) {
        this.mingPaiState = mingPaiState;
    }

    public boolean isFarmerSpring() {
        return farmerSpring;
    }

    public void setFarmerSpring(boolean farmerSpring) {
        this.farmerSpring = farmerSpring;
    }

    public boolean isLandLordSpring() {
        return LandLordSpring;
    }

    public void setLandLordSpring(boolean landLordSpring) {
        LandLordSpring = landLordSpring;
    }

    public int getBomb() {
        return bomb;
    }

    public void setBomb(int bomb) {
        this.bomb = bomb;
    }

    public boolean isHuangFan() {
        return isHuangFan;
    }

    public void setHuangFan(boolean isHuangFan) {
        this.isHuangFan = isHuangFan;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<List<CardRecord>> getRecords() {
        return records;
    }

    public SC getCurrentStatusSC() {
        return currentStatusSC;
    }

    public void setCurrentStatusSC(SC currentStatusSC) {
        this.currentStatusSC = currentStatusSC;
    }

    public int getRandiooMoney() {
        return randiooMoney;
    }

    public void setRandiooMoney(int randiooMoney) {
        this.randiooMoney = randiooMoney;
    }

    public int getRoomCards() {
        return roomCards;
    }

    public void setRoomCards(int roomCards) {
        this.roomCards = roomCards;
    }

    public boolean isUseCard() {
        return isUseCard;
    }

    public void setUseCard(boolean isUseCard) {
        this.isUseCard = isUseCard;
    }

    public List<SC> getRoundoverList() {
        return roundoverList;
    }

    public List<String> getStartTime() {
        return startTime;
    }

    public String getApplyExitGameRoleId() {
        return applyExitGameRoleId;
    }

    public void setApplyExitGameRoleId(String applyExitGameRoleId) {
        this.applyExitGameRoleId = applyExitGameRoleId;
    }

    public int getApplyExitId() {
        return applyExitId;
    }

    public void setApplyExitId(int applyExitId) {
        this.applyExitId = applyExitId;
    }

    public Map<String, FightVoteApplyExit> getVoteMap() {
        return voteMap;
    }

    public Set<Integer> getBackMenuSet() {
        return backMenuSet;
    }
}
