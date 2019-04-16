package com.channelsoft.ccod.session;

import javax.servlet.http.HttpSession;

/**
 * @author sicwen
 * @date 2019/04/15
 */
public interface SessionRepository {

    /**
     * 创建session
     * @return
     */
    HttpSession createSession();

    /**
     * 持久化session
     * @param session
     */
    void save(HttpSession session);

    /**
     * 查找session
     * @param id
     * @return
     */
    HttpSession findById(String id);

    /**
     * 删除session
     * @param id
     */
    void deleteById(String id);
}