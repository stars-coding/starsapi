package com.stars.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stars.common.model.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据库操作
 *
 * @author stars
 */
public interface UserMapper extends BaseMapper<User> {

    int selectUserCount(@Param("userAccount") String paramString);
}
