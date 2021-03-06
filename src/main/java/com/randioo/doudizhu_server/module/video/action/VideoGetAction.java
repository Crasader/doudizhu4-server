package com.randioo.doudizhu_server.module.video.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.video.service.VideoService;
import com.randioo.doudizhu_server.protocol.Video.VideoGetRequest;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(VideoGetRequest.class)
public class VideoGetAction implements IActionSupport {

	@Autowired
	private VideoService videoService;

	@Override
	public void execute(Object data, IoSession session) {
		VideoGetRequest request = (VideoGetRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage sc = videoService.videoGet(role);
		SessionUtils.sc(role.getRoleId(), sc);
	}

}
