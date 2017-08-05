package com.randioo.doudizhu_server.module.close.service;

import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface CloseService extends ObserveBaseServiceInterface {
	public void asynManipulate(RoleInterface roleInterface);

	public void roleDataCache2DB(RoleInterface role, boolean mustSave);
}
