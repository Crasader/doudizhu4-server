package com.randioo.doudizhu_server.module.login.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.doudizhu_server.module.login.component.LoginConfig;
import com.randioo.doudizhu_server.module.login.service.LoginService;
import com.randioo.doudizhu_server.protocol.Login.LoginGetRoleDataRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(LoginGetRoleDataRequest.class)
public class LoginGetRoleDataAction implements IActionSupport {

    @Autowired
    private LoginService loginService;

    @Override
    public void execute(Object data, IoSession session) {
        LoginGetRoleDataRequest request = (LoginGetRoleDataRequest) data;

        // 登录配置
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setAccount(request.getAccount());
        loginConfig.setMacAddress(request.getUuid());
        loginConfig.setHeadImageUrl(request.getHeadImageUrl());

        loginService.getRoleData(loginConfig, session);
    }

}
