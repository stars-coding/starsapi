package com.stars.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.stars.backend.annotation.AuthCheck;
import com.stars.backend.common.BaseResponse;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.common.ResultUtils;
import com.stars.backend.constant.UserConstant;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.mapper.UserInvokeInterfMapper;
import com.stars.backend.service.InterfService;
import com.stars.common.model.entity.Interf;
import com.stars.common.model.entity.UserInvokeInterf;
import com.stars.common.model.vo.AnalysisInterfVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据分析控制器
 *
 * @author stars
 */
@RestController
@Slf4j
@RequestMapping("/analysis")
public class AnalysisController {

    @Resource
    private UserInvokeInterfMapper userInvokeInterfMapper;

    @Resource
    private InterfService interfService;

    /**
     * 获取调用次数最多的接口列表
     *
     * @return 包含分析结果的响应对象
     */
    @GetMapping("/top/interf/invoke")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<AnalysisInterfVO>> listTopInvokeInterf() {
        // 获取调用次数最多的用户接口列表
        List<UserInvokeInterf> userInvokeInterfList = this.userInvokeInterfMapper.listTopInvokeInterf(3);
        // 根据接口ID分组映射
        Map<Long, List<UserInvokeInterf>> interfIdObjMap =
                userInvokeInterfList.stream().collect(Collectors.groupingBy(UserInvokeInterf::getInterfId));
        // 构建查询条件，查询与接口ID匹配的接口
        QueryWrapper<Interf> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfIdObjMap.keySet());
        // 查询接口列表
        List<Interf> list = this.interfService.list(queryWrapper);
        // 如果接口列表为空，则抛出异常
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取失败，接口列表为空");
        }
        // 构建分析接口视图
        List<AnalysisInterfVO> collect = list.stream().map(interf -> {
            AnalysisInterfVO analysisInterfVO = new AnalysisInterfVO();
            BeanUtils.copyProperties(interf, analysisInterfVO);
            analysisInterfVO.setTotalInvokeNum(interfIdObjMap.get(interf.getId()).get(0).getTotalInvokeNum());
            return analysisInterfVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }
}
