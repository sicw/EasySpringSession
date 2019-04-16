package com.channelsoft.ccod.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author sicwen
 * @date 2019/03/26
 */
public class MapSession implements HttpSession, Serializable {

    private Map<String,Object> sessionAttrs = new HashMap<String,Object>(16);
    private long creationTime = System.currentTimeMillis();
    private long lastAccessedTime = this.creationTime;
    private String id;
    private int maxInactiveInterval;

    public MapSession(){
        this(generateId());
    }

    public MapSession(String id){
        this.id = id;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return sessionAttrs.get(name);
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
        if (value == null) {
            removeAttribute(name);
        }
        else {
            this.sessionAttrs.put(name, value);
        }
    }

    @Override
    public void putValue(String name, Object value) {

    }

    @Override
    public void removeAttribute(String name) {
        sessionAttrs.remove(name);
    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {
        maxInactiveInterval = 0;
    }

    @Override
    public boolean isNew() {
        return false;
    }
}