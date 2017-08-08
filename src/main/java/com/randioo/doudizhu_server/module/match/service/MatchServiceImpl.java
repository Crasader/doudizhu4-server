package com.randioo.doudizhu_server.module.match.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.cache.file.RoomCardConfigCache;
import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.CancelRoomTimeEvent;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.entity.po.RoleMatchRule;
import com.randioo.doudizhu_server.module.fight.FightConstant;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.module.login.service.LoginService;
import com.randioo.doudizhu_server.module.match.MatchConstant;
import com.randioo.doudizhu_server.module.money.service.MoneyExchangeService;
import com.randioo.doudizhu_server.protocol.Entity.GameConfig;
import com.randioo.doudizhu_server.protocol.Entity.GameRoleData;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Match.MatchAIResponse;
import com.randioo.doudizhu_server.protocol.Match.MatchCancelResponse;
import com.randioo.doudizhu_server.protocol.Match.MatchCreateGameResponse;
import com.randioo.doudizhu_server.protocol.Match.MatchGetGameInfoResponse;
import com.randioo.doudizhu_server.protocol.Match.MatchJoinGameResponse;
import com.randioo.doudizhu_server.protocol.Match.MatchRoleResponse;
import com.randioo.doudizhu_server.protocol.Match.SCMatchAI;
import com.randioo.doudizhu_server.protocol.Match.SCMatchCancelGame;
import com.randioo.doudizhu_server.protocol.Match.SCMatchCreateGame;
import com.randioo.doudizhu_server.protocol.Match.SCMatchJoinGame;
import com.randioo.doudizhu_server.protocol.Match.SCMatchJoinGameData;
import com.randioo.doudizhu_server.protocol.Match.SCMatchOutOfTime;
import com.randioo.doudizhu_server.protocol.Match.SCMatchRole;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.doudizhu_server.util.Tool;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.module.match.MatchHandler;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.module.match.MatchRule;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("matchService")
public class MatchServiceImpl extends ObserveBaseService implements MatchService {

    @Autowired
    private IdClassCreator idClassCreator;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MatchModelService matchModelService;

    @Autowired
    private MoneyExchangeService moneyExchangeService;

    @Autowired
    private FightService fightService;

    @Autowired
    private EventScheduler eventScheduler;

    @Override
    public void init() {
        List<String> locks = GameCache.getGameLocks();
        for (int i = 100000; i < 999999; i++) {
            locks.add(String.valueOf(i));
        }
    }

    @Override
    public void initService() {
        matchModelService.setMatchHandler(new MatchHandler() {

            @Override
            public void outOfTime(MatchRule matchRule) {
                RoleMatchRule roleMatchRule = (RoleMatchRule) matchRule;
                int roleId = roleMatchRule.getRoleId();
                // if (roleMatchRule.isAi()) {
                // Game game = createGame(roleId, gameConfig);
                // addAccountRole(game, roleId);
                // int maxCount = game.getMaxRoleCount();
                // for (int i = game.getRoleIdMap().size(); i < maxCount; i++) {
                // addAIRole(game);
                // }
                // }
                matchModelService.cancelMatch(matchRule.getId());
                SessionUtils.sc(roleId, SC.newBuilder().setSCMatchOutOfTime(SCMatchOutOfTime.newBuilder()).build());
                System.out.println(TimeUtils.getNowTime() + " out of Time");
            }

            @Override
            public void matchSuccess(Map<String, MatchRule> matchMap) {
                loggerdebug("matchSuccess" + matchMap);
                List<RoleMatchRule> list = new ArrayList<>(matchMap.size());
                for (MatchRule matchRule : matchMap.values())
                    list.add((RoleMatchRule) matchRule);

                Collections.sort(list);
                GameConfig config = GameConfig.newBuilder().setDi(3).setMingpai(true).setHuangFang(true).setRound(1)
                        .build();
                Game game = new Game();
                int gameId = idClassCreator.getId(Game.class);
                game.setGameId(gameId);
                game.setGameType(GameType.GAME_TYPE_MATCH);
                game.setGameState(GameState.GAME_STATE_PREPARE);
                int masterId = list.get(0).getRoleId();
                game.setMasterRoleId(masterId);
                game.setLockString("");
                game.setMaxRoleCount(3);

                addAccountRole(game, masterId);

                game.setGameConfig(config);
                game.setRound(config.getRound());

                GameCache.getGameMap().put(gameId, game);

                for (MatchRule matchRule : matchMap.values()) {
                    if (matchRule == list.get(0)) {
                        continue;
                    }
                    RoleMatchRule rule = (RoleMatchRule) matchRule;

                    addAccountRole(game, rule.getRoleId());
                }
                matchSucess(game.getGameId());
            }

            @Override
            public boolean checkMatchRule(MatchRule rule1, MatchRule rule2) {
                RoleMatchRule roleRule1 = (RoleMatchRule) rule1;
                RoleMatchRule roleRule2 = (RoleMatchRule) rule2;

                return roleRule1.getMaxCount() == roleRule2.getMaxCount();
            }

            @Override
            public boolean checkArriveMaxCount(MatchRule rule, Map<String, MatchRule> matchRuleMap) {
                RoleMatchRule roleRule = (RoleMatchRule) rule;

                return matchRuleMap.size() == roleRule.getMaxCount();
            }
        });

        matchModelService.initService();
    }

    @Override
    public void createRoom(Role role, GameConfig gameConfig) {
        if (!checkConfig(gameConfig)) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setMatchCreateGameResponse(
                            MatchCreateGameResponse.newBuilder().setErrorCode(ErrorCode.CREATE_FAILED.getNumber()))
                            .build());
            return;
        }

        int needCards = RoomCardConfigCache.getRoomCardMapByRound().get(gameConfig.getRound()).needCards;
        int needRandiooMoney = RoomCardConfigCache.getRoomCardMapByRound().get(gameConfig.getRound()).needRandiooMoney;
        boolean isUseMoney = false;
        if (needCards > role.getRoomCards()) { // 房卡不够
            if (!moneyExchangeService.exchangeMoney(role, needRandiooMoney, true)) {
                SessionUtils.sc(role.getRoleId(),
                        SC.newBuilder().setMatchCreateGameResponse(
                                MatchCreateGameResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber()))
                                .build());
                return;
            } else {
                if (role.getRandiooMoney() - needRandiooMoney >= 0) {
                    role.setRandiooMoney(role.getRandiooMoney() - needRandiooMoney);
                }
                isUseMoney = true;
            }
        }

        Game game = this.createGame(role.getRoleId(), gameConfig);
        if (isUseMoney) {
            game.setRandiooMoney(gameConfig.getRound() / 3 * 10);
        } else {
            game.setUseCard(true);
            game.setRoomCards(needCards);
        }
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(this.getGameRoleId(game.getGameId(), role.getRoleId()));

        GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game.getGameId());

        // 解散房间定时器
        CancelRoomTimeEvent cancelRoomTimeEvent = new CancelRoomTimeEvent() {
            @Override
            public void update(TimeEvent timeEvent) {
                // TODO Auto-generated method stub
                cancelRoom(((CancelRoomTimeEvent) timeEvent).getGameId());
            }
        };
        int endTime = TimeUtils.getNowTime() + game.getGameConfig().getRound() * 60 / 6 * 80; // 解散
                                                                                              // 6局80分钟
        cancelRoomTimeEvent.setEndTime(endTime);
        cancelRoomTimeEvent.setGameId(game.getGameId());
        // 发射时间截止的计时器
        eventScheduler.addEvent(cancelRoomTimeEvent);

        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setMatchCreateGameResponse(MatchCreateGameResponse.newBuilder()).build());
        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder()
                        .setSCMatchCreateGame(SCMatchCreateGame.newBuilder().setId(game.getLockString())
                                .setGameId(String.valueOf(game.getGameId())).setMingpai(gameConfig.getMingpai())
                                .setGameRoleData(myGameRoleData).setRoomType(GameType.GAME_TYPE_FRIEND.getNumber())
                                .setRoundNum(gameConfig.getRound()))
                        .build());
        // 通知fight录像
        fightService.notifyObservers(FightConstant.FIGHT_CREATE_GAME, roleGameInfo,
                SC.newBuilder()
                        .setSCMatchCreateGame(SCMatchCreateGame.newBuilder().setId(game.getLockString())
                                .setGameId(String.valueOf(game.getGameId())).setMingpai(gameConfig.getMingpai())
                                .setGameRoleData(myGameRoleData).setRoomType(GameType.GAME_TYPE_FRIEND.getNumber())
                                .setRoundNum(gameConfig.getRound()))
                        .build());

    }

    /**
     * 创建游戏
     * 
     * @param roleId
     * @return
     * @author wcy 2017年5月26日
     */
    private Game createGame(int roleId, GameConfig gameConfig) {
        Game game = new Game();
        int gameId = idClassCreator.getId(Game.class);
        game.setGameId(gameId);
        game.setGameType(GameType.GAME_TYPE_FRIEND);
        game.setGameState(GameState.GAME_STATE_PREPARE);

        game.setMasterRoleId(roleId);
        game.setLockString(this.getLockString());
        game.setMaxRoleCount(3);
        for (int i = 0; i < game.getMaxRoleCount(); i++) {
            game.getRoleIdList().add("-1"); // 占位
        }

        this.addAccountRole(game, roleId);

        game.setGameConfig(gameConfig);
        game.setRound(gameConfig.getRound());

        GameCache.getGameMap().put(gameId, game);
        GameCache.getGameLockStringMap().put(game.getLockString(), gameId);

        return game;
    }

    /**
     * 加入玩家
     * 
     * @param game
     * @param roleId
     * @author wcy 2017年5月26日
     */
    private void addAccountRole(Game game, int roleId) {
        String gameRoleId = getGameRoleId(game.getGameId(), roleId);

        addRole(game, roleId, gameRoleId);
    }

    /**
     * 加入ai
     * 
     * @param game
     * @author wcy 2017年5月26日
     */
    private String addAIRole(Game game) {
        String gameRoleId = this.getAIGameRoleId(game.getGameId());

        addRole(game, 0, gameRoleId);
        // 机器人自动准备完毕
        game.getRoleIdMap().get(gameRoleId).ready = true;
        return gameRoleId;
    }

    private void addRole(Game game, int roleId, String gameRoleId) {
        RoleGameInfo roleGameInfo = this.createRoleGameInfo(roleId, gameRoleId);
        // roleGameInfo.seatIndex = game.getRoleIdMap().size();
        if (roleId != 0) {
            Role role = (Role) RoleCache.getRoleById(roleId);
            role.setGameId(game.getGameId());
        }
        int index = -1;
        for (int i = 0; i < game.getMaxRoleCount(); i++) {

            if (game.getRoleIdList().get(i).equals("-1")) {
                index = i;
                break;

            }
        }
        game.getRoleIdList().set(index, gameRoleId);
        game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);
    }

    /**
     * 创建用户在游戏中的数据结构
     * 
     * @param roleId
     * @param gameId
     * @return
     * @author wcy 2017年5月25日
     */
    private RoleGameInfo createRoleGameInfo(int roleId, String gameRoleId) {
        RoleGameInfo roleGameInfo = new RoleGameInfo();
        roleGameInfo.roleId = roleId;
        roleGameInfo.gameRoleId = gameRoleId;

        return roleGameInfo;
    }

    @Override
    public Role getRoleFromRoleGameInfo(RoleGameInfo info) {
        int roleId = info.roleId;
        if (roleId == 0) {
            Role role = new Role();

            role.setName("ROBOT" + info.roleId);
            return role;
        }
        return (Role) RoleCache.getRoleById(roleId);
    }

    @Override
    public void joinGame(Role role, String lockString) {
        Integer gameId = GameCache.getGameLockStringMap().get(lockString);
        System.out.println("gameid:" + gameId);
        if (gameId == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setMatchJoinGameResponse(
                            MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber()))
                            .build());
            return;
        }

        Game game = GameCache.getGameMap().get(gameId);
        System.out.println("game:" + game);
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setMatchJoinGameResponse(
                            MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber()))
                            .build());
            return;
        }
        String targetLock = game.getLockString();
        // 如果锁相同则可以进
        if (!targetLock.equals(lockString)) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setMatchJoinGameResponse(
                            MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.MATCH_ERROR_LOCK.getNumber()))
                            .build());
            return;
        }

        if (game.getRoleIdMap().size() >= game.getMaxRoleCount()) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setMatchJoinGameResponse(
                            MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.MATCH_MAX_ROLE_COUNT.getNumber()))
                            .build());
            return;
        }

        this.addAccountRole(game, role.getRoleId());

        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(this.getGameRoleId(game.getGameId(), role.getRoleId()));

        GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game.getGameId());
        SC scJoinGame = SC.newBuilder().setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(myGameRoleData))
                .build();
        // 通知其他人加入房间
        RoleGameInfo myInfo = null;
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (role.getRoleId() == info.roleId) {
                myInfo = info;
                continue;
            }
            SessionUtils.sc(info.roleId, scJoinGame);
            fightService.notifyObservers(FightConstant.FIGHT_JION, info, scJoinGame);
        }

        List<GameRoleData> gameRoleDataList = new ArrayList<>(game.getRoleIdMap().size());
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            GameRoleData gameRoleData = this.parseGameRoleData(info, game.getGameId());
            gameRoleDataList.add(gameRoleData);
        }
        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setMatchJoinGameResponse(MatchJoinGameResponse.newBuilder()).build());
        SessionUtils.sc(role.getRoleId(), SC.newBuilder()
                .setSCMatchJoinGameData(SCMatchJoinGameData.newBuilder().setMingpai(game.getGameConfig().getMingpai())
                        .addAllGameRoleData(gameRoleDataList).setSeated(myGameRoleData.getSeated()).setId(lockString)
                        .setGameId(String.valueOf(game.getGameId())).setRoomType(GameType.GAME_TYPE_FRIEND.getNumber())
                        .setRoundNum(game.getGameConfig().getRound()))
                .build());
        fightService.notifyObservers(FightConstant.FIGHT_JION, myInfo, SC.newBuilder()
                .setSCMatchJoinGameData(SCMatchJoinGameData.newBuilder().setMingpai(game.getGameConfig().getMingpai())
                        .addAllGameRoleData(gameRoleDataList).setSeated(myGameRoleData.getSeated()).setId(lockString)
                        .setGameId(String.valueOf(game.getGameId())).setRoomType(GameType.GAME_TYPE_FRIEND.getNumber())
                        .setRoundNum(game.getGameConfig().getRound()))
                .build());
    }

    @Override
    public GeneratedMessage match(Role role) {
        if (role == null || role.getMoney() < 1000)
            return SC.newBuilder()
                    .setMatchRoleResponse(MatchRoleResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber()))
                    .build();
        RoleMatchRule matchRule = new RoleMatchRule();
        String matchRuleId = idClassCreator.getId(RoleMatchRule.class) + "_" + role.getRoleId();
        role.setMatchRuleId(matchRuleId);
        matchRule.setId(matchRuleId);
        matchRule.setRoleId(role.getRoleId());
        matchRule.setWaitTime(MatchConstant.MATCH_ROLE_COUNTDOWN);
        matchRule.setAi(false);
        matchRule.setMaxCount(3);
        matchRule.setMatchTime(TimeUtils.getNowTime());
        matchModelService.matchRole(matchRule);
        return SC.newBuilder()
                .setMatchRoleResponse(MatchRoleResponse.newBuilder().setCountdown(MatchConstant.MATCH_ROLE_COUNTDOWN))
                .build();
    }

    @Override
    public GeneratedMessage matchCancel(Role role) {
        if (role.getMatchRuleId() != null) {
            matchModelService.cancelMatch(role.getMatchRuleId());
            role.setMatchRuleId(null);
        }
        return SC.newBuilder().setMatchCancelResponse(MatchCancelResponse.newBuilder()).build();
    }

    @Override
    public void fillAI(Game game) {
        int maxRoleCount = game.getMaxRoleCount();
        // 需要AI的个数
        int needAI = maxRoleCount - game.getRoleIdMap().size();

        List<RoleGameInfo> realRoleGameInfos = new ArrayList<>(game.getRoleIdMap().values());
        for (int i = 0; i < needAI; i++) {
            String aiGameRoleId = addAIRole(game);

            RoleGameInfo info = game.getRoleIdMap().get(aiGameRoleId);
            int index = game.getRoleIdList().indexOf(aiGameRoleId);

            GameRoleData AIGameRoleData = this.parseGameRoleData(info, game.getGameId());
            SC scJoinGame = SC.newBuilder()
                    .setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(AIGameRoleData)).build();

            for (RoleGameInfo roleGameInfo : realRoleGameInfos) {
                SessionUtils.sc(roleGameInfo.roleId, scJoinGame);
                boolean notFull = false;
                // TODO 录像处理
                // this.notifyObservers(MatchConstant.JOIN_GAME, scJoinGame,
                // game.getGameId(), info, notFull);
            }
        }
    }

    @Override
    public void matchAI(Role role) {
        int roleId = role.getRoleId();
        GameConfig config = GameConfig.newBuilder().setDi(1).setMingpai(true).setHuangFang(true).setRound(1).build();
        Game game = createGame(roleId, config);
        game.setGameType(GameType.GAME_TYPE_MATCH);
        RoleGameInfo tRoleGameInfo = game.getRoleIdMap().get(this.getGameRoleId(game.getGameId(), role.getRoleId()));

        GameRoleData myGameRoleData = this.parseGameRoleData(tRoleGameInfo, game.getGameId());
        SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchAIResponse(MatchAIResponse.newBuilder()).build());
        SessionUtils
                .sc(role.getRoleId(),
                        SC.newBuilder()
                                .setSCMatchAI(SCMatchAI.newBuilder().setId(game.getLockString())
                                        .setGameRoleData(myGameRoleData).setMingpai(true)
                                        .setRoomType(GameType.GAME_TYPE_MATCH.getNumber()))
                                .build());
        fightService
                .notifyObservers(FightConstant.FIGHT_RECORD, tRoleGameInfo,
                        SC.newBuilder()
                                .setSCMatchAI(SCMatchAI.newBuilder().setId(game.getLockString())
                                        .setGameRoleData(myGameRoleData).setMingpai(true)
                                        .setRoomType(GameType.GAME_TYPE_MATCH.getNumber()))
                                .build(),
                        1);
        int maxCount = game.getMaxRoleCount();
        for (int i = game.getRoleIdMap().size(); i < maxCount; i++) {
            String gameRoleId = addAIRole(game);

            RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
            System.out.println(info);
            int index = game.getRoleIdList().indexOf(gameRoleId);
            GameRoleData AIGameRoleData = GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready)
                    .setSeated(index).setName("ROBOT" + info.gameRoleId).setId(0).build();

            SC scJoinGame = SC.newBuilder()
                    .setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(AIGameRoleData)).build();
            SessionUtils.sc(role.getRoleId(), scJoinGame);
            fightService.notifyObservers(FightConstant.FIGHT_JION, tRoleGameInfo, scJoinGame);
        }

    }

    @Override
    public GameRoleData parseGameRoleData(RoleGameInfo info, int gameId) {
        Role role = loginService.getRoleById(info.roleId);
        String name = role != null ? role.getName() : "";

        Game game = GameCache.getGameMap().get(gameId);
        int index = game.getRoleIdList().indexOf(info.gameRoleId);
        return GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready).setSeated(index)
                .setName(name).setHeadImgUrl(role.getHeadImgUrl()).setMoney(role.getMoney()).setId(role.getRoleId())
                .setSex(role.getSex()).setAccount(role.getAccount()).build();
    }

    /**
     * 游戏内使用的玩家id
     * 
     * @param gameId
     * @param roleId
     * @return
     * @author wcy 2017年5月24日
     */
    @Override
    public String getGameRoleId(int gameId, int roleId) {
        return gameId + "_" + roleId;
    }

    /**
     * 
     * @param gameId
     * @param roleId
     * @return
     * @author wcy 2017年5月24日
     */
    private String getAIGameRoleId(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        int aiCount = 0;
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            if (roleGameInfo.roleId == 0) {
                aiCount++;
            }
        }
        return gameId + "_0_" + aiCount;
    }

    private String getLockString() {
        int size = GameCache.getGameLocks().size();
        int random = RandomUtils.getRandomNum(size);
        return GameCache.getGameLocks().remove(random);
    }

    public boolean checkConfig(GameConfig gameConfig) {
        int[] di = { 1, 2, 3, 5 };
        int[] round = { 2, 6, 12, 18, 24 };
        if (Tool.indexOf(di, gameConfig.getDi()) == -1 || Tool.indexOf(round, gameConfig.getRound()) == -1) {
            return false;
        }
        return true;
    }

    @Override
    public void matchSucess(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        List<GameRoleData> gameRoleDataList = new ArrayList<>(game.getRoleIdMap().size());
        // 通知每个人游戏开始
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            info.ready = true;
            Role role = (Role) RoleCache.getRoleById(info.roleId);
            GameRoleData gameRoleData = GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready)
                    .setSeated(game.getRoleIdList().indexOf(info.gameRoleId)).setName(role.getName())
                    .setHeadImgUrl(role.getHeadImgUrl()).setMoney(role.getMoney()).setId(info.roleId)
                    .setSex(role.getSex()).build();
            gameRoleDataList.add(gameRoleData);
        }
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId,
                    SC.newBuilder()
                            .setSCMatchRole(SCMatchRole.newBuilder().addAllGameRoleData(gameRoleDataList).setId("0")
                                    .setMingpai(true).setSeated(game.getRoleIdList().indexOf(info.gameRoleId))
                                    .setRoomType(GameType.GAME_TYPE_MATCH.getNumber())
                                    .setGameId(String.valueOf(game.getGameId())))
                            .build());
            fightService.notifyObservers(FightConstant.FIGHT_JION, info,
                    SC.newBuilder()
                            .setSCMatchRole(SCMatchRole.newBuilder().addAllGameRoleData(gameRoleDataList).setId("0")
                                    .setMingpai(true).setSeated(game.getRoleIdList().indexOf(info.gameRoleId))
                                    .setRoomType(GameType.GAME_TYPE_MATCH.getNumber())
                                    .setGameId(String.valueOf(game.getGameId())))
                            .build());
        }
        fightService.gameStart(game.getGameId());
    }

    @Override
    public void cancelRace(String lockString) {

        // GameCache.getGameLockStringMap(lockString)
        Game game = GameCache.getGameMap().get(GameCache.getGameLockStringMap().get(lockString));
        if (game == null)
            return;
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            int roleId = info.roleId;
            // SCMatchCancelGame
            loginService.getRoleById(roleId).setGameId(0);
            SessionUtils.sc(roleId, SC.newBuilder().setSCMatchCancelGame(SCMatchCancelGame.newBuilder()).build());
        }

        GameCache.getGameLocks().add(game.getLockString());
        if (!game.getLockString().equals("")) {
            GameCache.getGameLockStringMap().remove(game.getLockString());
        }
        GameCache.getGameMap().remove(game.getGameId());
    }

    @Override
    public Set<String> getLockStringList() {
        Set<String> lockStringSet = GameCache.getGameLockStringMap().keySet();
        return lockStringSet;
    }

    @Override
    public GeneratedMessage getRoomInfo(Role role) {

        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            return SC.newBuilder()
                    .setMatchGetGameInfoResponse(
                            MatchGetGameInfoResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                    .build();
        }
        GameConfig gameConfig = game.getGameConfig();
        return SC.newBuilder().setMatchGetGameInfoResponse(
                MatchGetGameInfoResponse.newBuilder().setGameConfig(gameConfig).setLockString(game.getLockString()))
                .build();
    }

    private void cancelRoom(int gameId) {
        // GameCache.getGameLockStringMap(lockString)
        Game game = GameCache.getGameMap().get(gameId);
        if (game == null)
            return;
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            int roleId = info.roleId;
            // SCMatchCancelGame
            loginService.getRoleById(roleId).setGameId(0);
            SessionUtils.sc(roleId, SC.newBuilder().setSCMatchCancelGame(SCMatchCancelGame.newBuilder()).build());
        }

        GameCache.getGameLocks().add(game.getLockString());
        if (!game.getLockString().equals("")) {
            GameCache.getGameLockStringMap().remove(game.getLockString());
        }
        GameCache.getGameMap().remove(game.getGameId());
    }
}
