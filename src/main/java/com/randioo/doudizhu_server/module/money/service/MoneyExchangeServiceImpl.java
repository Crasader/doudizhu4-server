package com.randioo.doudizhu_server.module.money.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.login.service.LoginService;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.MoneyExchange.MoneyExchangeResponse;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("moneyExchangeService")
public class MoneyExchangeServiceImpl extends ObserveBaseService implements MoneyExchangeService {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RandiooPlatformSdk randiooPlatformSdk;

    @Override
    public void newRoleInit(Role role) {
        role.setMoneyExchangeTimeStr(TimeUtils.getCurrentTimeStr());
    }

    @Override
    public GeneratedMessage moneyExchange(Role role, boolean add, int num) {
        System.out.println("@@@" + role.getAccount());
        int max = role.getMoneyExchangeNum();
        String today = TimeUtils.getCurrentTimeStr();
        String time = role.getMoneyExchangeTimeStr();
        int addMoney = num;
        double charge = 0;
        if (num % 1000 != 0 || num * 0.001 < 1) {
            return SC.newBuilder()
                    .setMoneyExchangeResponse(
                            MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()))
                    .build();
        }

        if (add) {
            charge = randioo2bean(num);
            if (role.getRandiooMoney() < charge) {
                return SC.newBuilder()
                        .setMoneyExchangeResponse(
                                MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()))
                        .build();
            }
        } else {
            // TODO 每天5W

            if (time == null) {
                max = num;
            } else {
                time = time.substring(0, 10);
                if (today.equals(time)) {
                    if (max + num > 50000) {
                        SC.newBuilder().setMoneyExchangeResponse(
                                MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()))
                                .build();
                    }
                    max += num;
                } else {
                    if (num > 50000) {
                        SC.newBuilder().setMoneyExchangeResponse(
                                MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()))
                                .build();
                    }
                    max = num;
                }
            }

            charge = bean2randioo(num);
            if (role.getMoney() < num) {
                SC.newBuilder()
                        .setMoneyExchangeResponse(
                                MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()))
                        .build();
            }

        }
        if (exchangeMoney(role, charge, add)) {
            role.setMoney(role.getMoney() + (add ? 1 : -1) * addMoney);
            if (!add) {
                role.setMoneyExchangeTimeStr(today);
                role.setMoneyExchangeNum(max);
                System.out.println("@@@@" + today + "--" + max);
            }
            return SC
                    .newBuilder().setMoneyExchangeResponse(MoneyExchangeResponse.newBuilder()
                            .setErrorCode(ErrorCode.OK.getNumber()).setRoleData(loginService.getRoleData(role)))
                    .build();
        } else {
            return SC.newBuilder().setMoneyExchangeResponse(
                    MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber())).build();
        }

    }

    private int randioo2bean(int bean) {
        return (int) (bean * 0.1);
    }

    private int bean2randioo(int bean) {
        return (int) (bean * 0.085);
    }

    @Override
    public boolean exchangeMoney(Role role, double money, boolean add) {

        money = add ? -money : money;
        try {
            randiooPlatformSdk.addMoney(role.getAccount(), money);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
