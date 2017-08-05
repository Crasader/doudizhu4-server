package com.randioo.doudizhu_server.module.close.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.dao.RoleDAO;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.doudizhu_server.protocol.Fight.SCFightDisconnect;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.utils.SaveUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("closeService")
public class CloseServiceImpl extends ObserveBaseService implements CloseService {

	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private MatchService matchService;

	@Autowired
	private GameDB gameDB;

	@Autowired
	private FightService fightService;

	@Override
	public void asynManipulate(RoleInterface roleInterface) {
		// TODO Auto-generated method stub
		Role role = (Role) roleInterface;
		if(role ==null)
			return ;
		loggerinfo(role, "[account:" + role.getAccount() + ",name:" + role.getName() + "] manipulate");

		// 匹配取消
		matchService.matchCancel(role);
		roleInGameOption(role);

		role.setOfflineTimeStr(TimeUtils.getDetailTimeStr());
		if (!gameDB.isUpdatePoolClose()) {
			gameDB.getUpdatePool().submit(new EntityRunnable<Role>(role) {
				@Override
				public void run(Role role) {
					roleDataCache2DB(role, true);
				}
			});
		}
	}

	@Override
	public void roleDataCache2DB(RoleInterface roleInterface, boolean mustSave) {
		Role role = (Role) roleInterface;

		try {
			if (SaveUtils.needSave(role, mustSave)) {
				roleDAO.update(role);
			}
		} catch (Exception e) {
			loggererror("id:" + role.getRoleId() + ",account:" + role.getAccount() + ",name:" + role.getName()
					+ "] save error", e);
		}
	}

	public void roleInGameOption(Role role) {
		if (role.getGameId() == 0) {
			return;
		}
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			return;
		}
		RoleGameInfo myInfo = null;
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.roleId == role.getRoleId()) {
				myInfo = info;
				myInfo.online = false;
				break;
			}
		}
		if (game.getGameState() == GameState.GAME_STATE_PREPARE && game.getGameType() == GameType.GAME_TYPE_MATCH) {
			fightService.disconnectTimeUp(role.getRoleId());
		} else if (game.getGameState() == GameState.GAME_START_START) {
			if (game.getCallLandlordCount() < game.getMaxRoleCount() && game.getMultiple() < 3
					&& game.getGameType() == GameType.GAME_TYPE_MATCH) {
				if (game.getRoleIdMap().get(game.getRoleIdList().get(game.getCurrentRoleIdIndex())).roleId == role
						.getRoleId()) {

					fightService.callLandlord(game.getGameId(), myInfo.gameRoleId, 0);
				}
			}
		}
		// TODO 通知断线
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.roleId == role.getRoleId()) {
				continue;
			}
			SessionUtils.sc(
					info.roleId,
					SC.newBuilder()
							.setSCFightDisconnect(
									SCFightDisconnect.newBuilder().setSeated(
											game.getRoleIdList().indexOf(myInfo.gameRoleId))).build());
		}
	}
}
