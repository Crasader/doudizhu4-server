package com.randioo.doudizhu_server.module.login.service;

import org.apache.mina.core.session.IoSession;

import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.login.component.LoginConfig;
import com.randioo.doudizhu_server.protocol.Entity.RoleData;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface LoginService extends ObserveBaseServiceInterface {

    /**
     * 获得玩家数据
     * 
     * @param loginConfig 登录配置
     * @param ioSession
     * @author wcy 2017年8月4日
     */
    void getRoleData(LoginConfig loginConfig, IoSession ioSession);

    /**
     * 通过id获取玩家
     * 
     * @param roleId
     * @return
     * @author wcy 2017年1月10日
     */
    public Role getRoleById(int roleId);

    /**
     * 通过帐号获得玩家
     * 
     * @param account
     * @return
     * @author wcy 2017年1月10日
     */
    public Role getRoleByAccount(String account);

    RoleData getRoleData(Role role);

}
