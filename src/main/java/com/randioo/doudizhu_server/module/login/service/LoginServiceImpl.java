package com.randioo.doudizhu_server.module.login.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.doudizhu_server.GlobleConstant;
import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.dao.RoleDAO;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.heartbeat.service.HeartbeatService;
import com.randioo.doudizhu_server.module.login.LoginConstant;
import com.randioo.doudizhu_server.module.login.component.LoginConfig;
import com.randioo.doudizhu_server.module.money.service.MoneyExchangeService;
import com.randioo.doudizhu_server.module.role.service.RoleService;
import com.randioo.doudizhu_server.protocol.Entity.RoleData;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Login.LoginGetRoleDataResponse;
import com.randioo.doudizhu_server.protocol.Login.SCLoginGetRoleData;
import com.randioo.doudizhu_server.protocol.Login.SCLoginOtherSide;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.doudizhu_server.util.Tool;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.module.login.Facility;
import com.randioo.randioo_server_base.module.login.LoginHandler;
import com.randioo.randioo_server_base.module.login.LoginInfo;
import com.randioo.randioo_server_base.module.login.LoginModelConstant;
import com.randioo.randioo_server_base.module.login.LoginModelService;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.template.Ref;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("loginService")
public class LoginServiceImpl extends ObserveBaseService implements LoginService {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private LoginModelService loginModelService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private HeartbeatService heartbeatService;

    @Autowired
    private MoneyExchangeService moneyExchangeService;

    @Autowired
    private GameDB gameDB;

    @Override
    public void init() {
        // 初始化所有已经有过的帐号和昵称
        add(RoleCache.getNameSet(), roleDAO.getAllNames());
        add(RoleCache.getAccountSet(), roleDAO.getAllAccounts());

        loginModelService.setLoginHandler(new LoginHandlerImpl());
    }

    private void add(Map<String, String> map, List<String> list) {
        for (String str : list) {
            map.put(str, str);
        }
    }

    private class LoginHandlerImpl implements LoginHandler {

        @Override
        public RoleInterface getRoleInterfaceFromDBById(int roleId) {
            return roleDAO.get(null, roleId);
        }

        @Override
        public RoleInterface getRoleInterfaceFromDBByAccount(String account) {
            return roleDAO.get(account, 0);
        }

        @Override
        public void loginRoleModuleDataInit(RoleInterface roleInterface) {
            // 将数据库中的数据放入缓存中
            Role role = (Role) roleInterface;
            role = (Role) getRoleInterfaceFromDBById(role.getRoleId());

            System.out.println(role);
        }

        @Override
        public boolean createRoleCheckAccount(LoginInfo info, Ref<Integer> errorCode) {
            // 账号姓名不可为空
            if (StringUtils.isNullOrEmpty(info.getAccount())) {
                errorCode.set(LoginConstant.CREATE_ROLE_NAME_SENSITIVE);
                return false;
            }

            return true;
        }

        @Override
        public RoleInterface createRole(LoginInfo loginInfo) {
            String account = loginInfo.getAccount();
            // 用户数据
            // 创建用户
            Role role = new Role();
            role.setAccount(account);

            roleService.newRoleInit(role);
            moneyExchangeService.newRoleInit(role);

            gameDB.getInsertPool().submit(new EntityRunnable<Role>(role) {

                @Override
                public void run(Role entity) {
                    try {
                        roleDAO.insert(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return role;
        }

        @Override
        public Facility saveFacility(Facility facility) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void noticeOtherPlaceLogin(Facility oldFacility) {
            IoSession session = oldFacility.getSession();
            SessionUtils.sc(session, SC.newBuilder().setSCLoginOtherSide(SCLoginOtherSide.newBuilder()).build());
        }

        @Override
        public Facility getFacilityFromDB(int roleId, String macAddress) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    @Override
    public void getRoleData(LoginConfig loginConfig, IoSession ioSession) {

        // 帐号必须能转换成数字
        if (!this.checkAccountStringisNumber(loginConfig.getAccount())) {
            SC sc = SC
                    .newBuilder()
                    .setLoginGetRoleDataResponse(
                            LoginGetRoleDataResponse.newBuilder().setErrorCode(ErrorCode.ACCOUNT_ILLEGEL.getNumber()))
                    .build();
            SessionUtils.sc(ioSession, sc);
            return;
        }

        Ref<Integer> errorCode = new Ref<>();

        RoleInterface roleInterface = null;
        try {
            roleInterface = loginModelService.getRoleData(loginConfig, errorCode, ioSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (roleInterface != null) {
            Role role = (Role) roleInterface;
            // 登陆消息返回
            SessionUtils.sc(ioSession,
                    SC.newBuilder().setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder()).build());

            // 聊天持有的关键字
            String chatKey = MessageFormat.format(LoginConstant.CHAT_KEY_FORMAT,
                    GlobleMap.Int(GlobleConstant.ARGS_PORT));
            boolean isGaming = this.isInGaming(role);
            RoleData roleData = getRoleData(role);
            int serverTime = TimeUtils.getNowTime();

            SessionUtils.sc(
                    ioSession,
                    SC.newBuilder()
                            .setSCLoginGetRoleData(
                                    SCLoginGetRoleData.newBuilder().setServerTime(serverTime).setRoleData(roleData)
                                            .setGame(chatKey).setIsGaming(isGaming)).build());
            
            // 查看玩家有没有保持的主推，有的话发送
            if (role.getSc() != null) {
                SessionUtils.sc(ioSession, role.getSc());
                role.setSc(null);
            }

            return;
        }

        ErrorCode errorEnum = null;
        switch (errorCode.get()) {
        case LoginModelConstant.GET_ROLE_DATA_NOT_EXIST:
            errorEnum = ErrorCode.NO_ROLE_DATA;
            break;
        case LoginModelConstant.GET_ROLE_DATA_IN_LOGIN:
            errorEnum = ErrorCode.IN_LOGIN;
            break;
        }
        SC sc = SC.newBuilder()
                .setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder().setErrorCode(errorEnum.getNumber()))
                .build();
        SessionUtils.sc(ioSession, sc);
    }

    /**
     * 是否在游戏中
     * 
     * @param role
     * @return
     * @author wcy 2017年8月4日
     */
    private boolean isInGaming(Role role) {
        int gameId = role.getGameId();
        if (gameId == 0) {
            return false;
        }
        return GameCache.getGameMap().containsKey(gameId);
    }

    /**
     * 检查帐号是否是数字
     * 
     * @param account
     * @author wcy 2017年8月4日
     */
    private boolean checkAccountStringisNumber(String account) {
        try {
            Integer.parseInt(account);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public RoleData getRoleData(Role role) {
        roleService.roleInit(role);
        heartbeatService.heartInit(role);

        int roleId = (Tool.regExpression(role.getAccount(), "[0-9]*") ? Integer.parseInt(role.getAccount()) : role
                .getRoleId());

        String name = role.getName();
        int money = role.getMoney();
        int musicVolume = role.getMusicVolume();
        int volume = role.getVolume();
        int randiooMoney = role.getRandiooMoney();
        String headImageUrl = role.getHeadImgUrl();
        int roomCards = role.getRoomCards();

        RoleData roleData = RoleData.newBuilder().setRoleId(roleId).setName(name).setMoney(money)
                .setMusicVolume(musicVolume).setVolume(volume).setRandiooMoney(randiooMoney)
                .setHeadImgUrl(headImageUrl).setRoomCards(roomCards).build();

        return roleData;
    }

    @Override
    public Role getRoleById(int roleId) {
        RoleInterface roleInterface = loginModelService.getRoleInterfaceById(roleId);
        return roleInterface == null ? null : (Role) roleInterface;
    }

    @Override
    public Role getRoleByAccount(String account) {
        RoleInterface roleInterface = loginModelService.getRoleInterfaceByAccount(account);
        return roleInterface == null ? null : (Role) roleInterface;
    }
}
