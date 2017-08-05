package com.randioo.doudizhu_server.http.action;



import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.sun.net.httpserver.HttpExchange;

@Controller
public class RaceCancelAction implements IActionSupport {

	@Autowired
	private MatchService matchService ;
	@Override
	public void execute(Object data, IoSession session) {
		HttpExchange request = (HttpExchange) data;
		String lockString = (String) request.getAttribute("lockString");
//		int gameId = Integer.parseInt(gameIdStr);
//		String account = (String) request.getAttribute("account");
		matchService.cancelRace(lockString);
	}
}
