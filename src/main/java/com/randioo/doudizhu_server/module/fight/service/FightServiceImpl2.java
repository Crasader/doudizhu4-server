package com.randioo.doudizhu_server.module.fight.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.doudizhu_server.GlobleConstant;
import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.comparator.HexCardComparator;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.FightConstant;
import com.randioo.doudizhu_server.module.fight.component.CallLandlord;
import com.randioo.doudizhu_server.module.fight.component.CallLandlordJudger;
import com.randioo.doudizhu_server.module.fight.component.CardSort;
import com.randioo.doudizhu_server.module.fight.component.GameBroadcast;
import com.randioo.doudizhu_server.module.fight.component.GameFinder;
import com.randioo.doudizhu_server.module.fight.component.GameInit;
import com.randioo.doudizhu_server.module.fight.component.OverChecker;
import com.randioo.doudizhu_server.module.fight.component.Report;
import com.randioo.doudizhu_server.module.fight.component.RoleCountChecker;
import com.randioo.doudizhu_server.module.fight.component.RoleGameInfoFinder;
import com.randioo.doudizhu_server.module.fight.component.RoleGameSeat;
import com.randioo.doudizhu_server.module.fight.component.SeatCursor;
import com.randioo.doudizhu_server.module.fight.component.cardlist.CardList;
import com.randioo.doudizhu_server.module.fight.component.dispatch.CardPart;
import com.randioo.doudizhu_server.module.fight.component.dispatch.DebugDispatcher;
import com.randioo.doudizhu_server.module.fight.component.dispatch.Dispatcher;
import com.randioo.doudizhu_server.module.fight.component.dispatch.RandomDispatcher;
import com.randioo.doudizhu_server.module.fight.component.send.AbstractAction;
import com.randioo.doudizhu_server.module.fight.component.send.GuoAction;
import com.randioo.doudizhu_server.module.fight.component.send.InitiativeSendAction;
import com.randioo.doudizhu_server.module.fight.component.send.PassiveSendAction;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListPatternException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardListTooSmallException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.component.send.exception.MustSendCardException;
import com.randioo.doudizhu_server.module.match.component.RoleGameIdCreator;
import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.doudizhu_server.protocol.Entity.Cards;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.PaiNum;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Fight.FightCallLandLordResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightMingPaiResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightPlayReportResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightReadyResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightReportResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightSendCardResponse;
import com.randioo.doudizhu_server.protocol.Fight.SCFightButtonDisappear;
import com.randioo.doudizhu_server.protocol.Fight.SCFightCallLandLord;
import com.randioo.doudizhu_server.protocol.Fight.SCFightCallScore;
import com.randioo.doudizhu_server.protocol.Fight.SCFightLandLord;
import com.randioo.doudizhu_server.protocol.Fight.SCFightMingPai;
import com.randioo.doudizhu_server.protocol.Fight.SCFightMingPaiShow;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPlayReport;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPlayReportShow;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPutOut;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPutOutPai;
import com.randioo.doudizhu_server.protocol.Fight.SCFightReady;
import com.randioo.doudizhu_server.protocol.Fight.SCFightReport;
import com.randioo.doudizhu_server.protocol.Fight.SCFightReportShow;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.service.ObserveBaseService;

@Service("fightService")
public class FightServiceImpl2 extends ObserveBaseService {

    @Autowired
    private CallLandlord callLandlord;
    @Autowired
    private GameInit gameInit;

    @Autowired
    private GameFinder gameFinder;

    @Autowired
    private RoleGameIdCreator roleGameIdCreator;

    @Autowired
    private RoleGameInfoFinder roleGameInfoFinder;

    @Autowired
    private RoleGameSeat roleGameSeat;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private RoleCountChecker roleCountChecker;

    @Autowired
    private MatchService matchService;

    @Autowired
    private RandomDispatcher randomDispatcher;

    @Autowired
    private DebugDispatcher debugDispatcher;

    @Autowired
    private HexCardComparator hexCardComparator;

    @Autowired
    private CallLandlordJudger callLandlordJudger;

    @Autowired
    private Report report;

    @Autowired
    private GuoAction guoAction;

    @Autowired
    private InitiativeSendAction initiativeSendAction;

    @Autowired
    private PassiveSendAction passiveSendAction;

    @Autowired
    private SeatCursor seatCursor;

    @Autowired
    private OverChecker overChecker;

    /**
     * 游戏准备
     */
    public void gameReady(Role role) {
        int gameId = role.getGameId();
        // 获得游戏实体
        Game game = gameFinder.find(gameId);

        FightReadyResponse.Builder readyResponseBuilder = FightReadyResponse.newBuilder();
        if (game == null) {
            readyResponseBuilder.setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber());
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightReadyResponse(readyResponseBuilder).build());
            return;
        }

        // 应答游戏准备
        SC fightReadyResponse = SC.newBuilder().setFightReadyResponse(readyResponseBuilder).build();
        SessionUtils.sc(role.getRoleId(), fightReadyResponse);

        String gameRoleId = roleGameIdCreator.getRoleId(game, role.getRoleId());
        RoleGameInfo roleGameInfo = roleGameInfoFinder.find(game, gameRoleId);
        roleGameInfo.ready = true;

        // 获得玩家座位号
        int seat = roleGameSeat.getSeat(game, gameRoleId);
        // 是否是第一局
        boolean isFirst = game.getGameConfig().getRound() == game.getRound();
        SC scFightReadySC = SC.newBuilder()
                .setSCFightReady(SCFightReady.newBuilder().setSeated(seat).setIsFirst(isFirst)).build();

        // 通知所有人
        gameBroadcast.notice(game, scFightReadySC);
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            this.notifyObservers(FightConstant.FIGHT_READY, info, scFightReadySC);
        }

        // 检查是否全部都准备完毕,全部准备完毕开始游戏
        int maxCount = game.getMaxRoleCount();
        boolean allReady = roleCountChecker.checkAllReady(game.getRoleIdMap(), maxCount);

        if (GlobleMap.Boolean(GlobleConstant.ARGS_MATCH_AI)) {
            matchService.fillAI(game);
            allReady = true;
        }

        // 所有人都准备完毕则开始游戏
        if (allReady) {
            // 游戏开始
            this.gameStart(game);
        }

    }

    /**
     * 游戏开始
     */
    public void gameStart(Game game) {

        game.setGameState(GameState.GAME_START_START);
        // 游戏初始化
        gameInit.init(game);
        // TODO 明牌为王 要通知底分 x2

        // 获取分发器
        Dispatcher dispatcher = randomDispatcher;

        // 检测全局变量
        if (GlobleMap.Boolean(GlobleConstant.ARGS_DISPATCH)) {
            // 切换到调试牌组
            dispatcher = debugDispatcher;
        }

        // 准备所有卡组
        List<Integer> landlordCards = game.getLandlordCards();
        for (int i = 0; i < FightConstant.CARD_BOX_COUNT; i++) {
            for (int index = 0; index < FightConstant.CARDS.length; index++) {
                landlordCards.add(FightConstant.CARDS[index]);
            }
        }

        // 玩家数量
        int roleCount = game.getRoleIdMap().size();
        List<CardPart> cardParts = dispatcher.dispatch(landlordCards, FightConstant.EVERY_PART_CARD_COUNT, roleCount);
        // 每个人加牌
        int i = 0;
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            roleGameInfo.cards.addAll(cardParts.get(i));
            // 卡组排序
            Collections.sort(roleGameInfo.cards, hexCardComparator);
        }

        // 确认开始叫地主的人
        String landlordGameRoleId = callLandlordJudger.getStartCallLandlordGameRoleId(game.getRoleIdMap().values(),
                game.getFlagCard());
        game.setStartGameRoleId(landlordGameRoleId);
        // 通知叫地主
        this.noticeCallLandlord(game, landlordGameRoleId);
    }

    /**
     * 通知叫地主
     */
    public void noticeCallLandlord(Game game, String landlordGameRoleId) {

        int index = roleGameSeat.getSeat(game, landlordGameRoleId);
        int currentScore = game.getScore();
        SCFightCallLandLord.Builder callLandLardBuilder = SCFightCallLandLord.newBuilder();
        callLandLardBuilder.setSeated(index);
        callLandLardBuilder.setCurrentScore(currentScore);

        SC scCallLandLord = SC.newBuilder().setSCFightCallLandLord(callLandLardBuilder).build();
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            SessionUtils.sc(roleGameInfo.roleId, scCallLandLord);
        }
    }

    /**
     * 叫地主
     */
    public void callLandlord(Role role, int score) {
        int gameId = role.getGameId();
        Game game = GameCache.getGameMap().get(gameId);
        FightCallLandLordResponse.Builder callLandLordResponseBuilder = FightCallLandLordResponse.newBuilder();
        if (game == null) {
            callLandLordResponseBuilder.setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber());
            SC callLandLordResponse = SC.newBuilder().setFightCallLandLordResponse(callLandLordResponseBuilder).build();
            SessionUtils.sc(role.getRoleId(), callLandLordResponse);
            return;
        }
        SC callLandLordResponse = SC.newBuilder().setFightCallLandLordResponse(callLandLordResponseBuilder).build();
        SessionUtils.sc(role.getRoleId(), callLandLordResponse);
        String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
        int callScoreResult = callLandlord.callLandlord(game, gameRoleId, score);

        if (FightConstant.CALL_LANDLORD_ERROR == callScoreResult) {
            return;
        }
        // 叫分通知
        SCFightCallScore.Builder callScoreBuilder = SCFightCallScore.newBuilder();
        callScoreBuilder.setCurrentScore(game.getScore());
        callScoreBuilder.setScore(score);
        callScoreBuilder.setSeat(game.getRoleIdList().indexOf(gameRoleId));
        SC scCallScore = SC.newBuilder().setSCFightCallScore(callScoreBuilder).build();
        SessionUtils.sc(role.getRoleId(), scCallScore);
        if (FightConstant.CALL_LANDLORD_OVER == callScoreResult) {
            game.setMultiple(game.getMultiple() * game.getScore());
            this.landlordGiveCards(game);
        }
        if (FightConstant.NOT_CALL_SCORE == callScoreResult) {
        }
        if (FightConstant.NEXT_CALL_SCORE == callScoreResult) {
            // 通知下位叫挡
            String nextGameRoleId = seatCursor.getNextGameRoleId(game.getRoleIdList(), gameRoleId);
            noticeCallLandlord(game, nextGameRoleId);
        }
        if (FightConstant.DISPATCH_CARD_AGAIN == callScoreResult) {
            // 重新发牌
            this.gameStart(game);
        }
    }

    /**
     * 地主给牌
     */
    public void landlordGiveCards(Game game) {

        List<Integer> landlordCards = game.getLandlordCards();
        String landlordGameRoleId = game.getLandlordGameRoleId();

        // 将牌加入到牌组
        RoleGameInfo landLordGameInfo = game.getRoleIdMap().get(landlordGameRoleId);
        landLordGameInfo.cards.addAll(landlordCards);

        int landLordIndex = game.getRoleIdList().indexOf(game.getLandlordGameRoleId());

        // 给农民的通知
        SCFightLandLord.Builder landLordBuilder = SCFightLandLord.newBuilder();
        landLordBuilder.addAllLandLordPai(landlordCards);
        landLordBuilder.setSeated(landLordIndex);
        SC scLandLordToFarmer = SC.newBuilder().setSCFightLandLord(landLordBuilder).build();

        // 给地主的通知
        SCFightLandLord.Builder landLordToLordBuilder = SCFightLandLord.newBuilder();
        landLordToLordBuilder.addAllLandLordPai(landlordCards);
        landLordToLordBuilder.setSeated(landLordIndex);
        landLordToLordBuilder.addAllPai(landLordGameInfo.cards);
        SC scLandLordToLord = SC.newBuilder().setSCFightLandLord(landLordToLordBuilder).build();

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            if (!(roleGameInfo.gameRoleId).equals(landlordGameRoleId)) {
                SessionUtils.sc(roleGameInfo.roleId, scLandLordToFarmer);
            } else {
                SessionUtils.sc(roleGameInfo.roleId, scLandLordToLord);
            }
        }
        this.noticeLandlordReport(game);
    }

    /**
     * 通知地主报到
     * 
     * @param report
     * @return
     */
    public void noticeLandlordReport(Game game) {
        String landlordGameRoleId = game.getLandlordGameRoleId();
        RoleGameInfo landLordGameInfo = game.getRoleIdMap().get(landlordGameRoleId);

        Map<Integer, Integer> reportMap = report.canReport(landLordGameInfo.cards);
        landLordGameInfo.reportMap = reportMap; // 保存可报道的结果
        if (reportMap.isEmpty()) {
            // 如果无法报道 ，直接通知明牌
            this.noticeLightCard(game);
            return;
        }
        List<Integer> reportList = new ArrayList<>();
        reportList.addAll(reportMap.keySet());
        List<List<Integer>> resultList = report.reportCard(reportList, landLordGameInfo.cards);
        SCFightReportShow.Builder reportShowBuilder = SCFightReportShow.newBuilder();
        for (List<Integer> list : resultList) {
            Cards card = Cards.newBuilder().addAllCard(list).build();
            reportShowBuilder.addCards(card);
        }
        SC scReportShow = SC.newBuilder().setSCFightReportShow(reportShowBuilder).build();
        SessionUtils.sc(landLordGameInfo.roleId, scReportShow);

    }

    /**
     * 地主是否报道
     * 
     * @param report
     */
    public void landlordReport(Role role, boolean isReport) {
        int gameId = role.getGameId();
        Game game = GameCache.getGameMap().get(gameId);
        FightReportResponse.Builder reportResponseBuilder = FightReportResponse.newBuilder();
        if (game == null) {
            reportResponseBuilder.setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber());
            SC reportResponse = SC.newBuilder().setFightReportResponse(reportResponseBuilder).build();
            SessionUtils.sc(role.getRoleId(), reportResponse);
            return;
        }

        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        String landlordGameRoleId = game.getLandlordGameRoleId();
        RoleGameInfo landLordGameInfo = game.getRoleIdMap().get(landlordGameRoleId);
        // 不是地主 和不能报道
        if (!gameRoleId.equals(landlordGameRoleId) || landLordGameInfo.reportMap.isEmpty()) {
            reportResponseBuilder.setErrorCode(ErrorCode.REPORT_FAIL.getNumber());
            SC reportResponse = SC.newBuilder().setFightReportResponse(reportResponseBuilder).build();
            SessionUtils.sc(role.getRoleId(), reportResponse);
            return;
        }
        SC reportResponse = SC.newBuilder().setFightReportResponse(reportResponseBuilder).build();
        SessionUtils.sc(role.getRoleId(), reportResponse);

        landLordGameInfo.isReport = isReport;
        // 消除报道按钮
        SCFightButtonDisappear buttonDisappear = SCFightButtonDisappear.newBuilder().build();
        SC scButtonDisappear = SC.newBuilder().setSCFightButtonDisappear(buttonDisappear).build();
        SessionUtils.sc(role.getRoleId(), scButtonDisappear);

        if (isReport) {
            List<Integer> canReporCardtList = new ArrayList<>();
            canReporCardtList.addAll(landLordGameInfo.reportMap.keySet());

            // 通知所有人报道的牌
            List<List<Integer>> resultList = report.reportCard(canReporCardtList, landLordGameInfo.cards);
            SCFightReport.Builder reportBuilder = SCFightReport.newBuilder();
            for (List<Integer> list : resultList) {
                Cards card = Cards.newBuilder().addAllCard(list).build();
                reportBuilder.addCards(card);
            }
            SC scReport = SC.newBuilder().setSCFightReport(reportBuilder).build();
            for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
                SessionUtils.sc(roleGameInfo.roleId, scReport);
            }

            // 报道处理 ，把牌移到报道卡组
            report.report(canReporCardtList, landLordGameInfo.reportCardList, landLordGameInfo.cards);
            // 通知显示是否打报道
            noticePlayReport(game);
        }

    }

    /**
     * 通知是否打报道
     * 
     * @param play
     * @return
     */
    public void noticePlayReport(Game game) {
        SCFightPlayReportShow playReportShow = SCFightPlayReportShow.newBuilder().build();
        SC scPlayReportShow = SC.newBuilder().setSCFightPlayReportShow(playReportShow).build();
        String landlordGameRoleId = game.getLandlordGameRoleId();
        RoleGameInfo landLordGameInfo = game.getRoleIdMap().get(landlordGameRoleId);
        SessionUtils.sc(landLordGameInfo.roleId, scPlayReportShow);
    }

    /**
     * 打报道确认
     * 
     * @param play
     */
    public void playReport(Role role, boolean isPlay) {
        int gameId = role.getGameId();
        Game game = GameCache.getGameMap().get(gameId);
        FightPlayReportResponse.Builder playReportResponseBuilder = FightPlayReportResponse.newBuilder();
        if (game == null) {
            playReportResponseBuilder.setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber());
            SC playReportResponse = SC.newBuilder().setFightPlayReportResponse(playReportResponseBuilder).build();
            SessionUtils.sc(role.getRoleId(), playReportResponse);
            return;
        }

        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        String landlordGameRoleId = game.getLandlordGameRoleId();
        RoleGameInfo landLordGameInfo = game.getRoleIdMap().get(landlordGameRoleId);
        // 不是地主 和为选择报道
        if (!gameRoleId.equals(landlordGameRoleId) || landLordGameInfo.isReport) {
            playReportResponseBuilder.setErrorCode(ErrorCode.PLAY_FAIL.getNumber());
            SC playReportResponse = SC.newBuilder().setFightPlayReportResponse(playReportResponseBuilder).build();
            SessionUtils.sc(role.getRoleId(), playReportResponse);
            return;
        }
        SC playReportResponse = SC.newBuilder().setFightPlayReportResponse(playReportResponseBuilder).build();
        SessionUtils.sc(role.getRoleId(), playReportResponse);

        // 消除报道后打或不大的按钮
        SCFightButtonDisappear buttonDisappear = SCFightButtonDisappear.newBuilder().build();
        SC scButtonDisappear = SC.newBuilder().setSCFightButtonDisappear(buttonDisappear).build();
        SessionUtils.sc(role.getRoleId(), scButtonDisappear);

        SCFightPlayReport.Builder playReportBuilder = SCFightPlayReport.newBuilder();
        int index = game.getRoleIdList().indexOf(landlordGameRoleId);
        playReportBuilder.setSeat(index);
        if (!isPlay) {
            playReportBuilder.setIsPlay(false);
            SC scPlayReport = SC.newBuilder().setSCFightPlayReport(playReportBuilder).build();
            SessionUtils.sc(role.getRoleId(), scPlayReport);
            // TODO 结算本轮

        } else {
            playReportBuilder.setIsPlay(true);
            SC scPlayReport = SC.newBuilder().setSCFightPlayReport(playReportBuilder).build();
            SessionUtils.sc(role.getRoleId(), scPlayReport);
            // 通知选择是否明牌
            noticeLightCard(game);
        }
    }

    /**
     * 通知明牌
     * 
     * @return
     */
    public void noticeLightCard(Game game) {
        String landlordGameRoleId = game.getLandlordGameRoleId();
        // 获得玩家信息
        RoleGameInfo landLordGameInfo = roleGameInfoFinder.find(game, landlordGameRoleId);

        SCFightMingPaiShow.Builder scFightMingPaiBuilder = SCFightMingPaiShow.newBuilder();
        SC scFightMingPaiShow = SC.newBuilder().setSCFightMingPaiShow(scFightMingPaiBuilder).build();
        SessionUtils.sc(landLordGameInfo.roleId, scFightMingPaiShow);
    }

    /**
     * 明牌
     */
    public void lightCard(Role role, boolean isLightCard) {
        int gameId = role.getGameId();
        Game game = GameCache.getGameMap().get(gameId);
        FightMingPaiResponse.Builder mingPaiResponseBuilder = FightMingPaiResponse.newBuilder();
        if (game == null) {
            mingPaiResponseBuilder.setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber());
            SC mingPaiResponse = SC.newBuilder().setFightMingPaiResponse(mingPaiResponseBuilder).build();
            SessionUtils.sc(role.getRoleId(), mingPaiResponse);
            return;
        }
        SC mingPaiResponse = SC.newBuilder().setFightMingPaiResponse(mingPaiResponseBuilder).build();
        SessionUtils.sc(role.getRoleId(), mingPaiResponse);

        // 消除明牌按钮
        SCFightButtonDisappear buttonDisappear = SCFightButtonDisappear.newBuilder().build();
        SC scButtonDisappear = SC.newBuilder().setSCFightButtonDisappear(buttonDisappear).build();
        SessionUtils.sc(role.getRoleId(), scButtonDisappear);

        if (isLightCard) {
            // TODO 通知出牌
        } else {
            String landlordGameRoleId = game.getLandlordGameRoleId();
            RoleGameInfo landLordGameInfo = game.getRoleIdMap().get(landlordGameRoleId);
            int index = game.getRoleIdList().indexOf(landlordGameRoleId);
            SCFightMingPai.Builder mingPaiBuilder = SCFightMingPai.newBuilder();
            mingPaiBuilder.setSeated(index);
            mingPaiBuilder.setSex(role.getSex());
            mingPaiBuilder.addAllPai(landLordGameInfo.cards);
            SC scMingPai = SC.newBuilder().setSCFightMingPai(mingPaiBuilder).build();
            SessionUtils.sc(role.getRoleId(), scMingPai);
            // TODO 通知出牌

        }

    }

    /**
     * 通知出牌
     */
    public void noticeSendCard(Game game) {
        int seat = game.getCurrentRoleIdIndex();
        // 倒计时
        int countdown = FightConstant.COUNTDOWN;
        // 下一个人是否可以过
        boolean allowGuo = game.getPassCount() < (game.getMaxRoleCount() - 1);

        SCFightPutOut scFightPutOut = SCFightPutOut.newBuilder().setSeated(seat).setCountdown(countdown)
                .setAllowGuo(allowGuo).build();
        SC scFightPutOutSC = SC.newBuilder().setSCFightPutOut(scFightPutOut).build();

        gameBroadcast.notice(game, scFightPutOutSC);
    }

    /**
     * 出牌
     */
    public void sendCard(Role role, List<Integer> cardList) {
        int gameId = role.getGameId();
        int roleId = role.getRoleId();
        Game game = gameFinder.find(gameId);
        if (game == null) {
            FightSendCardResponse fightSendCardResponse = FightSendCardResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()).build();
            SC fightSendCardResponseSC = SC.newBuilder().setFightSendCardResponse(fightSendCardResponse).build();
            SessionUtils.sc(role.getRoleId(), fightSendCardResponseSC);
            return;
        }

        String gameRoleId = roleGameIdCreator.getRoleId(game, roleId);

        ErrorCode errorCode = null;
        FightSendCardResponse.Builder fightSendCardResponseBuilder = FightSendCardResponse.newBuilder();
        try {
            this.sendCard(game, gameRoleId, cardList);
        } catch (CardTypeComparableException e) {
            errorCode = ErrorCode.NOT_SAME_TYPE;
        } catch (MustSendCardException e) {
            errorCode = ErrorCode.NULL_REJECT;
        } catch (CardListPatternException e) {
            errorCode = ErrorCode.NOT_SAME_TYPE;
        } catch (CardListTooSmallException e) {
            errorCode = ErrorCode.SMALLER;
        }

        if (errorCode != null) {
            fightSendCardResponseBuilder.setErrorCode(ErrorCode.NOT_SAME_TYPE.getNumber()).build();
            SessionUtils.sc(roleId, SC.newBuilder().setFightSendCardResponse(fightSendCardResponseBuilder).build());
            return;
        }

        SessionUtils.sc(roleId, SC.newBuilder().setFightSendCardResponse(fightSendCardResponseBuilder).build());
    }

    /**
     * 出牌
     * 
     * @param game 游戏对象
     * @param gameRoleId 出牌玩家
     * @param paiList 出牌内容
     * @throws CardTypeComparableException 类型比较出错
     * @throws MustSendCardException 必须出牌
     * @throws CardListPatternException 匹配
     * @throws CardListTooSmallException 卡组太小
     * @author wcy 2017年8月8日
     */
    public void sendCard(Game game, String gameRoleId, List<Integer> paiList) throws CardTypeComparableException,
            MustSendCardException, CardListPatternException, CardListTooSmallException {
        // 获得上一局卡牌类型
        CardList lastCardList = game.getLastCardList();
        RoleGameInfo roleGameInfo = roleGameInfoFinder.find(game, gameRoleId);

        int paiListSize = paiList.size();
        AbstractAction abstractAction = null;

        // 牌数量为0,则是过
        if (paiListSize == 0) {
            // 过的动作
            abstractAction = guoAction;
        } else {
            // 没有上一次出牌则为主动出牌,否则是被动出牌
            if (lastCardList == null) {
                // 主动出牌
                abstractAction = initiativeSendAction;
            } else {
                // 被动出牌
                abstractAction = passiveSendAction;
            }
        }

        // 初始化卡组
        CardSort cardSort = new CardSort(4 * FightConstant.CARD_BOX_COUNT);
        cardSort.fillCardSort(paiList);
        // 执行动作
        abstractAction.execute(game, roleGameInfo, cardSort, lastCardList, paiList);

        // 按钮消失
        SCFightButtonDisappear scFightButtonDisappear = SCFightButtonDisappear.newBuilder().build();
        SC scFightButtonDisappearSC = SC.newBuilder().setSCFightButtonDisappear(scFightButtonDisappear).build();
        SessionUtils.sc(roleGameInfo.roleId, scFightButtonDisappearSC);

        // 发送出牌类型
        String sendType = paiListSize == 0 ? "guo" : game.getLastCardList().getClass().getSimpleName();

        int currentSeat = game.getCurrentRoleIdIndex();
        SCFightPutOutPai.Builder scPutOutPaiBuilder = SCFightPutOutPai.newBuilder().addAllPutOutPai(paiList)
                .setSeated(currentSeat).setType(sendType);

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            int seat = roleGameSeat.getSeat(game, info);
            int cardNum = info.cards.size();
            scPutOutPaiBuilder.addPaiNum(PaiNum.newBuilder().setSeated(seat).setNum(cardNum));
        }

        SC scFightPutOutPaiSC = SC.newBuilder().setSCFightPutOutPai(scPutOutPaiBuilder).build();
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId, scFightPutOutPaiSC);
        }

        // 检查是否结束
        if (!overChecker.isOver(game)) {
            // 下一个人出牌
            seatCursor.getNextGameRoleId(game.getRoleIdList(), gameRoleId);
        }
    }

    /**
     * 检查地主明牌变化
     * 
     * @param gameRoleId
     * @param game
     * @return
     * @author wcy 2017年8月8日
     */
    private boolean checkLandlordLigheCardChange(String gameRoleId, Game game) {
        boolean isMingPai = game.isMingPaiState();
        if (isMingPai) {
            String landlordGameRoleId = game.getLandlordGameRoleId();
            if (gameRoleId.equals(landlordGameRoleId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 一次结算
     */
    public void roundOver(Game game) {

    }

    /**
     * 二次结算
     */
    public void gameOver(Game game) {

    }
}
