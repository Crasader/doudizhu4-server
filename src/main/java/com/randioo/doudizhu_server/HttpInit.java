package com.randioo.doudizhu_server;

import java.io.IOException;

import com.randioo.doudizhu_server.http.servlet.RaceCancelServlet;
import com.randioo.doudizhu_server.http.servlet.RaceListServlet;
import com.randioo.http_server.server.LiteHttpServer;
import com.randioo.http_server.server.LiteServlet;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.utils.SpringContext;

public class HttpInit {

    private static LiteHttpServer httpServer = new LiteHttpServer();

    public static void init() {
        httpServer.setPort(GlobleMap.Int(GlobleConstant.ARGS_PORT) + 10000);
        httpServer.setRootPath("/doudizhu");
        httpServer.addLiteServlet("/cancelRace", (LiteServlet) SpringContext.getBean(RaceCancelServlet.class));
        httpServer.addLiteServlet("/getListRace", (LiteServlet) SpringContext.getBean(RaceListServlet.class));
        try {
            httpServer.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LiteHttpServer getHttpServer() {
        return httpServer;
    }

}
