package com.randioo.doudizhu_server.util;

import org.apache.mina.core.session.IoSession;

import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.template.Function;

public class SessionUtils {
	private static Function function = null;

	public static void initFunction(Function func) {
		function = func;
	}

	public static void sc(IoSession session, Object message) {
		if (session != null) {
			if (function != null)
				function.apply(session, message);

			session.write(message);
		}
	}

	public static void sc(int roleId, Object message) {
		IoSession session = SessionCache.getSessionById(roleId);
		sc(session, message);
	}

}
