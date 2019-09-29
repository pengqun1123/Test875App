package com.face.db;

import java.util.List;

/**
 * 用户信息管理接口
 */
public interface UserManager {

    /**
     * 查询一个用户
     *
     * @param id 用户ID
     * @return
     */
    User findOne(long id);

    /**
     * 查询多个用户
     *
     * @param limit  查询最大条数
     * @param offset 查询起始位移量
     * @return
     */
    List<User> find(int limit, int offset);

    /**
     * 增加一个用户
     *
     * @param user
     * @return 返回用户ID
     */
    long addOne(User user);

    /**
     * 删除一个用户
     *
     * @param id 用户ID
     * @return
     */
    boolean deleteOne(long id);

}
