package com.randioo.doudizhu_server;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.keepalive.KeepAliveFilter;

import com.randioo.doudizhu_server.handler.HeartTimeOutHandler;
import com.randioo.doudizhu_server.protocol.ClientMessage.CS;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_server_base.config.ConfigLoader;
import com.randioo.randioo_server_base.config.GlobleArgsLoader;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.config.GlobleXmlLoader;
import com.randioo.randioo_server_base.heart.ProtoHeartFactory;
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.init.LogSystem;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.SpringContext;

/**
 * Hello world!
 * 
 */
public class doudizhu_serverApp {
    public static void main(String[] args) {
        GlobleXmlLoader.init("./server.xml");
        GlobleArgsLoader.init(args);

        LogSystem.init(doudizhu_serverApp.class);

        ConfigLoader.loadConfig(GlobleConstant.CONFIG_PACKAGE_PATH, GlobleMap.String(GlobleConstant.ARGS_CONFIG_PATH));
        SensitiveWordDictionary.readAll(GlobleMap.String(GlobleConstant.ARGS_SENSITIVE_PATH));

        SpringContext.initSpringCtx("ApplicationContext.xml");

        RandiooPlatformSdk randiooPlatformSdk = SpringContext.getBean(RandiooPlatformSdk.class);
        randiooPlatformSdk.setDebug(GlobleMap.Boolean(GlobleConstant.ARGS_PLATFORM));
        randiooPlatformSdk.setActiveProjectName(GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME));

        GameServerInit gameServerInit = ((GameServerInit) SpringContext.getBean(GameServerInit.class));

        HeartTimeOutHandler handler = SpringContext.getBean(HeartTimeOutHandler.class);
        gameServerInit.setKeepAliveFilter(new KeepAliveFilter(new ProtoHeartFactory(CS.class, SC.class),
                IdleStatus.READER_IDLE, handler, 3, 5));

        gameServerInit.start();
        GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, true);

        HttpInit.init();
    }

}
