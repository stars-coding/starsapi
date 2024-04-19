package com.stars.backend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.common.PageHelper;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.mapper.InterfMapper;
import com.stars.backend.model.dto.interf.InterfQueryRequest;
import com.stars.backend.service.InterfService;
import com.stars.common.model.entity.Interf;
import com.stars.common.model.vo.InterfVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 接口服务实现
 * 提供接口信息的管理和查询功能，包括接口验证和分页查询等操作。
 *
 * @author stars
 */
@Service
public class InterfServiceImpl extends ServiceImpl<InterfMapper, Interf> implements InterfService {

    @Resource
    private InterfMapper interfMapper;

    /**
     * 验证接口
     * 根据添加或更新操作验证接口信息的有效性。
     *
     * @param interf 接口
     * @param add    是否为添加操作
     */
    @Override
    public void validInterf(Interf interf, boolean add) {
        // todo 先处理添加和更新的公共逻辑（接口合法性、唯一性、可用性），再分别处理添加和更新的特有逻辑。
        if (interf == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证失败，接口为空");
        }
        String name = interf.getInterfName();
        if (StringUtils.isBlank(name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证失败，接口名称为空或接口名称为空白字符");
        }
        if (StringUtils.isAnyBlank(name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证失败，接口名称中包含空白字符");
        }
        if (name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证失败，接口名称过长");
        }
    }

    /**
     * 获取我的接口列表
     * 根据查询请求获取当前用户的接口列表并进行分页。
     *
     * @param interfQueryRequest 查询请求
     * @return 接口列表的分页信息
     */
    @Override
    public PageHelper<InterfVO> getMyInterf(InterfQueryRequest interfQueryRequest) {
        int pageSize = interfQueryRequest.getPageSize();
        int pageNum = interfQueryRequest.getCurrent();
        long userId = interfQueryRequest.getInterfUserId();
        int count = this.interfMapper.selectMyInterfCount(userId);
        int pageCount = (count % pageSize == 0) ? (count / pageSize) : (count / pageSize + 1);
        int start = (pageNum - 1) * pageSize;
        String sortField = interfQueryRequest.getSortField();
        String sortOrder = interfQueryRequest.getSortOrder();
        if (StrUtil.isBlank(sortField)) {
            sortField = "i.id";
        }
        String description = interfQueryRequest.getInterfDescription();
        description = "%" + description + "%";
        List<InterfVO> interfVOS = this.interfMapper
                .selectMyInterfByPage(userId, start, pageSize, sortField, sortOrder, description);
        PageHelper<InterfVO> interfVOPageHelper = new PageHelper<>(count, pageCount, interfVOS);
        return interfVOPageHelper;
    }
}
