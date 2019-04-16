package com.channelsoft.ccod.session;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sicwen
 * @date 2019/04/15
 */
public class RedisSessionRepository implements SessionRepository{

    private RedisTemplate<Object,Object> redisTemplate;

    public RedisSessionRepository(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RedisSession createSession() {
        return new RedisSession();
    }

    @Override
    public void save(HttpSession session) {
        ((RedisSession)session).saveDelta();
    }

    @Override
    public RedisSession findById(String id) {
        return getSession(id, false);
    }

    @Override
    public void deleteById(String id) {
        redisTemplate.delete(id);
    }

    private RedisSession getSession(String id, boolean allowExpired) {
        Map<Object, Object> entries = getSessionBoundHashOperations(id).entries();
        if (entries.isEmpty()) {
            return null;
        }
        MapSession loaded = loadSession(id, entries);
        RedisSession result = new RedisSession(loaded);
        return result;
    }

    private MapSession loadSession(String id, Map<Object, Object> entries) {
        MapSession loaded = new MapSession(id);
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String key = (String) entry.getKey();
            loaded.setAttribute((String) entry.getKey(),entry.getValue());
        }
        return loaded;
    }

    public BoundHashOperations<Object, Object, Object> getSessionBoundHashOperations(String sessionId) {
        return redisTemplate.boundHashOps(sessionId);
    }


    public class RedisSession implements HttpSession {
        private MapSession cached;
        private Map<String, Object> delta = new HashMap<>();

        public RedisSession(){
            this(new MapSession());
        }

        public RedisSession(MapSession mapSession){
            this.cached = mapSession;
        }

        @Override
        public long getCreationTime() {
            return cached.getCreationTime();
        }

        @Override
        public String getId() {
            return cached.getId();
        }

        @Override
        public long getLastAccessedTime() {
            return cached.getLastAccessedTime();
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public void setMaxInactiveInterval(int interval) {
            cached.setMaxInactiveInterval(interval);
        }

        @Override
        public int getMaxInactiveInterval() {
            return cached.getMaxInactiveInterval();
        }

        @Override
        public HttpSessionContext getSessionContext() {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return cached.getAttribute(name);
        }

        @Override
        public Object getValue(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String[] getValueNames() {
            return new String[0];
        }

        @Override
        public void setAttribute(String name, Object value) {
            cached.setAttribute(name,value);
            //立即更新
            putAndFlush(name,value);
        }

        @Override
        public void putValue(String name, Object value) {

        }

        @Override
        public void removeAttribute(String name) {

        }

        @Override
        public void removeValue(String name) {

        }

        @Override
        public void invalidate() {

        }

        @Override
        public boolean isNew() {
            return false;
        }

        private void flushImmediateIfNecessary() {
            this.saveDelta();
        }

        private void putAndFlush(String a, Object v) {
            this.delta.put(a, v);
            this.flushImmediateIfNecessary();
        }

        public void saveDelta(){
            String sessionId = getId();
            if (this.delta.isEmpty()) {
                return;
            }
            getSessionBoundHashOperations(sessionId).putAll(this.delta);
        }
    }
}