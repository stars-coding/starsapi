package com.stars.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stars.common.model.entity.UserInvokeInterf;

import java.util.List;

/**
 * 用户调用接口数据库操作
 *
 * @author stars
 */
public interface UserInvokeInterfMapper extends BaseMapper<UserInvokeInterf> {

    List<UserInvokeInterf> listTopInvokeInterf(int paramInt);
}
