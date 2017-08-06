package com.randioo.doudizhu_server.module.fight.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.GlobleConstant;
import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.comparator.HexCardComparator;
import com.randioo.doudizhu_server.dao.VideoDAO;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.bo.VideoData;
import com.randioo.doudizhu_server.entity.po.AgreeExitTimeEvent;
import com.randioo.doudizhu_server.entity.po.CallLandLordTimeEvent;
import com.randioo.doudizhu_server.entity.po.CardRecord;
import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.entity.po.DisconnectTimeEvent;
import com.randioo.doudizhu_server.entity.po.GameOverTimeEvent;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.entity.po.RoundOverTimeEvent;
import com.randioo.doudizhu_server.entity.po.SendCardTimeEvent;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.FightConstant;
import com.randioo.doudizhu_server.module.fight.component.CardTools;
import com.randioo.doudizhu_server.module.fight.component.GameInit;
import com.randioo.doudizhu_server.module.fight.component.LandlordJudger;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A1;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A2;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A2B2C2;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A3;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A3B3;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A3B3C2D2;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A3B3CD;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A3N1;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A3N2;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A4;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A4B2C2;
import com.randioo.doudizhu_server.module.fight.component.cardlist.A4BC;
import com.randioo.doudizhu_server.module.fight.component.cardlist.ABCDE;
import com.randioo.doudizhu_server.module.fight.component.cardlist.CardList;
import com.randioo.doudizhu_server.module.fight.component.cardlist.KQ;
import com.randioo.doudizhu_server.module.fight.component.dispatch.CardPart;
import com.randioo.doudizhu_server.module.fight.component.dispatch.DebugDispatcher;
import com.randioo.doudizhu_server.module.fight.component.dispatch.Dispatcher;
import com.randioo.doudizhu_server.module.fight.component.dispatch.RandomDispatcher;
import com.randioo.doudizhu_server.module.login.service.LoginService;
import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.doudizhu_server.module.money.service.MoneyExchangeService;
import com.randioo.doudizhu_server.module.score.service.ScoreService;
import com.randioo.doudizhu_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.doudizhu_server.protocol.Entity.GameConfig;
import com.randioo.doudizhu_server.protocol.Entity.GameOverData;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.doudizhu_server.protocol.Entity.PaiNum;
import com.randioo.doudizhu_server.protocol.Entity.RecommandPai;
import com.randioo.doudizhu_server.protocol.Entity.Record;
import com.randioo.doudizhu_server.protocol.Entity.RoleInfo;
import com.randioo.doudizhu_server.protocol.Entity.RoundVideoData;
import com.randioo.doudizhu_server.protocol.Entity.Score;
import com.randioo.doudizhu_server.protocol.Entity.video;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Fight.FightAgreeExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightApplyExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightAutoResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightBackMenuResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightCallLandLordResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightGameInfoResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightGetlastRoundResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightMingPaiResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightReadyResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightRecommandResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightRejoinResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightSendCardResponse;
import com.randioo.doudizhu_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightApplyExitResult;
import com.randioo.doudizhu_server.protocol.Fight.SCFightAuto;
import com.randioo.doudizhu_server.protocol.Fight.SCFightCallFen;
import com.randioo.doudizhu_server.protocol.Fight.SCFightCallLandLord;
import com.randioo.doudizhu_server.protocol.Fight.SCFightCurrentScore;
import com.randioo.doudizhu_server.protocol.Fight.SCFightExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightFinishRejoin;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameDismiss;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameOver;
import com.randioo.doudizhu_server.protocol.Fight.SCFightLandLord;
import com.randioo.doudizhu_server.protocol.Fight.SCFightLastRoundReady;
import com.randioo.doudizhu_server.protocol.Fight.SCFightMingPai;
import com.randioo.doudizhu_server.protocol.Fight.SCFightMingPaiOnOrOff;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPutOut;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPutOutPai;
import com.randioo.doudizhu_server.protocol.Fight.SCFightReady;
import com.randioo.doudizhu_server.protocol.Fight.SCFightRejoin;
import com.randioo.doudizhu_server.protocol.Fight.SCFightRoundOver;
import com.randioo.doudizhu_server.protocol.Fight.SCFightSpring;
import com.randioo.doudizhu_server.protocol.Fight.SCFightStart;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.doudizhu_server.util.VideoUtils;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.log.HttpLogUtils;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.ReflectUtils;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

    @Autowired
    private VideoDAO videoDAO;

    @Autowired
    private MatchService matchService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private MoneyExchangeService moneyExchangeService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private HexCardComparator hexCardComparator;

    @Autowired
    private EventScheduler eventScheduler;

    @Autowired
    private RandiooPlatformSdk randiooPlatformSdk;

    @Autowired
    private GameDB gameDB;

    @Autowired
    private RandomDispatcher randomDispatcher;

    @Autowired
    private DebugDispatcher debugDispatcher;

    @Autowired
    private LandlordJudger landlordJudger;

    @Autowired
    private GameInit gameInit;

    @Override
    public void init() {

        // 把牌型加入到cardLists(cardLists 为牌型列表)
        Map<Class<? extends CardList>, CardList> cardLists = GameCache.getCardLists();
        List<Class<? extends CardList>> classes = new ArrayList<>();

        classes.add(A1.class);
        classes.add(A2.class);
        classes.add(A3.class);
        classes.add(A4.class);
        classes.add(A3N1.class);
        classes.add(A3N2.class);
        classes.add(A3B3.class);
        classes.add(KQ.class);
        classes.add(ABCDE.class);
        classes.add(A2B2C2.class);
        classes.add(A3B3CD.class);
        classes.add(A3B3C2D2.class);
        classes.add(A4BC.class);
        classes.add(A4B2C2.class);

        for (Class<? extends CardList> clazz : classes)
            cardLists.put(clazz, ReflectUtils.newInstance(clazz));

        // 把牌型加入到recommendCardLists(recommendCardLists 为提示列表)
        List<Class<? extends CardList>> recommendClasses = new ArrayList<>();
        Map<Class<? extends CardList>, CardList> recommendCardLists = GameCache.getRecommendCardLists();
        recommendClasses.add(A1.class);
        recommendClasses.add(A2.class);
        recommendClasses.add(A3.class);
        recommendClasses.add(A3N1.class);
        recommendClasses.add(A3N2.class);
        recommendClasses.add(ABCDE.class);
        recommendClasses.add(A2B2C2.class);
        recommendClasses.add(A3B3.class);
        recommendClasses.add(A3B3CD.class);
        recommendClasses.add(A3B3C2D2.class);
        recommendClasses.add(A4.class);
        recommendClasses.add(KQ.class);
        for (Class<? extends CardList> clazz : recommendClasses) {
            CardList cardList = cardLists.get(clazz);
            recommendCardLists.put(clazz, cardList);
        }

        // 炸弹， 王炸牌型 放入 缓存
        GameCache.getSendCardSeqCheckerList().add(GameCache.getCardLists().get(A4.class));
        GameCache.getSendCardSeqCheckerList().add(GameCache.getCardLists().get(KQ.class));
    }

    @Override
    public void update(Observer observer, String msg, Object... args) {
        if (msg.equals(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD)) {
            Game game = GameCache.getGameMap().get(args[0]);
            String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
            RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
            System.out.println("@@@" + (game.getRoleIdList().indexOf(info.gameRoleId) == game.getCurrentRoleIdIndex()));
            if ((info.roleId == 0 && game.getRoleIdList().indexOf(info.gameRoleId) == game.getCurrentRoleIdIndex())) {
                CallLandLordTimeEvent callLandLordTimeEvent = new CallLandLordTimeEvent() {

                    @Override
                    public void update(TimeEvent timeEvent) {
                        timeUp((CallLandLordTimeEvent) timeEvent);
                    }
                };

                callLandLordTimeEvent.setGameRoleId(gameRoleId);
                callLandLordTimeEvent.setEndTime(TimeUtils.getNowTime() + 3);
                callLandLordTimeEvent.setGameId(game.getGameId());
                callLandLordTimeEvent.setScore(RandomUtils.getRandomNum(4));

                eventScheduler.addEvent(callLandLordTimeEvent);

                loggerinfo("game=>" + game.getGameId() + info.roleId + "不叫");
            }
            // IoSession session = SessionCache.getSessionById(info.roleId);
            if (game.getGameType() == GameType.GAME_TYPE_MATCH) {
                CallLandLordTimeEvent callLandLordTimeEvent = new CallLandLordTimeEvent() {

                    @Override
                    public void update(TimeEvent timeEvent) {
                        timeUp((CallLandLordTimeEvent) timeEvent);
                    }
                };
                callLandLordTimeEvent.setGameRoleId(gameRoleId);
                callLandLordTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.SEND_CARD_WAIT_TIME / 2);
                callLandLordTimeEvent.setGameId(game.getGameId());
                callLandLordTimeEvent.setScore(0);

                eventScheduler.addEvent(callLandLordTimeEvent);
            }
        }
        if (msg.equals(FightConstant.FIGHT_APPLY_LEAVE)) {
            int gameId = (int) args[0];
            RoleGameInfo gameRoleInfo = (RoleGameInfo) args[1];
            SC applyExitSC = (SC) args[2];
            this.observeApplyExit(gameId, gameRoleInfo.gameRoleId,
                    applyExitSC.getSCFightApplyExitGame().getApplyExitId());
        }
        if (msg.equals(FightConstant.NEXT_GAME_ROLE_SEND_CARD)) {
            int gameId = (int) args[0];
            this.checkAutoAI(gameId);
        }
    }

    @Override
    public void initService() {
        this.addObserver(this);
    }

    /**
     * 通知该场游戏的所有人
     * 
     * @param game
     * @param sc
     * @author wcy 2017年6月29日
     */
    private void sendSC(Game game, SC sc) {
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values())
            SessionUtils.sc(roleGameInfo.roleId, sc);

    }

    private void observeApplyExit(int gameId, String gameRoleId, int applyExitId) {
        Game game = this.getGameById(gameId);
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        if (roleGameInfo.roleId == 0) {
            aiAgreeExit(gameId, gameRoleId);
        } else { // 这个else没啥有 就没有放入线程中
            // 不是机器人进入自动托管状态
            AgreeExitTimeEvent agreeExitTimeEvent = new AgreeExitTimeEvent() {

                @Override
                public void update(TimeEvent timeEvent) {
                    int gameId = ((AgreeExitTimeEvent) timeEvent).getGameId();
                    String gameRoleId = ((AgreeExitTimeEvent) timeEvent).getGameRoleId();
                    aiAgreeExit(gameId, gameRoleId);
                }
            };
            agreeExitTimeEvent.setGameId(gameId);
            agreeExitTimeEvent.setGameRoleId(gameRoleId);
            agreeExitTimeEvent.setApplyExitId(applyExitId);
            agreeExitTimeEvent.setEndTime(TimeUtils.getNowTime() + 15);
        }
    }

    @Override
    public GeneratedMessage auto(Role role) {// 托管
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            return SC.newBuilder().setFightAutoResponse(
                    FightAutoResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())).build();
        }
        // 没叫完地主不让托管
        if (game.getCallLandlordCount() < game.getMaxRoleCount() && game.getMultiple() < 3) {
            return SC.newBuilder().setFightAutoResponse(
                    FightAutoResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber())).build();
        }
        RoleGameInfo myInfo = null;
        // 获得当前role对应的 game中的 roleGameInfo
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.roleId == role.getRoleId()) {
                myInfo = info;
                break;
            }
        }
        if (myInfo == null) {
            return SC.newBuilder().setFightAutoResponse(
                    FightAutoResponse.newBuilder().setErrorCode(ErrorCode.NO_ROLE_DATA.getNumber())).build();
        }
        if (game.getGameState() != GameState.GAME_START_START) {
            return SC.newBuilder().setFightAutoResponse(FightAutoResponse.newBuilder()).build();
        }
        if (myInfo.auto >= 2) {// 托管解除
            myInfo.auto = 0;
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SessionUtils.sc(info.roleId,
                        SC.newBuilder()
                                .setSCFightAuto(SCFightAuto.newBuilder()
                                        .setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId)).setAuto(false))
                                .build());
                this.notifyObservers(FightConstant.FIGHT_AUTO, info,
                        SC.newBuilder()
                                .setSCFightAuto(SCFightAuto.newBuilder()
                                        .setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId)).setAuto(false))
                                .build());
            }
        } else {
            myInfo.auto = 2;
            // 为托管状态 ，轮到当前用户时 ，自动出牌
            if (game.getRoleIdList().get(game.getCurrentRoleIdIndex()).equals(myInfo.gameRoleId)) {
                autoSendCard(game.getGameId(), myInfo.gameRoleId);// 自动出牌
            } else {
                for (RoleGameInfo info : game.getRoleIdMap().values()) {
                    SessionUtils.sc(info.roleId,
                            SC.newBuilder()
                                    .setSCFightAuto(SCFightAuto.newBuilder()
                                            .setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId)).setAuto(true))
                                    .build());
                    this.notifyObservers(FightConstant.FIGHT_AUTO, info,
                            SC.newBuilder()
                                    .setSCFightAuto(SCFightAuto.newBuilder()
                                            .setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId)).setAuto(false))
                                    .build());
                }
            }
        }
        return SC.newBuilder().setFightAutoResponse(FightAutoResponse.newBuilder()).build();
    }

    @Override
    public void readyGame(Role role) {
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder()
                            .setFightReadyResponse(
                                    FightReadyResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }

        String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        // 清理上局scist
        roleGameInfo.scList.clear();
        // 游戏准备
        SessionUtils.sc(roleGameInfo.roleId,
                SC.newBuilder().setFightReadyResponse(FightReadyResponse.newBuilder()).build());
        roleGameInfo.ready = true;
        SC scFightReady = SC.newBuilder()
                .setSCFightReady(
                        SCFightReady.newBuilder().setSeated(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId))
                                .setIsFirst(game.getGameConfig().getRound() == game.getRound()))
                .build();
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId, scFightReady);
            this.notifyObservers(FightConstant.FIGHT_READY, info, scFightReady);
        }
        // 通知 场上所有人 当前role的积分
        SC sc = SC.newBuilder()
                .setSCFightCurrentScore(SCFightCurrentScore.newBuilder()
                        .setSeated(game.getRoleIdList().indexOf(gameRoleId)).setCurrentScore(roleGameInfo.allMark))
                .build();
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId, sc);
            this.notifyObservers(FightConstant.FIGHT_CURRENT_SCORE, info, sc);
        }

        // 检查是否全部都准备完毕,全部准备完毕开始游戏
        boolean matchAI = GlobleMap.Boolean(GlobleConstant.ARGS_MATCH_AI);
        if (matchAI ? this.checkAllAI(game) : this.checkAllReady(role.getGameId())) {
            if (matchAI) {
                matchService.fillAI(game);
            }
            gameStart(game.getGameId());
        }
    }

    private boolean checkAllAI(Game game) {
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (!info.ready)
                return false;
        }
        return true;
    }

    @Override
    public void rejoin(Role role) {// 重连
        int gameId = role.getGameId();
        /* System.out.println("@@@@"+gameId); */
        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder()
                            .setFightRejoinResponse(
                                    FightRejoinResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }
        // 获得当前role对应的 game中的 roleGameInfo
        RoleGameInfo myInfo = null;
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.roleId == role.getRoleId()) {
                myInfo = info;
                break;
            }
        }
        if (myInfo == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder()
                            .setFightRejoinResponse(
                                    FightRejoinResponse.newBuilder().setErrorCode(ErrorCode.NO_ROLE_DATA.getNumber()))
                            .build());
            return;
        }

        List<ByteString> scList = new ArrayList<>();

        // 重连后逐条发通知
        try {
            for (SC sc : myInfo.headList) {
                scList.add(sc.toByteString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 单局内的sc
        for (SC sc : myInfo.scList) {
            scList.add(sc.toByteString());
        }
        scList.add(SC.newBuilder().setSCFightFinishRejoin(SCFightFinishRejoin.newBuilder()).build().toByteString());

        // 还原单局游戏 所需要的所有sc ，通过response 返回给role
        RoundVideoData roundVideoData = RoundVideoData.newBuilder().addAllSc(scList).build();

        SessionUtils.sc(role.getRoleId(), SC.newBuilder()
                .setFightRejoinResponse(FightRejoinResponse.newBuilder().setRoundVideoData(roundVideoData)).build());

        myInfo.online = true;

        for (RoleGameInfo info : game.getRoleIdMap().values()) {// 通知其他玩家我已经重上好了
            if (info.roleId == role.getRoleId()) {
                continue;
            }
            SessionUtils.sc(info.roleId,
                    SC.newBuilder().setSCFightRejoin(
                            SCFightRejoin.newBuilder().setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId)))
                            .build());
        }

        if (myInfo.auto >= 2) {// 重连后解除托管状态
            this.auto(role);
        }
    }

    @Override
    public void gameStart(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        game.getStartTime().add(TimeUtils.getDetailTimeStr());
        game.getRecords().add(new ArrayList<CardRecord>());
        game.setGameState(GameState.GAME_START_START);
        // 游戏初始化
        gameInit.init(game);
        // 分牌
        this.dispatchCard(gameId);
        // 通知场上积分
        this.callFen(game);

        // 剩余局数
        int remainRoundCount = game.getRound() - 1;
        game.setRound(remainRoundCount);
        boolean moGuai = game.isMoGuai();

        SCFightStart.Builder scFightStartBuilder = SCFightStart.newBuilder();
        scFightStartBuilder.setMoguai(moGuai);
        scFightStartBuilder.setRoundNum(remainRoundCount);

        for (int i = 0; i < game.getMaxRoleCount(); i++) {
            String gameRoleId = game.getRoleIdList().get(i);
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
            // 该玩家 卡牌数量
            int cardSize = roleGameInfo.cards.size();

            PaiNum paiNum = PaiNum.newBuilder().setSeated(i).setNum(cardSize).build();
            scFightStartBuilder.addPaiNum(paiNum);
        }

        SCFightStart scFightStart = scFightStartBuilder.build();

        int currentFen = 0;
        int currentSeat = game.getCurrentRoleIdIndex();
        int callLandlordCountdown = FightConstant.COUNTDOWN / 2;
        int fen = -1;

        // 叫地主的主推
        SC scFightCallLandlordSC = SC.newBuilder().setSCFightCallLandLord(SCFightCallLandLord.newBuilder()
                .setCurrentFen(currentFen).setSeated(currentSeat).setCountdown(callLandlordCountdown).setFen(fen))
                .build();
        game.setCurrentStatusSC(scFightCallLandlordSC);

        // 通知每个人 各自的牌
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            info.auto = 0;
            info.videoRoundPoint.add(info.scList2.size());

            info.scList2.addAll(info.scoreList);
            info.scoreList.clear();

            // 克隆基础消息
            SCFightStart.Builder currentGameRoleSCFightStartBuilder = scFightStart.toBuilder();
            // 发送给每个人的游戏开始通知
            SCFightStart currentGameRoleSCFightStart = currentGameRoleSCFightStartBuilder.addAllPai(info.cards).build();
            SC currentGameRoleSCFightStartSC = SC.newBuilder().setSCFightStart(currentGameRoleSCFightStart).build();
            SessionUtils.sc(info.roleId, currentGameRoleSCFightStartSC);
            this.notifyObservers(FightConstant.FIGHT_START, info, currentGameRoleSCFightStartSC);

            // 每个人都要通知开始叫地主
            SessionUtils.sc(info.roleId, scFightCallLandlordSC);
            this.notifyObservers(FightConstant.FIGHT_START_CALL_LANDLORD, info, scFightCallLandlordSC, 1);

            // 所有人的pai 记录到场上所有的录像
            int seat = game.getRoleIdList().indexOf(info.gameRoleId);
            SC scFightMingPaiSC = SC.newBuilder()
                    .setSCFightMingPai(SCFightMingPai.newBuilder().addAllPai(info.cards).setSeated(seat).setIsFirst(1))
                    .build();

            for (RoleGameInfo info2 : game.getRoleIdMap().values()) {
                this.notifyObservers(FightConstant.FIGHT_ALL_CARD, info2, scFightMingPaiSC);
            }

        }

        this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
    }

    /**
     * 检查全部准备完毕
     * 
     * @param gameId
     * @return
     * @author wcy 2017年5月31日
     */
    private boolean checkAllReady(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        if (game.getRoleIdMap().size() < game.getMaxRoleCount())
            return false;

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (!info.ready)
                return false;
        }
        return true;
    }

    /**
     * 明牌请求
     */
    @Override
    public void mingPai(Role role, boolean isMingPai) {
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightMingPaiResponse(
                            FightMingPaiResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
        }
        RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
        if (game.getRoleIdMap().get(game.getLandlordGameRoleId()).roleId != role.getRoleId()) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder()
                            .setFightMingPaiResponse(
                                    FightMingPaiResponse.newBuilder().setErrorCode(ErrorCode.NOT_LANDLORD.getNumber()))
                            .build());
        }
        // 当游戏配中不允许明牌时，提示请求错误
        if (!game.getGameConfig().getMingpai()) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightMingPaiResponse(
                            FightMingPaiResponse.newBuilder().setErrorCode(ErrorCode.MINGPAI_FORBIDDEN.getNumber()))
                            .build());
        }

        // 通知关闭明牌按钮
        SCFightMingPaiOnOrOff scFightMingPaiOnOrOff = SCFightMingPaiOnOrOff.newBuilder().setIsOpen(false).build();
        SC sc = SC.newBuilder().setSCFightMingPaiOnOrOff(scFightMingPaiOnOrOff).build();
        // role选择不明牌
        if (!isMingPai) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightMingPaiResponse(FightMingPaiResponse.newBuilder()).build());

            SessionUtils.sc(role.getRoleId(), sc);
            notifyObservers(FightConstant.FIGHT_MINGPAI_ONOROFF, LandLord, sc);
            // 通知出牌
            outPutSc(game);
            notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, game.getGameId());
            return;
        }

        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setFightMingPaiResponse(FightMingPaiResponse.newBuilder()).build());
        SessionUtils.sc(role.getRoleId(), sc);
        notifyObservers(FightConstant.FIGHT_MINGPAI_ONOROFF, LandLord, sc);
        // 通知出牌
        outPutSc(game);
        notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, game.getGameId());
        game.setMultiple(game.getMultiple() * 2);
        this.callFen(game);
        game.setMingPaiState(true);

        SCFightMingPai scMingPai = SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
                .setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId)).setSex(role.getSex()).build();

        SC scFightMingPai = SC.newBuilder().setSCFightMingPai(scMingPai).build();

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId, scFightMingPai);
            this.notifyObservers(FightConstant.FIGHT_MINGPAI, info, scFightMingPai);
        }

    }

    // 出牌通知
    private void outPutSc(Game game) {

        SCFightPutOut scFightPutOut = createSCFightPutOut(game, false);

        SC sc = SC.newBuilder().setSCFightPutOut(scFightPutOut).build();
        game.setCurrentStatusSC(sc);

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId, sc);
            this.notifyObservers(FightConstant.FIGHT_OUTPUT, info, sc);
        }
    }

    /**
     * 获取上一圈数据
     */
    @Override
    public GeneratedMessage getLastRecord(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            return SC.newBuilder()
                    .setFightGetlastRoundResponse(
                            FightGetlastRoundResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                    .build();
        }
        List<CardRecord> records = game.getRecords().get(game.getRecords().size() - 1);
        if (records.size() < 3) {
            return SC.newBuilder()
                    .setFightGetlastRoundResponse(
                            FightGetlastRoundResponse.newBuilder().setErrorCode(ErrorCode.FIRST_ROUND.getNumber()))
                    .build();
        }
        FightGetlastRoundResponse.Builder builder = FightGetlastRoundResponse.newBuilder();
        List<CardRecord> record = new ArrayList<>();
        for (int i = (records.size() - 5 < 1 ? 0 : records.size() - 5); i < records.size() - 2; i++) {
            record.add(records.get(i));
        }
        for (CardRecord temp : record) {
            builder.addRecords(
                    Record.newBuilder().setSeated(game.getRoleIdList().indexOf(temp.gameRoleId)).addAllPai(temp.cards));
        }
        return SC.newBuilder().setFightGetlastRoundResponse(builder).build();

    }

    // @Override
    // public GeneratedMessage exitGame(Role role) {
    // Game game = GameCache.getGameMap().get(role.getGameId());
    // if (game == null) {
    // return SC
    // .newBuilder()
    // .setFightExitGameResponse(
    // FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
    // .build();
    // }
    //
    // String gameRoleId = matchService.getGameRoleId(game.getGameId(),
    // role.getRoleId());
    //
    // GameState gameState = game.getGameState();
    // // 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
    // if (gameState == GameState.GAME_STATE_PREPARE && game.getRound() ==
    // game.getGameConfig().getRound()) {
    // // 若是房主，则直接解散
    // if (game.getMasterRoleId() == role.getRoleId()) {
    // if (game.getRound() == game.getGameConfig().getRound()) {
    // moneyExchangeService.exchangeMoney(role, game.getGameConfig().getRound()
    // / 3, false);
    // role.setRandiooMoney(role.getRandiooMoney() +
    // game.getGameConfig().getRound() / 3 * 10);
    // }
    // game.setGameState(GameState.GAME_START_END);
    //
    // SC scDismiss =
    // SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
    // for (RoleGameInfo info : game.getRoleIdMap().values()) {
    // SessionUtils.sc(info.roleId, scDismiss);
    // Role tempRole = (Role) RoleCache.getRoleById(info.roleId);
    // if (role != null) {
    // tempRole.setGameId(0);
    // }
    // }
    // GameCache.getGameLocks().add(game.getLockString());
    // if (!game.getLockString().equals("")) {
    // GameCache.getGameLockStringMap().remove(game.getLockString());
    // }
    // // 将游戏从缓存池中移除
    // GameCache.getGameMap().remove(game.getGameId());
    // } else {
    // // 该玩家退出
    // SC scExit = SC
    // .newBuilder()
    // .setSCFightExitGame(
    // SCFightExitGame.newBuilder().setSeated(game.getRoleIdList().indexOf(gameRoleId)))
    // .build();
    // for (RoleGameInfo info : game.getRoleIdMap().values()) {
    // SessionUtils.sc(info.roleId, scExit);
    // this.notifyObservers(FightConstant.FIGHT_RECORD, info, scExit);
    // }
    // game.getRoleIdMap().remove(gameRoleId);
    // role.setGameId(0);
    // }
    //
    // }
    // // 如果游戏已经开始,则要申请退出
    // else /* if (gameState == GameState.GAME_START_START) */{
    // if (game.getOnlineRoleCount() != 0 /*
    // * || game.getRoleIdMap().get(
    // * gameRoleId).applyExitTime >
    // * TimeUtils.getNowTime() - 90
    // */) {
    // return SC
    // .newBuilder()
    // .setFightExitGameResponse(
    // FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_EXITING.getNumber()))
    // .build();
    // }
    // game.setApplyExitTime(TimeUtils.getNowTime());
    // SC scApplyExit = SC
    // .newBuilder()
    // .setSCFightApplyExitGame(
    // SCFightApplyExitGame.newBuilder().setName(role.getName())
    // .setCountDown(FightConstant.COUNTDOWN)).build();
    // setOnlineCount(game.getGameId());
    // for (RoleGameInfo info : game.getRoleIdMap().values()) {
    // if (info.roleId != role.getRoleId()) {
    // SessionUtils.sc(info.roleId, scApplyExit);
    // this.notifyObservers(FightConstant.APPLY_LEAVE, game.getGameId(), info);
    // }
    // }
    // agreeExit(role, true);
    // }
    //
    // return
    // SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build();
    // }

    @Override
    public void exitGame(Role role) {
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightExitGameResponse(
                            FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }

        if (!this.checkGameNeverStart(game)) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightExitGameResponse(
                            FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }

        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build());

        // 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
        // 若是房主，则直接解散
        if (game.getMasterRoleId() == role.getRoleId()) {
            // 退还钱款
            if (game.isUseCard()) {
                role.setRoomCards(role.getRoomCards() + game.getRoomCards());
            } else {
                moneyExchangeService.exchangeMoney(role, game.getRandiooMoney(), false);
                role.setRandiooMoney(role.getRandiooMoney() + game.getRandiooMoney());
            }

            // 标记比赛结束
            game.setGameState(GameState.GAME_START_END);

            // 通知所有人比赛结束，并把游戏id标记变成0
            SC scDismiss = SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SessionUtils.sc(info.roleId, scDismiss);
                Role tempRole = (Role) RoleCache.getRoleById(info.roleId);
                if (tempRole != null) {
                    tempRole.setGameId(0);
                }
            }

            GameCache.getGameLocks().add(game.getLockString());
            if (!StringUtils.isNullOrEmpty(game.getLockString()))
                GameCache.getGameLockStringMap().remove(game.getLockString());

            // 将游戏从缓存池中移除
            GameCache.getGameMap().remove(game.getGameId());
        } else {
            String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
            // 该玩家直接退出
            SC scExit = SC.newBuilder().setSCFightExitGame(
                    SCFightExitGame.newBuilder().setSeated(game.getRoleIdList().indexOf(gameRoleId))).build();
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SessionUtils.sc(info.roleId, scExit);
            }
            // 把role从game中移除
            game.getRoleIdMap().remove(gameRoleId);
            int index = game.getRoleIdList().indexOf(gameRoleId);
            // “-1” 为初始状态用来占位
            game.getRoleIdList().set(index, "-1");
            role.setGameId(0);
        }

    }

    @Override
    public void applyExitGame(Role role) {
        Game game = this.getGameById(role.getGameId());
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightApplyExitGameResponse(
                            FightApplyExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }

        String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

        // 1.距离上次拒绝时间到现在的间隔时间内不能连续发起申请退出
        // 2.有人在申请退出时不能发布自己的申请退出
        int deltaTime = 30;
        int nowTime = TimeUtils.getNowTime();

        // 是否允许申请退出
        try {
            if (!isAllowApplyExit(nowTime, game, gameRoleId, deltaTime)) {
                SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightApplyExitGameResponse(
                        FightApplyExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_EXITING.getNumber()))
                        .build());
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setFightApplyExitGameResponse(FightApplyExitGameResponse.newBuilder()).build());

        // 重置投票数据
        this.resetVoteData(game);
        // 设置申请退出的玩家id
        game.setApplyExitGameRoleId(gameRoleId);

        SC scApplyExit = SC.newBuilder().setSCFightApplyExitGame(SCFightApplyExitGame.newBuilder()
                .setName(role.getName()).setApplyExitId(game.getApplyExitId()).setCountDown(FightConstant.COUNTDOWN))
                .build();

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.roleId == role.getRoleId())
                continue;

            SessionUtils.sc(info.roleId, scApplyExit);
            // 发送玩家申请退出的通知
            notifyObservers(FightConstant.FIGHT_APPLY_LEAVE, game.getGameId(), info, scApplyExit);
        }

    }

    /**
     * 
     * @param game
     * @param roleGameInfo
     * @author wcy 2017年6月29日
     */
    private void aiAgreeExit(int gameId, String gameRoleId) {
        Game game = this.getGameById(gameId);
        if (game == null) {
            return;
        }
        this.voteApplyExit(game, gameRoleId, game.getApplyExitId(), FightVoteApplyExit.VOTE_AGREE);
    }

    /**
     * 
     * @param game
     * @author wcy 2017年6月29日
     */
    private void resetVoteData(Game game) {
        // 所有玩家表决状态重置
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            roleGameInfo.vote = FightVoteApplyExit.VOTE_IDLE;
        }

        game.getVoteMap().clear();
        game.setApplyExitGameRoleId(null);
    }

    /**
     * 自动生成申请退出标识符
     * 
     * @param game
     * @author wcy 2017年6月29日
     */
    private void generateApplyExitId(Game game) {
        game.setApplyExitId(game.getApplyExitId() + 1);
    }

    /**
     * 检查游戏是否从未开始过
     * 
     * @param game
     * @return
     * @author wcy 2017年6月29日
     */
    private boolean checkGameNeverStart(Game game) {
        GameState gameState = game.getGameState();
        GameConfig gameConfig = game.getGameConfig();
        int currentRound = game.getRound();
        int maxRound = gameConfig.getRound();
        return gameState == GameState.GAME_STATE_PREPARE && currentRound == maxRound;
    }

    /**
     * 检查是否允许申请退出
     * 
     * @param game
     * @param applyExitRoleGameId
     * @param deltaTime
     * @return
     * @author wcy 2017年6月29日
     */
    private boolean isAllowApplyExit(int nowTime, Game game, String applyExitRoleGameId, int deltaTime) {
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(applyExitRoleGameId);
        int lastRejectExitTime = roleGameInfo.lastRejectedExitTime;
        Role role = loginService.getRoleById(roleGameInfo.roleId);
        // 有人在申请退出时，不能让另一个人申请退出
        // 现在的时间与上次被拒绝的时间差不能小于规定间隔

        if (game.getGameType() == GameType.GAME_TYPE_FRIEND) {
            return true;
        }
        loggerinfo(role, "applyExitGameRoleId->" + game.getApplyExitGameRoleId());
        if (StringUtils.isNullOrEmpty(game.getApplyExitGameRoleId())) {
            loggerinfo(role,
                    "nowTime - lastRejectExitTime <= deltaTime" + nowTime + " " + lastRejectExitTime + " " + deltaTime);
            if (nowTime - lastRejectExitTime <= deltaTime) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * 同意退出
     */
    @Override
    public void agreeExit(Role role, FightVoteApplyExit vote, int voteId) {
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightAgreeExitGameResponse(
                            FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }

        String roleInfoStr = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build());

        this.voteApplyExit(game, roleInfoStr, voteId, vote);
    }

    /**
     * 当有人同意退出时，对游戏中退出条件进行判断，来决定是否结束游戏
     * 
     * @param game
     * @param voteGameRoleId
     * @param applyExitId
     * @param vote
     */
    private void voteApplyExit(Game game, String voteGameRoleId, int applyExitId, FightVoteApplyExit vote) {
        // 如果申请号不一样则直接返回不处理
        String applyExitGameRoleId = game.getApplyExitGameRoleId();
        int voteResult = FightConstant.GAME_IDLE;// 1.idle 2.over 3.continue

        if (applyExitId != game.getApplyExitId())
            return;
        synchronized (game) {
            if (game.getApplyExitId() != applyExitId)
                return;

            // 有人同意,则加入列表
            Map<String, FightVoteApplyExit> voteMap = game.getVoteMap();
            voteMap.put(voteGameRoleId, vote);
            // 检查人数是否满足,不满足查连接状态
            if (voteMap.size() == (game.getRoleIdMap().size() - 1)) {
                // 游戏结束
                voteResult = this.checkVoteContinueGame(game) ? FightConstant.GAME_CONTINUE : FightConstant.GAME_OVER;
                this.generateApplyExitId(game);
            } else {
                WAIT_VOTE: {
                    for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
                        // 申请人就跳过
                        if (roleGameInfo.gameRoleId.equals(applyExitGameRoleId)) {
                            continue;
                        }
                        // 投票中没有此人就检查连接,没断就返回
                        if (!game.getVoteMap().containsKey(roleGameInfo.gameRoleId)) {
                            IoSession session = SessionCache.getSessionById(roleGameInfo.roleId);
                            if (session == null || !session.isConnected()) {
                                voteMap.put(roleGameInfo.gameRoleId, FightVoteApplyExit.VOTE_AGREE);
                            } else {
                                break WAIT_VOTE;
                            }
                        }
                    }
                    voteResult = this.checkVoteContinueGame(game) ? FightConstant.GAME_CONTINUE
                            : FightConstant.GAME_OVER;
                    this.generateApplyExitId(game);
                }
            }

        }

        // 对投票结果进行处理
        if (voteResult == FightConstant.GAME_OVER || voteResult == FightConstant.GAME_CONTINUE) {
            SCFightApplyExitResult.Builder builder = SCFightApplyExitResult.newBuilder();
            for (Map.Entry<String, FightVoteApplyExit> entrySet : game.getVoteMap().entrySet()) {
                String key = entrySet.getKey();
                FightVoteApplyExit value = entrySet.getValue();
                RoleGameInfo roleGameInfo = game.getRoleIdMap().get(key);
                Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
                String name = role == null ? "ROBOT" + game.getRoleIdList().indexOf(key) : role.getName();
                if (value == FightVoteApplyExit.VOTE_AGREE) {
                    builder.addAgreeName(name);
                } else if (value == FightVoteApplyExit.VOTE_REJECT) {
                    builder.addRejectName(name);
                }
            }
            this.sendSC(game, SC.newBuilder().setSCFightApplyExitResult(builder).build());
            if (voteResult == FightConstant.GAME_OVER) {
                // 游戏结束
                this.cancelGame(game);
            } else if (voteResult == FightConstant.GAME_CONTINUE) {
                game.getRoleIdMap().get(game.getApplyExitGameRoleId()).lastRejectedExitTime = TimeUtils.getNowTime();
            }
            // 重置投票数据
            this.resetVoteData(game);
        }

    }

    /**
     * 检查投票是否可以继续比赛
     * 
     * @param game
     * @return
     * @author wcy 2017年6月30日
     */
    private boolean checkVoteContinueGame(Game game) {
        boolean continueGame = false;
        Map<String, FightVoteApplyExit> map = game.getVoteMap();
        for (FightVoteApplyExit resultVote : map.values()) {
            if (resultVote == FightVoteApplyExit.VOTE_REJECT) {
                continueGame = true;
                break;
            }
        }
        return continueGame;
    }

    /**
     * 取消游戏
     * 
     * @param game
     * @author wcy 2017年6月29日
     */
    private void cancelGame(Game game) {
        if (!game.isSaveData()) {
            Role masterRole = (Role) RoleCache.getRoleById(game.getMasterRoleId());
            // #####
            if (game.isUseCard()) {
                masterRole.setRoomCards(masterRole.getRoomCards() + game.getRoomCards());
            } else {
                moneyExchangeService.exchangeMoney(masterRole, game.getRandiooMoney(), false);
                masterRole.setRandiooMoney(masterRole.getRandiooMoney() + game.getRandiooMoney());
            }
        }
        game.setGameState(GameState.GAME_START_END);

        // 结算 本轮
        GameConfig gameConfig = game.getGameConfig();
        SCFightRoundOver.Builder builder = SCFightRoundOver.newBuilder();
        builder.setOver(true);
        builder.setDang(game.getMultiple());
        builder.setAll(gameConfig.getDi());
        builder.setBomb(game.getBomb());
        builder.setMingpai(game.isMingPaiState());
        builder.setMoguai(game.isMoGuai());

        // // builder.setSpring(spring);

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            Role role = (Role) RoleCache.getRoleById(info.roleId);
            String name = role == null ? "ROBOT" + info.gameRoleId : role.getName();
            int money = role == null ? 0 : role.getMoney();
            info.ready = role == null;
            info.callLandlordScore = -1;

            builder.addScore(Score.newBuilder().setName(name).setScore(info.currentMark).setAllScore(info.allMark)
                    .setSeated(game.getRoleIdList().indexOf(info.gameRoleId)).setMoney(money).addAllPai(info.cards));
        }
        SC scFightRoundOver = SC.newBuilder().setSCFightRoundOver(builder).build();
        // 设置当前游戏结束信息
        game.setCurrentStatusSC(scFightRoundOver);
        // 保存该轮游戏结束的信息
        game.getRoundoverList().add(scFightRoundOver);
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            info.scList2.add(scFightRoundOver);
            info.videoRoundPoint.add(info.scList2.size());
        }

        SCFightGameOver.Builder scFightGameOverBuilder = SCFightGameOver.newBuilder();
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            scFightGameOverBuilder.addGameOverData(parseGameOverDate(game, roleGameInfo));
        }

        SC gameOverSC = SC.newBuilder().setSCFightGameOver(scFightGameOverBuilder).build();
        this.sendSC(game, gameOverSC);
        // 记录游戏
        this.recordGame(game, gameOverSC);

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
            if (role != null) {
                role.setGameId(0);
            }
        }
        // 将游戏从缓存池中移除
        GameCache.getGameLocks().add(game.getLockString());
        if (!game.getLockString().equals("")) {
            GameCache.getGameLockStringMap().remove(game.getLockString());
        }
        GameCache.getGameMap().remove(game.getGameId());
    }

    /**
     * 生成游戏结束数据
     * 
     * @param game
     * @param info
     * @return
     */
    private GameOverData parseGameOverDate(Game game, RoleGameInfo info) {
        GameOverData.Builder gameOverBuilder = GameOverData.newBuilder();
        Role role = (Role) RoleCache.getRoleById(info.roleId);
        String name = role == null ? "ROBOT" + info.gameRoleId : role.getName();
        String headImgUrl = role == null ? "ui://h24q1ml0x7tz13m" : role.getHeadImgUrl();
        if (game.getLandlordGameRoleId() == null) {
            gameOverBuilder.setFarmerNum(info.farmerNum);
            gameOverBuilder.setLandLordNum(info.landLordNum);
        } else if (game.getLandlordGameRoleId().equals(info.gameRoleId)) {
            gameOverBuilder.setFarmerNum(info.farmerNum);
            gameOverBuilder.setLandLordNum(info.landLordNum - 1);
        } else {
            gameOverBuilder.setFarmerNum(info.farmerNum - 1);
            gameOverBuilder.setLandLordNum(info.landLordNum);
        }

        gameOverBuilder.setName(name);
        gameOverBuilder.setScore(info.allMark);
        gameOverBuilder.setSeated(game.getRoleIdList().indexOf(info.gameRoleId));
        gameOverBuilder.setHeadImgUrl(headImgUrl);
        gameOverBuilder.setRoleId(info.roleId);
        return gameOverBuilder.build();
    }

    /**
     * 记录游戏结果
     * 
     * @param game
     * @param gameOverSC
     */
    private void recordGame(Game game, SC gameOverSC) {
        ByteString gameOverSCByteString = gameOverSC.toByteString();
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.roleId == 0) {
                continue;
            }

            // 不在线的人记录一下游戏结果
            IoSession session = SessionCache.getSessionById(info.roleId);
            if (session == null || session.isClosing()) {
                Role role = (Role) RoleCache.getRoleById(info.roleId);
                if (role != null) {
                    role.setSc(gameOverSC);
                }

                continue;
            }

            info.videoRoundPoint.add(info.scList2.size());
            this.notifyObservers(FightConstant.FIGHT_GAME_OVER, info, gameOverSC);
            // 将所有的sc记录
            List<ByteString> list = new ArrayList<>(info.scList2.size());
            for (SC sc : info.scList2) {
                list.add(sc.toByteString());
            }
            List<ByteString> gameOverList = new ArrayList<>(game.getRoundoverList().size());
            for (SC sc : game.getRoundoverList()) {
                gameOverList.add(sc.toByteString());
            }
            video videodata = video.newBuilder().addAllVideoRecord(list).addAllKeyPoint(info.videoRoundPoint)
                    .addAllRoundOver(gameOverList).addAllStartTime(game.getStartTime())
                    .setGameOver(gameOverSCByteString).setGameType(game.getGameType().getNumber())
                    .setRoomId(game.getLockString().equals("") ? "0" : game.getLockString()).build();
            videoDAO.insert(VideoUtils.toVideo(info, videodata));

        }
    }

    /**
     * 发牌
     */
    // @Override
    // public void dispatchCard(int gameId) {
    // Game game = GameCache.getGameMap().get(gameId);
    //
    // int maxCount = game.getMaxRoleCount();
    // int needCard = 1;
    // int totalCardCount = (FightConstant.CARDS.length - maxCount) / maxCount;
    //
    // int[][] card = {
    // { 0x11, 0x21, 0x31, 0x41, 0x12, 0x22, 0x32, 0x42, 0x13, 0x23, 0x33, 0x43,
    // 0x14, 0x24, 0x34, 0x44,
    // 0x1D },
    // { 0x15, 0x25, 0x35, 0x49, 0x16, 0x26, 0x36, 0x4A, 0x17, 0x27, 0x3B, 0x4B,
    // 0x18, 0x28, 0x38, 0x48,
    // 0x4C },
    // { 0x19, 0x29, 0x39, 0x45, 0x1A, 0x2A, 0x3A, 0x46, 0x1B, 0x2B, 0x37, 0x47,
    // 0x1C, 0x2C, 0x3C, 0x0E,
    // 0x0F } };
    //
    // List<Integer> list = new ArrayList<>(FightConstant.CARDS.length);
    // boolean debug = false;
    // // 这if没啥用 ，可能调试时用
    // if (debug) {
    // for (int i = 0; i < 3; i++) {
    // String gameRoleId = game.getRoleIdList().get(i);
    // RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
    // for (int j = 0; j < card[i].length; j++) {
    // int c = card[i][j];
    // roleGameInfo.cards.add(c);
    // for (int x = list.size() - 1; x >= 0; x--) {
    // if (c == list.get(x)) {
    // list.remove(x);
    // break;
    // }
    // }
    // }
    // }
    // } else {
    // for (int j = 0; j < needCard; j++) {
    // // 先添加所有的牌,然后逐一随机拿走
    // for (int i = 0; i < FightConstant.CARDS.length; i++)
    // list.add(FightConstant.CARDS[i]);
    // // 给每个人发牌 ，算法有问题
    // // TODO
    // for (int i = 0; i < totalCardCount; i++) {
    // for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
    // int index = RandomUtils.getRandomNum(list.size());
    // int value = list.get(index);
    // list.remove(index);
    //
    // // // 如果符合条件,就从这个人开始叫地主
    // // // TODO 判定地主的方法有问题list在不断减少，索引不确定
    // // if (landlordCardBoxIndex == j && landlordCardIndex ==
    // // index) {
    // // // 如果明牌是大小王，则要翻倍
    // // if ((value == CardTools.C_KING || value ==
    // // CardTools.C_QUEUE)
    // // && (game.getGameConfig().getMoguai())) {
    // // game.setMoGuai(true);
    // // game.setMultiple(game.getMultiple() * 2);
    // // this.callFen(game);
    // // }
    // //
    // // // 设置开始叫地主的人的索引
    // //
    // game.setCurrentRoleIdIndex(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId));
    // // }
    //
    // roleGameInfo.cards.add(value);
    // }
    // }
    // }
    //
    // } // 随机开始叫地主的人
    // game.setCurrentRoleIdIndex(RandomUtils.getRandomNum(game.getRoleIdList().size()));
    //
    // // 对所有玩家的卡牌进行排序
    // for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values())
    // Collections.sort(roleGameInfo.cards, hexCardComparator);
    //
    // // 剩下的牌是地主牌
    // game.getLandlordCards().addAll(list);
    // /*
    // * for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()){
    // * for(int t :
    // * card[game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)])
    // * roleGameInfo.cards.add(t); } game.setCurrentRoleIdIndex(0); for(int t
    // * : landlord) game.getLandlordCards().add(t);
    // */
    // }

    @Override
    public void dispatchCard(int gameId) {
        Game game = this.getGameById(gameId);
        // 玩家数量
        int maxCount = game.getMaxRoleCount();
        // 使用的卡组数量
        int needCard = FightConstant.CARD_BOX_COUNT;
        // 每人手上卡牌的数量
        int everyPartCount = (FightConstant.CARDS.length - maxCount) / maxCount;

        List<Integer> totalCards = new ArrayList<>(FightConstant.CARDS.length * needCard);
        for (int i = 0; i < FightConstant.CARDS.length; i++) {
            for (int j = 0; j < needCard; j++) {
                List<Integer> landlordCards = game.getLandlordCards();
                landlordCards.add(FightConstant.CARDS[i]);
            }
        }
        // 获得发牌器
        Dispatcher dispatcher = randomDispatcher;
        // 如果是调试模式则换掉分牌器
        if (GlobleMap.Boolean(GlobleConstant.ARGS_DISPATCH)) {
            dispatcher = debugDispatcher;
        }

        // 分牌
        List<CardPart> cardParts = dispatcher.dispatch(totalCards, everyPartCount, maxCount);
        // 每个人加牌
        int i = 0;
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            roleGameInfo.cards.addAll(cardParts.get(i));
            // 卡组排序
            Collections.sort(roleGameInfo.cards, hexCardComparator);
            i++;
        }

        int landlordSeat = landlordJudger.getLandlord(game.getRoleIdList());
        // 设置地主的位置，这个人也就是开始出牌的人
        game.setCurrentRoleIdIndex(landlordSeat);
    }

    /**
     * 叫分
     */
    @Override
    public void callLandlord(Role role, int fen) {
        int gameId = role.getGameId();
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightCallLandLordResponse(
                            FightCallLandLordResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
        }
        String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setFightCallLandLordResponse(FightCallLandLordResponse.newBuilder()).build());
        // 叫地主
        this.callLandlord(gameId, gameRoleId, fen);

    }

    /**
     * 提示
     */
    @Override
    public GeneratedMessage recommandCardList(Role role) {
        int gameId = role.getGameId();
        if (role.getRoleId() != this.getCurrentRoleGameInfo(gameId).roleId) {
            return SC.newBuilder()
                    .setFightRecommandResponse(
                            FightRecommandResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
                    .build();

        }
        List<List<Integer>> list = this.getRecommandCardList(gameId);
        FightRecommandResponse.Builder builder = FightRecommandResponse.newBuilder();
        for (List<Integer> temp : list) {
            builder.addRecommandPai(RecommandPai.newBuilder().addAllPai(temp));
        }
        return SC.newBuilder().setFightRecommandResponse(builder).build();
    }

    /**
     * 获得推荐牌
     * 
     * @param gameId
     * @return
     * @author wcy 2017年6月2日
     */
    private List<List<Integer>> getRecommandCardList(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        CardList lastCardList = game.getLastCardList();
        RoleGameInfo roleGameInfo = this.getCurrentRoleGameInfo(gameId);

        CardSort cardSort = new CardSort();
        CardTools.fillCardSort(cardSort, roleGameInfo.cards);
        List<List<Integer>> recommandList = new ArrayList<>();
        // 先检查牌型，如果手牌成牌型，并且能出
        if (lastCardList == null) {
            for (CardList cardList : GameCache.getCardLists().values()) {
                CardList sendCardList = checkCardList(cardList, cardSort, roleGameInfo.cards);
                if (sendCardList != null
                        && (lastCardList == null || sendCardListBiggerThanLastCardList(sendCardList, lastCardList))) {
                    List<Integer> tList = new ArrayList<Integer>();
                    for (int t : roleGameInfo.cards) {
                        tList.add(CardTools.toNum(t));
                    }
                    recommandList.add(tList);
                    System.out.println(tList + sendCardList.getClass().getName());
                    return recommandList;
                }
            }
        }
        // 手牌不成牌型
        for (CardList cardList : GameCache.getRecommendCardLists().values()) {
            cardList.recommand(recommandList, cardSort, lastCardList, roleGameInfo.cards);
        }
        System.out.println(recommandList);
        return recommandList;

    }

    /**
     * 根据叫分 确定地主还重开
     */
    @Override
    public void callLandlord(int gameId, String gameRoleId, int callScore) {
        Game game = GameCache.getGameMap().get(gameId);
        System.out.print(gameRoleId + ":");
        // 检查是否是该人叫分
        int index = game.getRoleIdList().indexOf(gameRoleId);
        if (index != game.getCurrentRoleIdIndex() || game.getCallLandlordCount() == game.getMaxRoleCount()
                || game.getMultiple() == FightConstant.SCORE_3) {
            // 安全检查
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
            RoleInterface role = RoleCache.getRoleById(roleGameInfo.roleId);

            HttpLogUtils.role(role, "error");
            return;
        }
        game.getRoleIdMap().get(gameRoleId).callLandlordScore = callScore;
        int score = game.getMultiple();
        // 是否叫分
        boolean call = callScore != 0;
        if (call) {
            // 叫的分数不能比上一次低
            if (callScore < score) {
                System.out.println("叫分必须比上一个人高");
                return;
            }
            System.out.println("叫分:" + callScore);

            // 设置叫的分数
            game.setMultiple(callScore);
            this.callFen(game);
            game.setLandlordGameRoleId(gameRoleId);

            // 检查是不是叫了3分,如果叫了三分就是地主,加牌开始比赛
            if (game.getMultiple() == FightConstant.SCORE_3) {
                // 开始比赛
                this.confirmLandlord(game);
                return;
            }
        } else {
            System.out.println("不叫");
        }

        // 叫地主计数器加1
        int callCount = game.getCallLandlordCount();
        callCount++;
        game.setCallLandlordCount(callCount);
        // 检查是否每个人都叫过了
        if (callCount >= game.getMaxRoleCount()) {
            // 如果人数到了,则那最后一个人的叫分,如果没有人叫分,则重新发牌
            int resultScore = game.getMultiple();
            if (resultScore == 0) {
                // 说明没有人叫分，重新发牌
                System.out.println("没人叫地主，重新发牌");
                // 游戏数据初始化
                gameInit.init(game);
                // 发牌
                this.dispatchCard(gameId);
                // 叫分
                this.callFen(game);
                SCFightStart.Builder fightStartBuilder = SCFightStart.newBuilder();
                for (int i = 0; i < game.getMaxRoleCount(); i++) {
                    fightStartBuilder.addPaiNum(PaiNum.newBuilder().setSeated(i)
                            .setNum(game.getRoleIdMap().get(game.getRoleIdList().get(i)).cards.size()));

                }
                fightStartBuilder.setRoundNum(game.getRound());
                SC sc = SC.newBuilder()
                        .setSCFightCallLandLord(SCFightCallLandLord.newBuilder().setCurrentFen(0)
                                .setSeated(game.getCurrentRoleIdIndex()).setCountdown(FightConstant.COUNTDOWN / 2)
                                .setFen(-1))
                        .build();
                game.setCurrentStatusSC(sc);
                for (RoleGameInfo info : game.getRoleIdMap().values()) {
                    System.out.println("~~~" + info.roleId + game.getMultiple());
                    SessionUtils.sc(info.roleId,
                            SC.newBuilder().setSCFightStart(fightStartBuilder.clone().addAllPai(info.cards)).build());
                    // 所有的pai 记录到录像
                    for (RoleGameInfo info2 : game.getRoleIdMap().values()) {
                        this.notifyObservers(FightConstant.FIGHT_RECORD, info2,
                                SC.newBuilder()
                                        .setSCFightMingPai(SCFightMingPai.newBuilder().addAllPai(info.cards)
                                                .setSeated(game.getRoleIdList().indexOf(info.gameRoleId)).setIsFirst(1))
                                        .build(),
                                2);
                    }

                    SessionUtils.sc(info.roleId, sc);
                    this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());
                }
                this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
                return;
            } else {
                this.confirmLandlord(game);
            }
        } else {
            // 如果人数没有到,则通知下一个人
            int nextIndex = this.getNextIndex(gameId);
            String nextRoleGameId = game.getRoleIdList().get(nextIndex);
            game.setCallLandlordCount(callCount);
            SC sc = SC.newBuilder()
                    .setSCFightCallLandLord(SCFightCallLandLord.newBuilder().setCurrentFen(game.getMultiple())
                            .setSeated(nextIndex).setCountdown(FightConstant.COUNTDOWN / 2).setFen(callScore))
                    .build();
            game.setCurrentStatusSC(sc);
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SessionUtils.sc(info.roleId, sc);
                this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc, 1);
            }
            this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
        }
    }

    /**
     * 确定地主后 ，给地主发牌等操作
     * 
     * @param game
     */
    private void confirmLandlord(Game game) {
        int gameId = game.getGameId();
        // 看叫分最高的是谁
        loggerinfo("game=>" + game.getGameId() + "地主是:" + game.getLandlordGameRoleId());
        // 给地主牌
        this.giveLandlordCards(gameId);

        game.setCurrentRoleIdIndex(game.getRoleIdList().indexOf(game.getLandlordGameRoleId()));
        SCFightLandLord scFightLandLord = SCFightLandLord.newBuilder().addAllLandLordPai(game.getLandlordCards())
                .setSeated(game.getRoleIdList().indexOf(game.getLandlordGameRoleId())).build();

        SCFightPutOut scFightPutOut = createSCFightPutOut(game, false);

        // 出牌通知
        SC sc = SC.newBuilder().setSCFightPutOut(scFightPutOut).build();

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (!info.gameRoleId.equals(game.getLandlordGameRoleId())) {
                info.farmerNum++;
                SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightLandLord(scFightLandLord).build());
                this.notifyObservers(FightConstant.FIGHT_RECORD, info,
                        SC.newBuilder().setSCFightLandLord(scFightLandLord).build(), 1);
            } else {
                info.landLordNum++;
                SessionUtils.sc(info.roleId,
                        SC.newBuilder().setSCFightLandLord(
                                scFightLandLord.toBuilder().addAllPai(game.getRoleIdMap().get(info.gameRoleId).cards))
                                .build());
                this.notifyObservers(FightConstant.FIGHT_RECORD, info,
                        SC.newBuilder().setSCFightLandLord(
                                scFightLandLord.toBuilder().addAllPai(game.getRoleIdMap().get(info.gameRoleId).cards))
                                .build(),
                        1);
                this.aiMingPai(game, info.roleId);
                // 所有的牌
                for (RoleGameInfo info2 : game.getRoleIdMap().values()) {
                    this.notifyObservers(FightConstant.FIGHT_RECORD, info2,
                            SC.newBuilder()
                                    .setSCFightMingPai(SCFightMingPai.newBuilder().addAllPai(info.cards)
                                            .setSeated(game.getRoleIdList().indexOf(info.gameRoleId)).setIsFirst(1))
                                    .build(),
                            2);// setIsFirst
                               // 为1
                               // 表示
                               // 不是第一次
                }
            }
            // 如果配置表中 选择不明牌 通知出牌
            if (!game.getGameConfig().getMingpai()) {
                game.setCurrentStatusSC(sc);
                SessionUtils.sc(info.roleId, sc);
                this.notifyObservers(FightConstant.FIGHT_OUTPUT, info, sc);
            }
        }
        // 根据配置表中明牌选项，决定是否弹出 明牌按钮
        if (game.getGameConfig().getMingpai()) {
            SC scFightMingPaiOnOrOff = SC.newBuilder()
                    .setSCFightMingPaiOnOrOff(SCFightMingPaiOnOrOff.newBuilder().setIsOpen(true)).build();
            RoleGameInfo landlordGameInfo = game.getRoleIdMap().get(game.getLandlordGameRoleId());
            SessionUtils.sc(landlordGameInfo.roleId, scFightMingPaiOnOrOff);
            this.notifyObservers(FightConstant.FIGHT_MINGPAI_ONOROFF, landlordGameInfo, scFightMingPaiOnOrOff);
        } else {
            SC scFightMingPaiOnOrOff = SC.newBuilder()
                    .setSCFightMingPaiOnOrOff(SCFightMingPaiOnOrOff.newBuilder().setIsOpen(false)).build();
            RoleGameInfo landlordGameInfo = game.getRoleIdMap().get(game.getLandlordGameRoleId());
            SessionUtils.sc(landlordGameInfo.roleId, scFightMingPaiOnOrOff);
            this.notifyObservers(FightConstant.FIGHT_MINGPAI_ONOROFF, landlordGameInfo, scFightMingPaiOnOrOff);
            notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
        }

    }

    // 生成SCFightPutOut
    private SCFightPutOut createSCFightPutOut(Game game, boolean isAllowGuo) {
        int currentRoleIdIndex = game.getCurrentRoleIdIndex();
        String gameRoleId = game.getRoleIdList().get(currentRoleIdIndex);
        int auto = game.getRoleIdMap().get(gameRoleId).auto;
        int countDown = auto < 2 ? FightConstant.COUNTDOWN : 1;
        SCFightPutOut scFightPutOut = SCFightPutOut.newBuilder().setCountdown(countDown).setSeated(currentRoleIdIndex)
                .setAllowGuo(isAllowGuo).build();
        return scFightPutOut;
    }

    private void aiMingPai(Game game, int roleId) {
        if (roleId == 0) {
            game.setMultiple(game.getMultiple() * 2);
            this.callFen(game);
            game.setMingPaiState(true);
            String landlordGameRoleId = game.getLandlordGameRoleId();
            RoleGameInfo LandLord = game.getRoleIdMap().get(landlordGameRoleId);

            SCFightMingPai scightMingPai = SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
                    .setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId)).build();// setIsFirst为1表示不是第一次

            SC sc = SC.newBuilder().setSCFightMingPai(scightMingPai).build();
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SessionUtils.sc(info.roleId, sc);
                this.notifyObservers(FightConstant.FIGHT_MINGPAI, info, sc);
            }
        }
    }

    /**
     * 通知场上底分
     * 
     * @param game
     * @author wcy 2017年7月3日
     */
    private void callFen(Game game) {
        Map<String, RoleGameInfo> roleGameInfoMap = game.getRoleIdMap();
        SC sc = SC.newBuilder().setSCFightCallFen(SCFightCallFen.newBuilder().setFen(game.getMultiple())).build();
        for (RoleGameInfo roleGameInfo : roleGameInfoMap.values()) {
            SessionUtils.sc(roleGameInfo.roleId, sc);
            this.notifyObservers(FightConstant.FIGHT_NOTICE_FEN, roleGameInfo, sc);
        }
    }

    /**
     * 给地主牌
     * 
     * @param gameId
     * @author wcy 2017年6月1日
     */
    private void giveLandlordCards(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        String landlordId = game.getLandlordGameRoleId();
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(landlordId);
        roleGameInfo.cards.addAll(game.getLandlordCards());

        List<Integer> list = game.getLandlordCards();
        for (int card : list) {
            if (card == CardTools.C_KING || card == CardTools.C_QUEUE) {
                game.setMoGuai(true);
                game.setMultiple(game.getMultiple() * 2);
                callFen(game);
                break;
            }
        }
    }

    /**
     * 出牌请求
     */
    @Override
    public void sendCard(Role role, List<Integer> paiList) {
        int gameId = role.getGameId();
        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightSendCardResponse(
                            FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }
        String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        if (roleGameInfo.roleId != role.getRoleId()) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightSendCardResponse(
                            FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
                            .build());
            return;
        }
        roleGameInfo.auto = 0;
        if (game.getGameType() == GameType.GAME_TYPE_MATCH) {
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SessionUtils.sc(info.roleId, SC.newBuilder()
                        .setSCFightAuto(SCFightAuto.newBuilder()
                                .setSeated(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)).setAuto(false))
                        .build());
            }
        }
        this.gameRoleIdSendCard(paiList, gameId, gameRoleId);
    }

    /**
     * 对出牌进行处理 ；判断牌是否正常，是否能打出
     * 
     * @param paiList
     * @param gameId
     * @param gameRoleId
     */
    private void gameRoleIdSendCard(List<Integer> paiList, int gameId, String gameRoleId) {
        Game game = GameCache.getGameMap().get(gameId);
        int roleId = game.getRoleIdMap().get(gameRoleId).roleId;

        CardList lastCardList = game.getLastCardList();

        CardList sendCardList = null;

        // 如果长度是0，说明不出牌，则要检查是否允许不出牌
        if (paiList.size() == 0) {
            // 如果过的次数大于等于总人数-1，就一定要出牌
            if (game.getPassCount() >= (game.getMaxRoleCount() - 1)) {
                // 不允许不出牌
                SessionUtils.sc(roleId,
                        SC.newBuilder().setFightSendCardResponse(
                                FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.NULL_REJECT.getNumber()))
                                .build());
                return;
            } else {
                // 否则允许不出牌
                game.setPassCount(game.getPassCount() + 1);
            }
        } else {
            // 结构化卡牌，用于卡牌识别
            CardSort cardSort = new CardSort();
            CardTools.fillCardSort(cardSort, paiList);

            // 如果没有先前的牌型，则为主动出牌,直接判断牌型<br>否则通过使用上一轮牌型判断
            sendCardList = lastCardList == null ? initiativeSend(cardSort, paiList)
                    : passiveSend(lastCardList.getClass(), cardSort, paiList);

            // 匹配牌型失败
            if (sendCardList == null) {
                SessionUtils.sc(roleId,
                        SC.newBuilder().setFightSendCardResponse(
                                FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.NOT_SAME_TYPE.getNumber()))
                                .build());
                return;
            }

            // 比较大小
            if (lastCardList != null) {
                try {
                    boolean bigger = sendCardListBiggerThanLastCardList(sendCardList, lastCardList);
                    if (!bigger) {
                        SessionUtils.sc(roleId,
                                SC.newBuilder().setFightSendCardResponse(
                                        FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.SMALLER.getNumber()))
                                        .build());
                        return;
                    }
                } catch (CardTypeComparableException e) {
                    SessionUtils.sc(roleId, SC.newBuilder().setFightSendCardResponse(
                            FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.NOT_SAME_TYPE.getNumber()))
                            .build());
                    return;
                }
            }
        }
        // 设置最后一个人的牌型
        sendCard(paiList, gameId, gameRoleId, sendCardList);

        SessionUtils.sc(roleId, SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());
        /**
         * 通知已经出牌
         */

        SCFightPutOutPai.Builder SCPutOutBuilder = SCFightPutOutPai.newBuilder().addAllPutOutPai(paiList)
                .setSeated(game.getCurrentRoleIdIndex())
                .setType(sendCardList == null ? "guo" : sendCardList.getClass().getSimpleName());
        for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {

            SCPutOutBuilder.addPaiNum(PaiNum.newBuilder().setSeated(game.getRoleIdList().indexOf(tInfo.gameRoleId))
                    .setNum(tInfo.cards.size()));

        }
        SCFightPutOutPai sCFightPutOutPai = SCPutOutBuilder.build();
        for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
            SessionUtils.sc(tInfo.roleId, SC.newBuilder().setSCFightPutOutPai(sCFightPutOutPai).build());
            this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo,
                    SC.newBuilder().setSCFightPutOutPai(sCFightPutOutPai).build(), 1);
            if (gameRoleId.equals(game.getLandlordGameRoleId()) && game.isMingPaiState()) {
                RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
                SC sc = SC.newBuilder().setSCFightMingPai(SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
                        .setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId)).setIsFirst(1)).build(); // setIsFirst
                                                                                                              // 为1
                                                                                                              // 表示
                                                                                                              // 不是第一次
                SessionUtils.sc(tInfo.roleId, sc);
                this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo, sc, 1);
            }
            if (game.getRecords().get(game.getRecords().size() - 1).size() == 3) {
                SessionUtils.sc(tInfo.roleId,
                        SC.newBuilder().setSCFightLastRoundReady(SCFightLastRoundReady.newBuilder()).build());
                this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo,
                        SC.newBuilder().setSCFightLastRoundReady(SCFightLastRoundReady.newBuilder()).build(), 1);
            }
        }

        for (RoleGameInfo tInfo2 : game.getRoleIdMap().values()) {
            RoleGameInfo roleInfo = game.getRoleIdMap().get(gameRoleId);
            SC sc = SC.newBuilder().setSCFightMingPai(SCFightMingPai.newBuilder().addAllPai(roleInfo.cards)
                    .setSeated(game.getRoleIdList().indexOf(roleInfo.gameRoleId)).setIsFirst(1)).build();
            this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo2, sc, 2);
        }
        notifyObservers(FightConstant.SEND_CARD, gameId, gameRoleId, sendCardList);

        // 检查游戏是否结束
        if (!this.checkGameOver(game)) {
            // 如果没有结束，则请求下一个人出牌
            // 将索引换到下一个人
            getNextIndex(gameId);

            game.setSendCardCount(game.getSendCardCount() + 1);
            game.setSendCardTime(TimeUtils.getNowTime());

            // 下一个人是否可以过
            boolean allowGuo = game.getPassCount() < (game.getMaxRoleCount() - 1);

            SCFightPutOut scFightPutOut = createSCFightPutOut(game, allowGuo);

            SC sc = SC.newBuilder().setSCFightPutOut(scFightPutOut).build();

            game.setCurrentStatusSC(sc);
            for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
                SessionUtils.sc(tInfo.roleId, sc);
                this.notifyObservers(FightConstant.FIGHT_OUTPUT, tInfo, sc);
            }

            notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
        }
    }

    /**
     * 把打出的牌记录
     * 
     * @param paiList
     * @param gameId
     * @param gameRoleId
     * @param sendCardList
     */
    public void sendCard(List<Integer> paiList, int gameId, String gameRoleId, CardList sendCardList) {

        Game game = GameCache.getGameMap().get(gameId);
        synchronized (game) {
            CardRecord record = new CardRecord();
            record.cards = paiList;
            record.gameRoleId = gameRoleId;
            game.getRecords().get(game.getRecords().size() - 1).add(record);
            // 如果出牌了，则放弃出牌的计数器重置
            if (sendCardList != null) {
                game.setPassCount(0);
                if (sendCardList.getClass() == A4.class || sendCardList.getClass() == KQ.class) {
                    game.setBomb(game.getBomb() + 1);
                    game.setMultiple(game.getMultiple() * 2);
                    this.callFen(game);
                }
                if (game.isLandLordSpring() && !gameRoleId.equals(game.getLandlordGameRoleId())) {
                    System.out.println("dizhuSpring!~");
                    game.setLandLordSpring(false);
                }
                if (game.getRecords().get(game.getRecords().size() - 1).size() > 1
                        && gameRoleId.equals(game.getLandlordGameRoleId())) {
                    game.setFarmerSpring(false);
                    System.out.println("farmerSpring!~");
                }
                game.setLastCardList(sendCardList);
            } else {
                if (game.getPassCount() >= game.getMaxRoleCount() - 1) {
                    game.setLastCardList(null);
                }
            }
            // 从手牌中移除该牌
            RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
            for (int pai : paiList) {
                int index = info.cards.indexOf(pai);
                System.out.println(info.cards + "~~~" + pai + "---" + index);
                info.cards.remove(index);
            }
        }
    }

    private Game getGameById(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        return game;
    }

    /**
     * 一局（round,一次结算）游戏结束
     * 
     * @param game
     * @param gameRoleId
     */
    private void gameOver(Game game, String gameRoleId) {
        boolean spring = this.isSpring(game, gameRoleId);
        if (spring) {
            game.setMultiple(game.getMultiple() * 2);
            callFen(game);
        }
        SC springSC = SC.newBuilder().setSCFightSpring(SCFightSpring.newBuilder().setIsSpring(spring).build()).build();
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId, springSC);
            this.notifyObservers(FightConstant.FIGHT_SPRING, info, springSC);
        }
        // 结算分数
        this.saveScore(game, gameRoleId);
        game.setSaveData(true);
        GameConfig gameConfig = game.getGameConfig();

        game.setGameState(GameState.GAME_STATE_PREPARE);
        SCFightRoundOver.Builder builder = SCFightRoundOver.newBuilder();
        builder.setOver(game.getRound() == 0);
        builder.setDang(game.getMultiple());
        builder.setAll(gameConfig.getDi());
        builder.setBomb(game.getBomb());
        builder.setMingpai(game.isMingPaiState());
        builder.setMoguai(game.isMoGuai());

        loggerinfo("game=>" + game.getGameId() + " dizhuWin:" + gameRoleId.equals(game.getLandlordGameRoleId())
                + "-dizhuSpring:" + game.isLandLordSpring() + game.isFarmerSpring());
        builder.setSpring(spring);

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            Role role = (Role) RoleCache.getRoleById(info.roleId);
            String name = role == null ? "ROBOT" + info.gameRoleId : role.getName();
            int money = role == null ? 0 : role.getMoney();
            info.ready = role == null;
            info.callLandlordScore = -1;

            builder.addScore(Score.newBuilder().setName(name).setScore(info.currentMark).setAllScore(info.allMark)
                    .setSeated(game.getRoleIdList().indexOf(info.gameRoleId)).setMoney(money).addAllPai(info.cards));
        }
        SC scFightRoundOver = SC.newBuilder().setSCFightRoundOver(builder).build();
        // 设置当前游戏结束信息
        game.setCurrentStatusSC(scFightRoundOver);
        // 保存该轮游戏结束的信息
        game.getRoundoverList().add(scFightRoundOver);
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            // SessionUtils.sc(info.roleId, scFightRoundOver);
            this.notifyObservers(FightConstant.FIGHT_ROUND_OVER, info, scFightRoundOver);
            info.videoRoundPoint.add(info.scList2.size());
        }

        RoundOverTimeEvent roundOverTimeEvent = new RoundOverTimeEvent() {
            public void update(TimeEvent timeEvent) {
                roundOverTime((RoundOverTimeEvent) timeEvent);
            }
        };
        roundOverTimeEvent.setGame(game);
        roundOverTimeEvent.setSc(scFightRoundOver);
        roundOverTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.ROUNDOVER_WAIT_TIME);
        eventScheduler.addEvent(roundOverTimeEvent);

        if (game.getRound() == 0) {

            SCFightGameOver.Builder SCGameOverBuilder = SCFightGameOver.newBuilder();
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                GameOverData.Builder gameOverBuilder = GameOverData.newBuilder();
                Role role = (Role) RoleCache.getRoleById(info.roleId);
                String name = role == null ? "ROBOT" + info.gameRoleId : role.getName();
                String headImgUrl = role == null ? "ui://h24q1ml0x7tz13m" : role.getHeadImgUrl();

                gameOverBuilder.setFarmerNum(info.farmerNum);
                gameOverBuilder.setLandLordNum(info.landLordNum);
                gameOverBuilder.setName(name);
                gameOverBuilder.setScore(info.allMark);
                gameOverBuilder.setSeated(game.getRoleIdList().indexOf(info.gameRoleId));
                gameOverBuilder.setHeadImgUrl(headImgUrl);
                gameOverBuilder.setRoleId(info.roleId);
                SCGameOverBuilder.addGameOverData(gameOverBuilder);
            }
            SC gameOverSC = SC.newBuilder().setSCFightGameOver(SCGameOverBuilder).build();
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                // 如果是机器人直接返回
                if (info.roleId == 0) {
                    continue;
                }

                IoSession session = SessionCache.getSessionById(info.roleId);

                Role role = (Role) RoleCache.getRoleById(info.roleId);
                if (session == null || session.isClosing()) {
                    role.setSc(gameOverSC);
                } else {
                    // SessionUtils.sc(info.roleId, gameOverSC);
                    GameOverTimeEvent gamedOverTimeEvent = new GameOverTimeEvent() {
                        public void update(TimeEvent timeEvent) {
                            gameOverTime((GameOverTimeEvent) timeEvent);
                        }
                    };
                    gamedOverTimeEvent.setSc(gameOverSC);
                    gamedOverTimeEvent.setRoleId(info.roleId);
                    gamedOverTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.ROUNDOVER_WAIT_TIME);
                    eventScheduler.addEvent(gamedOverTimeEvent);
                }

                this.notifyObservers(FightConstant.FIGHT_GAME_OVER, info, gameOverSC);

                List<ByteString> list = new ArrayList<>(info.scList2.size());
                for (SC tempSc : info.scList2) {
                    list.add(tempSc.toByteString());
                }
                List<ByteString> gameOverList = new ArrayList<>(game.getRoundoverList().size());
                for (SC tempSc : game.getRoundoverList()) {
                    gameOverList.add(tempSc.toByteString());
                }
                video videodata = video.newBuilder().addAllVideoRecord(list).addAllKeyPoint(info.videoRoundPoint)
                        .addAllRoundOver(gameOverList).addAllStartTime(game.getStartTime())
                        .setGameOver(gameOverSC.toByteString()).setGameType(game.getGameType().getNumber())
                        .setRoomId(game.getLockString().equals("") ? "0" : game.getLockString()).build();

                VideoData videoData = VideoUtils.toVideo(info, videodata);
                gameDB.getInsertPool().execute(new EntityRunnable<VideoData>(videoData) {

                    @Override
                    public void run(VideoData entity) {
                        videoDAO.insert(entity);
                    }
                });

            }

            this.addActive(game); // 添加活跃度

        }

    }

    /**
     * 添加活跃度
     * 
     * @param game
     */
    private void addActive(Game game) {
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
            try {
                randiooPlatformSdk.addActive(role.getAccount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 一次结算结果通知
     * 
     * @param roundOverTimeEvent
     */
    private void roundOverTime(RoundOverTimeEvent roundOverTimeEvent) {
        Game game = roundOverTimeEvent.getGame();
        SC sc = roundOverTimeEvent.getSc();
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId, sc);
        }
    }

    /**
     * 二次结算通知
     * 
     * @param gameOverTimeEvent
     */
    private void gameOverTime(GameOverTimeEvent gameOverTimeEvent) {
        SC sc = gameOverTimeEvent.getSc();
        int roleId = gameOverTimeEvent.getRoleId();
        SessionUtils.sc(roleId, sc);
    }

    /**
     * 返回大厅
     */
    @Override
    public void backMenu(Role role) {
        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setFightBackMenuResponse(FightBackMenuResponse.newBuilder()).build());
        int gameId = role.getGameId();
        Game game = this.getGameById(gameId);
        if (game == null || role.getGameId() != game.getGameId())
            return;
        synchronized (game.getBackMenuSet()) {
            game = this.getGameById(gameId);
            if (game == null || role.getGameId() != game.getGameId())
                return;

            game.getBackMenuSet().add(role.getRoleId());

            role.setGameId(0);
            if (game.getBackMenuSet().size() < getRealRoleCount(game))
                return;

            // 如果按返回菜单的人数满了,则删除游戏
            GameCache.getGameLocks().add(game.getLockString());
            if (!game.getLockString().equals("")) {
                GameCache.getGameLockStringMap().remove(game.getLockString());
            }
            GameCache.getGameMap().remove(game.getGameId());
        }
    }

    /**
     * 获得真实玩家的数量
     * 
     * @param game
     * @return
     * @author wcy 2017年7月7日
     */
    private int getRealRoleCount(Game game) {
        int count = 0;
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.roleId == 0)
                continue;
            count++;
        }
        return count;
    }

    /**
     * 保存分数
     * 
     * @param game
     * @param gameRoleId
     */
    private void saveScore(Game game, String gameRoleId) {
        GameConfig gameConfig = game.getGameConfig();
        int difen = gameConfig.getDi();
        int multiple = game.getMultiple();

        boolean landlordWin = gameRoleId.equals(game.getLandlordGameRoleId());

        loggerinfo("game=>" + game.getGameId() + " winGameRoleId=>" + gameRoleId);
        int totalFen = difen * multiple;
        if (game.getGameType() == GameType.GAME_TYPE_MATCH) {
            this.matchCalculateScore(game, landlordWin ? totalFen * 2 : -totalFen * 2,
                    landlordWin ? -totalFen : totalFen);
        } else {
            this.createRoomCalculateScore(game, landlordWin ? totalFen * 2 : -totalFen * 2,
                    landlordWin ? -totalFen : totalFen);
        }
    }

    /**
     * 结算房间结算
     * 
     * @param game
     * @param landlordFen
     * @param farmFen
     * @author wcy 2017年7月5日
     */
    private void matchCalculateScore(Game game, int landlordFen, int farmFen) {
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            Role role = (Role) RoleCache.getRoleById(info.roleId);
            if (role == null) {
                continue;
            }
            int money = role.getMoney();

            int targetFen = info.gameRoleId.equals(game.getLandlordGameRoleId()) ? landlordFen : farmFen;
            info.currentMark = targetFen;
            info.allMark += targetFen;
            if (targetFen < 0) {
                role.setMoney(money + (money < Math.abs(targetFen) ? -money : targetFen));
            } else {
                role.setMoney(money + (money < targetFen ? money : targetFen));
            }

        }
    }

    /**
     * 创建房间结算
     * 
     * @param game
     * @param landlordFen
     * @param farmFen
     * @author wcy 2017年7月5日
     */
    private void createRoomCalculateScore(Game game, int landlordFen, int farmFen) {
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            int targetFen = info.gameRoleId.equals(game.getLandlordGameRoleId()) ? landlordFen : farmFen;
            info.currentMark = targetFen;
            info.allMark += targetFen;
        }
    }

    /**
     * 检查是否是春天
     * 
     * @param game
     * @param gameRoleId
     * @return
     * @author wcy 2017年7月5日
     */
    private boolean isSpring(Game game, String gameRoleId) {
        if (gameRoleId.equals(game.getLandlordGameRoleId())) {
            return game.isLandLordSpring();
        } else {
            return game.isFarmerSpring();
        }
    }

    private void checkAutoAI(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);

        // 发送等待消息
        RoleGameInfo info = getCurrentRoleGameInfo(gameId);
        if (info.auto >= 2) {
            SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

                @Override
                public void update(TimeEvent timeEvent) {
                    timeUp((SendCardTimeEvent) timeEvent);
                }
            };

            sendCardTimeEvent.setRound(game.getRecords().get(game.getRecords().size() - 1).size());
            sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + 1);
            sendCardTimeEvent.setGameId(gameId);
            sendCardTimeEvent.setFirst(false);
            sendCardTimeEvent.setMustPutOut(true);

            eventScheduler.addEvent(sendCardTimeEvent);
            return;
        }
        if (game.getGameType() == GameType.GAME_TYPE_FRIEND && info.roleId != 0) {
            return;
        }

        int waitTime = info.roleId == 0 ? 2 : FightConstant.SEND_CARD_WAIT_TIME;
        SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

            @Override
            public void update(TimeEvent timeEvent) {
                timeUp((SendCardTimeEvent) timeEvent);
            }
        };

        sendCardTimeEvent.setRound(game.getRecords().get(game.getRecords().size() - 1).size());
        sendCardTimeEvent.setEndTime(TimeUtils.getNowTime()
                + waitTime/*
                           * FightConstant . SEND_CARD_WAIT_TIME
                           */);
        sendCardTimeEvent.setGameId(gameId);
        sendCardTimeEvent.setFirst(true);
        sendCardTimeEvent.setMustPutOut(false);

        eventScheduler.addEvent(sendCardTimeEvent);
    }

    private void timeUp(SendCardTimeEvent event) {
        int gameId = event.getGameId();
        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            return;
        }
        // 如果出牌数已经改变,或者游戏已经结束,则直接返回
        String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
        if (game.getRecords().get(game.getRecords().size() - 1).size() != event.getRound())
            return;
        if (game.getRoleIdMap().get(gameRoleId).online || !event.isFirst()) {
            if (game.getRoleIdMap().get(gameRoleId).online && !event.isFirst() && !event.isMustPutOut()) {
                SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

                    @Override
                    public void update(TimeEvent timeEvent) {
                        timeUp((SendCardTimeEvent) timeEvent);
                    }
                };

                sendCardTimeEvent.setRound(game.getRecords().get(game.getRecords().size() - 1).size());

                sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.SEND_CARD_WAIT_TIME);
                sendCardTimeEvent.setGameId(gameId);
                sendCardTimeEvent.setFirst(false);
                sendCardTimeEvent.setMustPutOut(true);
                eventScheduler.addEvent(sendCardTimeEvent);
                return;
            }
            this.autoSendCard(gameId, gameRoleId);
        } else {
            SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

                @Override
                public void update(TimeEvent timeEvent) {
                    timeUp((SendCardTimeEvent) timeEvent);
                }
            };

            sendCardTimeEvent.setRound(game.getRecords().get(game.getRecords().size() - 1).size());
            sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.SEND_CARD_WAIT_TIME * 2);
            sendCardTimeEvent.setGameId(gameId);
            sendCardTimeEvent.setFirst(false);
            sendCardTimeEvent.setMustPutOut(false);
            eventScheduler.addEvent(sendCardTimeEvent);
            return;
        }

        System.out.println("time up");
    }

    private void timeUp(CallLandLordTimeEvent event) {
        int gameId = event.getGameId();
        int t = event.getScore();
        int currentScore = GameCache.getGameMap().get(gameId).getMultiple();
        callLandlord(gameId, event.getGameRoleId(), t > currentScore ? t : 0);
    }

    /**
     * 断线处理
     */
    @Override
    public void disconnectTimeUp(int roleId) {
        DisconnectTimeEvent disconnectTimeEvent = new DisconnectTimeEvent() {

            @Override
            public void update(TimeEvent timeEvent) {
                int roleId = ((DisconnectTimeEvent) timeEvent).getRoleId();
                if (SessionCache.getSessionById(roleId) == null || SessionCache.getSessionById(roleId).isClosing()) {
                    Role role = (Role) RoleCache.getRoleMap().get(roleId);
                    Game game = GameCache.getGameMap().get(role.getGameId());
                    if (game != null && game.getGameState() == GameState.GAME_STATE_PREPARE) {
                        exitGame(role);
                    }
                }
            }
        };
        disconnectTimeEvent.setEndTime(TimeUtils.getNowTime()
                + 120/*
                      * FightConstant . SEND_CARD_WAIT_TIME
                      */);
        disconnectTimeEvent.setRoleId(roleId);

        eventScheduler.addEvent(disconnectTimeEvent);

    }

    /**
     * 自动出牌
     * 
     * @param gameId
     * @param gameRoleId
     * @author wcy 2017年6月2日
     */
    private void autoSendCard(int gameId, String gameRoleId) {
        // 否则进行自动出牌
        List<Integer> paiList = this.getAutoPaiList(gameId);
        List<Integer> cards = new ArrayList<>();
        List<Integer> putOut = new ArrayList<>();
        if (paiList != null) {
            cards.addAll(GameCache.getGameMap().get(gameId).getRoleIdMap().get(gameRoleId).cards);
            for (int pai : paiList) {
                int loc = -1;
                if (pai != 0xE && pai != 0xF) {
                    for (int i = 1; i <= 4; i++) {
                        loc = cards.indexOf(pai + 16 * i);
                        if (loc > -1) {
                            break;
                        }
                    }
                } else {
                    loc = cards.indexOf(pai);
                }
                /*
                 * if(loc == -1){ this.gameRoleIdSendCard(new
                 * ArrayList<Integer>(1), gameId, gameRoleId); return; }
                 */
                putOut.add(cards.remove(loc));
            }
        }
        /* this.gameRoleIdSendCard(putOut, gameId, gameRoleId); */
        // List<Integer> t = new ArrayList(1);
        Game game = GameCache.getGameMap().get(gameId);

        CardSort cardSort = new CardSort();
        if (game.getLastCardList() != null && game.getRoleIdMap().get(gameRoleId).roleId != 0
                && game.getRoleIdMap().get(gameRoleId).auto < 2) {
            putOut = new ArrayList<Integer>(1);
            game.getRoleIdMap().get(gameRoleId).auto++;
        } else if (game.getRoleIdMap().get(gameRoleId).roleId != 0 && game.getRoleIdMap().get(gameRoleId).auto < 2) {
            game.getRoleIdMap().get(gameRoleId).auto++;
        }
        if (game.getRoleIdMap().get(gameRoleId).auto == 2) {
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SessionUtils.sc(info.roleId,
                        SC.newBuilder()
                                .setSCFightAuto(SCFightAuto.newBuilder()
                                        .setSeated(game.getRoleIdList()
                                                .indexOf(game.getRoleIdMap().get(gameRoleId).gameRoleId))
                                        .setAuto(true))
                                .build());
                this.notifyObservers(FightConstant.FIGHT_RECORD, info,
                        SC.newBuilder()
                                .setSCFightAuto(SCFightAuto.newBuilder()
                                        .setSeated(game.getRoleIdList()
                                                .indexOf(game.getRoleIdMap().get(gameRoleId).gameRoleId))
                                        .setAuto(true))
                                .build(),
                        1);
            }
        }
        CardTools.fillCardSort(cardSort, putOut);

        // 如果没有先前的牌型，则为主动出牌,直接判断牌型<br>否则通过使用上一轮牌型判断
        CardList sendCardList = game.getLastCardList() == null ? initiativeSend(cardSort, putOut)
                : passiveSend(game.getLastCardList().getClass(), cardSort, putOut);
        if (sendCardList == null) {
            game.setPassCount(game.getPassCount() + 1);
        }
        this.sendCard(putOut, gameId, gameRoleId, sendCardList);
        boolean allowGuo = game.getPassCount() < (game.getMaxRoleCount() - 1);

        SCFightPutOutPai.Builder SCPutOutBuilder = SCFightPutOutPai.newBuilder().addAllPutOutPai(putOut)
                .setSeated(game.getCurrentRoleIdIndex())
                .setType(sendCardList == null ? "guo" : sendCardList.getClass().getSimpleName());
        for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
            SCPutOutBuilder.addPaiNum(PaiNum.newBuilder().setSeated(game.getRoleIdList().indexOf(tInfo.gameRoleId))
                    .setNum(tInfo.cards.size()));

        }
        SCFightPutOutPai sCFightPutOutPai = SCPutOutBuilder.build();
        for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
            SessionUtils.sc(tInfo.roleId, SC.newBuilder().setSCFightPutOutPai(sCFightPutOutPai).build());
            this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo,
                    SC.newBuilder().setSCFightPutOutPai(sCFightPutOutPai).build(), 1);
            if (gameRoleId.equals(game.getLandlordGameRoleId()) && game.isMingPaiState()) {
                RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
                SC sc = SC.newBuilder()
                        .setSCFightMingPai(SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
                                .setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId)).setIsFirst(1)) // 1表示不是第一次明牌
                        .build();
                SessionUtils.sc(tInfo.roleId, sc);
                this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo, sc, 1);
            }

            // //所有人的排 记录到录像
            for (RoleGameInfo tInfo2 : game.getRoleIdMap().values()) {
                RoleGameInfo roleInfo = game.getRoleIdMap().get(gameRoleId);
                SC sc = SC.newBuilder().setSCFightMingPai(SCFightMingPai.newBuilder().addAllPai(roleInfo.cards)
                        .setSeated(game.getRoleIdList().indexOf(roleInfo.gameRoleId)).setIsFirst(1)).build(); // 1表示不是第一次明牌
                this.notifyObservers(FightConstant.FIGHT_ALL_CARD, tInfo2, sc);
            }
            if (game.getRecords().get(game.getRecords().size() - 1).size() == 3) {
                SessionUtils.sc(tInfo.roleId,
                        SC.newBuilder().setSCFightLastRoundReady(SCFightLastRoundReady.newBuilder()).build());
                this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo,
                        SC.newBuilder().setSCFightLastRoundReady(SCFightLastRoundReady.newBuilder()).build(), 1);
            }
        }
        notifyObservers(FightConstant.SEND_CARD, gameId, gameRoleId, sendCardList);

        // 检查游戏是否结束
        if (!this.checkGameOver(game)) {
            // 如果没有结束，则请求下一个人出牌
            // 将索引换到下一个人
            getNextIndex(gameId);

            game.setSendCardCount(game.getSendCardCount() + 1);
            game.setSendCardTime(TimeUtils.getNowTime());
            for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {

                SCFightPutOut scFightPutOut = createSCFightPutOut(game, allowGuo);

                SC putOutSC = SC.newBuilder().setSCFightPutOut(scFightPutOut).build();

                SessionUtils.sc(tInfo.roleId, putOutSC);
                this.notifyObservers(FightConstant.FIGHT_OUTPUT, tInfo, putOutSC);
            }

            notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
        }
    }

    /**
     * 实现自动出牌
     * 
     * @param gameId
     * @return
     * @author wcy 2017年6月2日
     */
    private List<Integer> getAutoPaiList(int gameId) {
        List<List<Integer>> priorityList = getRecommandCardList(gameId);
        if (priorityList == null || priorityList.size() == 0) {
            return null;
        }
        return priorityList.get(0);
    }

    /**
     * 获取下位
     * 
     * @param gameId
     * @return
     */
    private int getNextIndex(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        int index = game.getCurrentRoleIdIndex();
        game.setCurrentRoleIdIndex((index + 1) >= game.getRoleIdList().size() ? 0 : index + 1);
        return game.getCurrentRoleIdIndex();
    }

    @Override
    public void getBestCardList(Role role) {
        int gameId = role.getGameId();
        Game game = GameCache.getGameMap().get(gameId);

        // 获得上一次的出牌
        CardList lastCardList = game.getLastCardList();

        // 获得该玩家信息
        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

        // 获得玩家卡牌
        CardSort cardSort = new CardSort();
        CardTools.fillCardSort(cardSort, roleGameInfo.cards);

    }

    /**
     * 主动出牌
     * 
     * @param cardSort
     * @param paiList
     * @return
     */
    private CardList initiativeSend(CardSort cardSort, List<Integer> paiList) {
        for (CardList cardList : GameCache.getCardLists().values()) {
            CardList sendCardList = checkCardList(cardList, cardSort, paiList);
            if (sendCardList != null)
                return sendCardList;
        }
        return null;
    }

    /**
     * 被动出牌
     * 
     * @param clazz
     * @param cardSort
     * @param paiList
     * @return
     */
    private CardList passiveSend(Class<?> clazz, CardSort cardSort, List<Integer> paiList) {
        CardList cardList = GameCache.getCardLists().get(clazz);
        CardList sendCardList = checkCardList(cardList, cardSort, paiList);

        if (sendCardList == null) {
            // 没有匹配成功，再查炸弹
            for (CardList checkCardList : GameCache.getSendCardSeqCheckerList()) {
                if (sendCardList == null) {
                    sendCardList = checkCardList(checkCardList, cardSort, paiList);
                    if (sendCardList != null)
                        break;
                }
            }
        }
        return sendCardList;
    }

    /**
     * 发送的牌是否比上一次的牌大
     * 
     * @param sendCardList
     * @param lastCardList
     * @return
     */
    private boolean sendCardListBiggerThanLastCardList(CardList sendCardList, CardList lastCardList) {
        int compare = sendCardList.compareTo(lastCardList);
        if (compare <= 0)
            return false;

        return true;
    }

    /**
     * 检查游戏是否结束
     * 
     * @param gameId
     */
    public boolean checkGameOver(Game game) {
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.cards.size() == 0) {
                // 该玩家是赢家
                loggerinfo("game=>" + game.getGameId() + " gameOver");
                this.gameOver(game, info.gameRoleId);
                return true;
            }
        }

        return false;
    }

    /**
     * 根据原有牌型查牌型
     * 
     * @param targetCardList
     * @param cardSort
     * @param paiList
     * @return
     */
    public CardList checkCardList(CardList targetCardList, CardSort cardSort, List<Integer> paiList) {
        try {
            return targetCardList.pattern(cardSort, paiList);
        } catch (CardListPatternException e) {
        }
        return null;
    }

    /**
     * 获得当前玩家的信息
     * 
     * @param gameId
     * @return
     * @author wcy 2017年6月2日
     */
    private RoleGameInfo getCurrentRoleGameInfo(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        int index = game.getCurrentRoleIdIndex();
        String gameRoleId = game.getRoleIdList().get(index);
        return game.getRoleIdMap().get(gameRoleId);
    }

    public static void main(String[] args) {
        // test_dispatchCard();
        // test_linkedMap();
        // test_call_landlord();

        // test_apply_exit();

        test_next();

    }

    public static void test_next() {
        Game game = new Game();
        game.setCurrentRoleIdIndex(3);
        game.getRoleIdList().addAll(Arrays.asList("1", "2", "3"));
        GameCache.getGameMap().put(1, game);
        FightServiceImpl impl = new FightServiceImpl();
        for (int i = 0; i < 3; i++) {
            int j = impl.getNextIndex(1);
            System.out.println(j);

        }

    }

    private static void test_apply_exit() {
        FightServiceImpl impl = new FightServiceImpl();

        Game game = new Game();
        game.setGameId(1);
        GameCache.getGameMap().put(1, game);

        for (int i = 0; i < 3; i++) {
            RoleGameInfo roleGameInfo = new RoleGameInfo();
            roleGameInfo.roleId = i;
            roleGameInfo.gameRoleId = i + "";
            game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);
        }

        game.setApplyExitGameRoleId("0");
        impl.voteApplyExit(game, "1", 0, FightVoteApplyExit.VOTE_AGREE);
        impl.voteApplyExit(game, "2", 0, FightVoteApplyExit.VOTE_AGREE);

        System.out.println(impl.checkVoteContinueGame(game));
    }

    private static void test_linkedMap() {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        map.put(4, 4);
        map.put(5, 5);
        map.put(3, 3);
        map.put(6, 6);
        map.put(8, 8);
        map.put(7, 7);
        map.put(9, 9);
        map.put(10, 10);
        map.put(11, 11);
        map.put(12, 12);
        System.out.println(map.keySet());
    }

    private static void test_dispatchCard() {
        FightServiceImpl impl = new FightServiceImpl();
        Game game = new Game();
        game.setGameId(1);
        game.setMaxRoleCount(3);
        game.setMasterRoleId(1);

        GameCache.getGameMap().put(game.getGameId(), game);

        for (int i = 0; i < 3; i++) {
            RoleGameInfo info = new RoleGameInfo();
            info.gameRoleId = i + "";
            info.roleId = i;
            game.getRoleIdMap().put(info.gameRoleId, info);
        }

        impl.dispatchCard(1);

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            System.out.print(info.gameRoleId + "=");
            for (int i : info.cards)
                System.out.print(CardTools.toNum(i) + ",");
            System.out.println();
        }
        for (int card : game.getLandlordCards())
            System.out.print(CardTools.toNum(card) + ",");

    }

    private static void test_map() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        Set<Integer> set = map.keySet();
        Set<Integer> set2 = map.keySet();
        map.remove(1);
        System.out.println(set);
    }

    private static void test_call_landlord() {
        FightServiceImpl impl = new FightServiceImpl();
        Game game = new Game();
        game.setGameId(1);
        game.setMaxRoleCount(3);
        game.setMasterRoleId(1);

        GameCache.getGameMap().put(game.getGameId(), game);

        for (int i = 0; i < 3; i++) {
            RoleGameInfo info = new RoleGameInfo();
            info.gameRoleId = i + "";
            info.roleId = i;
            game.getRoleIdMap().put(info.gameRoleId, info);
            game.getRoleIdList().add(info.gameRoleId);
        }

        impl.gameInit(1);
        impl.dispatchCard(1);

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            System.out.print(info.gameRoleId + "=");
            for (int i : info.cards)
                System.out.print(CardTools.toNum(i) + ",");
            System.out.println();
        }
        for (int card : game.getLandlordCards())
            System.out.print(CardTools.toNum(card) + ",");

        game.setCurrentRoleIdIndex(0);
        System.out.println();

        impl.callLandlord(1, 0 + "", 0);
        System.out.println("mul=" + game.getMultiple() + " score=" + game.getMultiple());
        impl.callLandlord(1, 1 + "", 0);
        System.out.println("mul=" + game.getMultiple() + " score=" + game.getMultiple());
        impl.callLandlord(1, 2 + "", 0);
        System.out.println("mul=" + game.getMultiple() + " score=" + game.getMultiple());
        game.setCurrentRoleIdIndex(0);
        impl.callLandlord(1, 0 + "", 1);
        System.out.println("mul=" + game.getMultiple() + " score=" + game.getMultiple());
        impl.callLandlord(1, 1 + "", 3);
        System.out.println("mul=" + game.getMultiple() + " score=" + game.getMultiple());
        impl.callLandlord(1, 2 + "", 2);
        System.out.println("mul=" + game.getMultiple() + " score=" + game.getMultiple());

    }

    private static void test_send_card_clock() {

    }

    @Override
    public void getGameInfo(Role role, String lockString) {
        int gameId = GameCache.getGameLockStringMap().get(lockString);
        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            SC sc = SC.newBuilder()
                    .setFightGameInfoResponse(
                            FightGameInfoResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
                    .build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }
        FightGameInfoResponse.Builder fightGameInfoResponse = FightGameInfoResponse.newBuilder();
        List<String> roleIdList = game.getRoleIdList();
        for (String roleId : roleIdList) {
            if ("-1".equals(roleId)) {
                continue;
            }
            RoleInfo.Builder roleInfo = RoleInfo.newBuilder();
            roleInfo.setRoleId(game.getRoleIdMap().get(roleId).roleId);
            roleInfo.setSeat(roleIdList.indexOf(roleId));
            fightGameInfoResponse.addRoleInfo(roleInfo);
        }
        SC sc = SC.newBuilder().setFightGameInfoResponse(fightGameInfoResponse).build();
        SessionUtils.sc(role.getRoleId(), sc);
    }

    @Override
    public void disconnect(Role role) {
        // TODO Auto-generated method stub

    }
}
