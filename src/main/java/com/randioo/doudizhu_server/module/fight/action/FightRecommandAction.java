package com.randioo.doudizhu_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Fight.FightRecommandRequest;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(FightRecommandRequest.class)
public class FightRecommandAction implements IActionSupport {

	@Autowired
	private FightService fightService;
	
	@Override
	public void execute(Object data, IoSession session) {
		FightRecommandRequest request = (FightRecommandRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage sc = fightService.recommandCardList(role);
		SessionUtils.sc(role.getRoleId(), sc);
	}

}
