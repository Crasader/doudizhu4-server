package com.randioo.doudizhu_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Fight.FightExitGameRequest;
import com.randioo.doudizhu_server.protocol.Fight.FightMingPaiRequest;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(FightMingPaiRequest.class)
public class FightMingPaiAction implements IActionSupport {

	@Autowired
	private FightService fightService;
	
	@Override
	public void execute(Object data, IoSession session) {
		FightMingPaiRequest request = (FightMingPaiRequest) data;
		boolean isMingPai = request.getIsMingPai();
		Role role = (Role) RoleCache.getRoleBySession(session);
		fightService.mingPai(role,isMingPai);
	}

}
