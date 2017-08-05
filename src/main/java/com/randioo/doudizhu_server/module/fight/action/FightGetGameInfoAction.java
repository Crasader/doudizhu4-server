package com.randioo.doudizhu_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Fight.FightGameInfoRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(FightGameInfoRequest.class)
public class FightGetGameInfoAction implements IActionSupport {

	@Autowired
	private FightService fightService;

	@Override
	public void execute(Object data, IoSession session) {
		FightGameInfoRequest request = (FightGameInfoRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		String lockString = request.getLockString();
		fightService.getGameInfo(role, lockString);
	}

}
