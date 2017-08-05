package com.randioo.doudizhu_server.module.video.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.randioo.doudizhu_server.dao.VideoDAO;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.bo.VideoData;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.FightConstant;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Entity.video;
import com.randioo.doudizhu_server.protocol.Entity.videoWithId;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.protocol.Video.VideoGetByIdResponse;
import com.randioo.doudizhu_server.protocol.Video.VideoGetByRoundResponse;
import com.randioo.doudizhu_server.protocol.Video.VideoGetResponse;
import com.randioo.doudizhu_server.util.VideoUtils;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;

@Service("videoService")
public class VideoServiceImpl extends ObserveBaseService implements
        VideoService {

    @Autowired
    private VideoDAO videoDAO;

    @Autowired
    private FightService fightService;

    @Override
    public void initService() {
        fightService.addObserver(this);
    }

    @Override
    public void update(Observer observer, String msg, Object... args) {
        if (FightConstant.FIGHT_SPRING.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_CREATE_GAME.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.headList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_CURRENT_SCORE.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scoreList.add((SC) args[1]);
            info.headList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_JION.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.headList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_ROUND_OVER.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_RECORD.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            int i = (int) args[2];
            if (i == 1) {
                info.scList.add((SC) args[1]);
                info.scList2.add((SC) args[1]);
            }
            if (i == 2) {
                info.scList2.add((SC) args[1]);
            }
        }
        if (FightConstant.FIGHT_MINGPAI_ONOROFF.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_NOTICE_FEN.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_READY.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_GAME_OVER.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_MINGPAI.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if (FightConstant.FIGHT_OUTPUT.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if(FightConstant.FIGHT_AUTO.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
        if(FightConstant.FIGHT_ALL_CARD.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList2.add((SC) args[1]);
        }
        if(FightConstant.FIGHT_SEND_CARD.equals(msg)) {
            RoleGameInfo info = (RoleGameInfo) args[0];
            info.scList.add((SC) args[1]);
            info.scList2.add((SC) args[1]);
        }
    }

    @Override
    public GeneratedMessage videoGet(Role role) {
        List<videoWithId> videoList = new ArrayList<>();
        List<VideoData> videoDataList = videoDAO.get(role.getRoleId());

        for (VideoData v : videoDataList) {
            videoList.add(videoWithId.newBuilder()
                    .setVideoMsg(VideoUtils.parseVideoWithoutRecord(v))
                    .setVideoId(v.getId()).setRoleId(v.getRoleId()).build());
        }
        return SC
                .newBuilder()
                .setVideoGetResponse(
                        VideoGetResponse.newBuilder().addAllVideoMsg(videoList))
                .build();
    }

    @Override
    public GeneratedMessage videoGetById(int id) {
        VideoData v = videoDAO.getById(id);
        if (v == null) {
            return SC
                    .newBuilder()
                    .setVideoGetByIdResponse(
                            VideoGetByIdResponse.newBuilder().setErrorCode(
                                    ErrorCode.NULL_REJECT.getNumber())).build();
        }
        return SC
                .newBuilder()
                .setVideoGetByIdResponse(
                        VideoGetByIdResponse
                                .newBuilder()
                                .setVideoMsg(
                                        videoWithId
                                                .newBuilder()
                                                .setVideoMsg(
                                                        VideoUtils
                                                                .parseVideoWithoutRecord(v))
                                                .setVideoId(v.getId())
                                                .setRoleId(v.getRoleId())))
                .build();
    }

    @Override
    public GeneratedMessage videoGetByRound(int id, int round) {
        VideoData v = videoDAO.getById(id);
        if (v == null) {
            return SC
                    .newBuilder()
                    .setVideoGetByRoundResponse(
                            VideoGetByRoundResponse.newBuilder().setErrorCode(
                                    ErrorCode.NO_VIDEO.getNumber())).build();// ERROR
        }
        video DBVideo = VideoUtils.parseVideo(v);
        List<ByteString> SCList = DBVideo.getVideoRecordList();
        List<Integer> keypointList = DBVideo.getKeyPointList();
        List<ByteString> resultList = new ArrayList<>();
        if (v == null
                || round < 1
                || keypointList.size() < round * 2
                || keypointList.get(2 * round - 2) >= keypointList
                        .get(2 * round - 1)) {
            return SC
                    .newBuilder()
                    .setVideoGetByRoundResponse(
                            VideoGetByRoundResponse.newBuilder().setErrorCode(
                                    ErrorCode.NO_VIDEO.getNumber())).build();// ERROR
        }
        for (int i = 0; i < keypointList.get(0); i++) {
            resultList.add(SCList.get(i));
        }
        for (int i = keypointList.get(2 * round - 2); i < keypointList
                .get(2 * round - 1); i++) {
            resultList.add(SCList.get(i));
        }
        return SC
                .newBuilder()
                .setVideoGetByRoundResponse(
                        VideoGetByRoundResponse.newBuilder().setVideoMsg(
                                video.newBuilder()
                                        .addAllVideoRecord(resultList)))
                .build();
    }

}
