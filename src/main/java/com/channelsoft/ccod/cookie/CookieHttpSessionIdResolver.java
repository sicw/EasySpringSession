package com.channelsoft.ccod.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sicwen
 * @date 2019/04/15
 */
public class CookieHttpSessionIdResolver implements HttpSessionIdResolver {

    private final String SESSION_NAME = "SHARE_SESSION_ID";

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        List<String> result = new ArrayList<String>();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(SESSION_NAME.equals(cookie.getName())){
                    result.add(cookie.getValue());
                }
            }
        }
        return result;
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(SESSION_NAME,sessionId);
        response.addCookie(cookie);
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
    }
}