package com.randioo.doudizhu_server.http.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.sun.net.httpserver.HttpExchange;

@Controller
public class RaceListAction implements IActionSupport {

	@Autowired
	private MatchService matchService;

	@Override
	public void execute(Object data, IoSession session) {
		HttpExchange request = (HttpExchange) data;
		String lockString = (String) request.getAttribute("lockString");
		// int gameId = Integer.parseInt(gameIdStr);
		// String account = (String) request.getAttribute("account");
		matchService.cancelRace(lockString);
	}

	public String getList(Object data, IoSession session) {
		Set<String> lockStringSet = matchService.getLockStringList();
		JSONArray jsonArray = new JSONArray(lockStringSet);
		System.out.println(jsonArray.toString());
		return jsonArray.toString();
	}
	
//	public static void main(String args[]){
//		Set<String> set = new HashSet<>();
//		set.add("132564");
//		set.add("333455");
//		JSONArray jsonArray = new JSONArray(set);
//		System.out.println(jsonArray.toString());
//		
//	}
}
