package com.randioo.doudizhu_server.module.fight;

public class FightConstant {

    public final static int[] CARDS = {
            // 3 4 5 6 7 8 9 10 J Q K A 2
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
            0x1A,
            0x1B,
            0x1C,
            0x1D, // 黑桃
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A,
            0x2B,
            0x2C,
            0x2D, // 红桃
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B,
            0x3C,
            0x3D, // 草花
            0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B,
            0x4C, 0x4D, // 方片
            // Joker
            0x0E, 0x0F };

    public final static String SEND_CARD = "fight_send_card";

    public static final String NEXT_GAME_ROLE_SEND_CARD = "next_game_role_send_card";
    public static final String NEXT_ROLE_TO_CALL_LANDLORD = "next_role_to_call_landlord";
    public static final String FIGHT_APPLY_LEAVE = "fight_apply_leave";
    public static final String FIGHT_RECORD = "RECORD";
    public static final String FIGHT_ROUND = "ROUND";
    public static final String FIGHT_GAME_OVER = "fight_game_over";
    public static final String FIGHT_CREATE_GAME = "fight_create_game";
    public static final String FIGHT_JION = "fight_jion"; // 加入房间
    public static final String FIGHT_ROUND_OVER = "fight_round_over"; // 完成一轮游戏
    public static final String FIGHT_CURRENT_SCORE = "fight_current_score"; // 完成一轮游戏
    public static final String FIGHT_MINGPAI_ONOROFF = "fight_mingpai_onoroff"; // 明牌卡关
    public static final int ROUNDOVER_WAIT_TIME = 3; // 一次结算延迟
    public static final String FIGHT_SPRING = "fight_spring"; // 春天
    public static final String FIGHT_NOTICE_FEN = "notice_fen"; // 通知分数
    public static final String FIGHT_READY = "ready"; // 准备
    public static final String FIGHT_MINGPAI = "mingpai"; // 明牌
    public static final String FIGHT_OUTPUT = "output"; // 出牌
    public static final String FIGHT_AUTO= "auto"; //托管
    public static final String FIGHT_ALL_CARD= "all_card"; //所有人的牌
    public static final String FIGHT_SEND_CARD= "send_card"; //发牌
    public static final String FIGHT_CALL_LAND_LORD= "call_Land_lord"; //通知叫地主
    
    public static final int SCORE_3 = 3;

    public static final int SEND_CARD_WAIT_TIME = 30;

    public static final int COUNTDOWN = 30;

    public static final int GAME_IDLE = 1;
    public static final int GAME_OVER = 2;
    public static final int GAME_CONTINUE = 3;

}
