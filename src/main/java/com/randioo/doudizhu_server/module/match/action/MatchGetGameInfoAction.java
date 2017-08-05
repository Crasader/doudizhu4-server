package com.randioo.doudizhu_server.module.match.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.doudizhu_server.protocol.Match.MatchCancelRequest;
import com.randioo.doudizhu_server.protocol.Match.MatchGetGameInfoRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Controller
@PTAnnotation(MatchGetGameInfoRequest.class)
public class MatchGetGameInfoAction implements IActionSupport {

	@Autowired
	private MatchService matchService;

	@Override
	public void execute(Object data, IoSession session) {
		MatchGetGameInfoRequest request = (MatchGetGameInfoRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage sc = matchService.getRoomInfo(role);
		SessionUtils.sc(role.getRoleId(), sc);
	}

}
