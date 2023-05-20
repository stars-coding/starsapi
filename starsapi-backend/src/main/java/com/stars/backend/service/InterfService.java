package com.stars.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stars.backend.common.PageHelper;
import com.stars.backend.model.dto.interf.InterfQueryRequest;
import com.stars.common.model.entity.Interf;
import com.stars.common.model.vo.InterfVO;

/**
 * 接口服务接口
 * 提供接口信息的管理和查询功能，包括接口验证和分页查询等操作。
 *
 * @author stars
 */
public interface InterfService extends IService<Interf> {

    /**
     * 验证接口信息
     * 根据添加或更新操作验证接口信息的有效性。
     *
     * @param interf 接口信息
     * @param add    是否为添加操作
     */
    void validInterf(Interf interf, boolean add);

    /**
     * 获取我的接口列表
     * 根据查询请求获取当前用户的接口列表并进行分页。
     *
     * @param interfQueryRequest 查询请求
     * @return 接口列表的分页信息
     */
    PageHelper<InterfVO> getMyInterf(InterfQueryRequest interfQueryRequest);
}
