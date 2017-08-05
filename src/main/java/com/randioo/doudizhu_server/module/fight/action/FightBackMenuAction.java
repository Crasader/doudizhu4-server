package com.randioo.doudizhu_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Fight.FightBackMenuRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(FightBackMenuRequest.class)
public class FightBackMenuAction implements IActionSupport {
	@Autowired
	private FightService fightService;

	@Override
	public void execute(Object data, IoSession session) {
		FightBackMenuRequest request = (FightBackMenuRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		fightService.backMenu(role);
	}
}
