package com.channelsoft.ccod.filter;

import com.channelsoft.ccod.cookie.CookieHttpSessionIdResolver;
import com.channelsoft.ccod.cookie.HttpSessionIdResolver;
import com.channelsoft.ccod.session.SessionRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @author sicwen
 * @date 2019/03/26
 */
public class SessionRepositoryFilter implements Filter {

    /**
     * session的存取操作
     */
    private SessionRepository sessionRepository;

    /**
     * 从Http协议的cookie中解析出sessionId信息
     */
    private HttpSessionIdResolver httpSessionIdResolver = new CookieHttpSessionIdResolver();

    public SessionRepositoryFilter() {}

    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)){
            throw new RuntimeException("just supports HTTP requests");
        }
        //封装新的请求
        SessionRepositoryRequestWrapper newRequest = new SessionRepositoryRequestWrapper((HttpServletRequest) request, (HttpServletResponse) response);
        try {
            chain.doFilter(newRequest, response);
        }finally {
            //持久化session到redis
            //设置sessionId到cookie
            newRequest.commitSession();
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 新的Request请求
     */
    public class SessionRepositoryRequestWrapper extends HttpServletRequestWrapper {
        private HttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;
        private HttpSession currentSession;

        public SessionRepositoryRequestWrapper(HttpServletRequest request,HttpServletResponse response) {
            super(request);
            httpServletRequest = request;
            httpServletResponse = response;
        }

        /**
         * 在servlet中调用getSession会触发该方法
         * @return session
         */
        @Override
        public HttpSession getSession() {
            HttpSession session = getRequestedSession();
            //没有sessionId或sessionId已过期
            if(session == null){
                session = sessionRepository.createSession();
            }
            currentSession = session;
            return session;
        }

        /**
         * 持久化session
         * 设置sessionId到Cookie
         */
        public void commitSession(){
            HttpSession session = currentSession;
            sessionRepository.save(session);
            String sessionId = session.getId();
            httpSessionIdResolver.setSessionId(httpServletRequest, httpServletResponse, sessionId);
        }

        private HttpSession getRequestedSession() {
            //获取sessionId
            List<String> sessionIds = httpSessionIdResolver.resolveSessionIds(httpServletRequest);
            //获取session
            for (String sessionId : sessionIds) {
                HttpSession session = sessionRepository.findById(sessionId);
                if(session != null){
                    return session;
                }
            }
            return null;
        }
    }
}