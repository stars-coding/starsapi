package com.stars.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.mapper.InterfMapper;
import com.stars.common.model.entity.Interf;
import com.stars.common.service.InnerInterfService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 内部接口服务实现
 * 该服务提供了查询接口是否存在于数据库中的功能。
 *
 * @author stars
 */
@DubboService
public class InnerInterfServiceImpl implements InnerInterfService {

    @Autowired
    private InterfMapper interfMapper;

    /**
     * 获取接口
     * 从数据库中获取接口。
     *
     * @param interfUrl           接口URL
     * @param interfRequestMethod 接口请求方法
     * @return 如果接口存在，则返回对应的接口实体，否则返回null
     */
    @Override
    public Interf getInterf(String interfUrl, String interfRequestMethod) {
        // 如果URL和请求方法为空或空白，则抛出异常
        if (StringUtils.isAnyBlank(interfUrl, interfRequestMethod)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "URL和请求方法为空或空白");
        }
        // 构建查询条件
        LambdaQueryWrapper<Interf> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Interf::getInterfUrl, interfUrl).eq(Interf::getInterfRequestMethod, interfRequestMethod);
        // 调用查询
        Interf interf = this.interfMapper.selectOne(lqw);
        return interf;
    }
}
