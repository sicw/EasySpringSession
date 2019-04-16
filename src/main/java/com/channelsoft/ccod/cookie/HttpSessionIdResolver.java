package com.channelsoft.ccod.cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 从Http协议中解析SessionId
 * 可以从cookie,header中解析
 *
 * @author sicwen
 * @date 2019/04/15
 */
public interface HttpSessionIdResolver {

    /**
     * 获取sessionId
     * @param request
     * @return
     */
    List<String> resolveSessionIds(HttpServletRequest request);

    /**
     * 设置sessionId
     * @param request
     * @param response
     * @param sessionId
     */
    void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId);

    /**
     * 设置过期sessionId
     * @param request
     * @param response
     */
    void expireSession(HttpServletRequest request, HttpServletResponse response);
}