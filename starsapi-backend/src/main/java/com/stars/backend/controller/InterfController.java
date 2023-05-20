package com.stars.backend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stars.backend.annotation.AuthCheck;
import com.stars.backend.common.*;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.model.dto.interf.InterfAddRequest;
import com.stars.backend.model.dto.interf.InterfQueryRequest;
import com.stars.backend.model.dto.interf.InterfUpdateRequest;
import com.stars.backend.model.enums.InterfStatusEnum;
import com.stars.backend.service.InterfService;
import com.stars.backend.service.UserInvokeInterfService;
import com.stars.backend.service.UserService;
import com.stars.common.model.entity.Interf;
import com.stars.common.model.entity.User;
import com.stars.common.model.entity.UserInvokeInterf;
import com.stars.common.model.vo.InterfVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.stars.backend.constant.CommonConstant.SORT_ORDER_ASC;
import static com.stars.backend.constant.RedisConstants.CACHE_INTERF_KEY;
import static com.stars.backend.constant.RedisConstants.LOCK_INTERF_BY_BREAK;

/**
 * 接口控制器
 *
 * @author stars
 */
@RestController
@Slf4j
@RequestMapping("/interf")
public class InterfController {

    @Resource
    private InterfService interfService;

    @Resource
    private UserInvokeInterfService userInvokeInterfService;

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加接口
     *
     * @param interfAddRequest 接口添加请求对象
     * @param request          请求对象
     * @return 新增接口的ID
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterf(@RequestBody InterfAddRequest interfAddRequest,
                                        HttpServletRequest request) {
        // 如果接口添加请求为空，则抛出异常
        if (interfAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口添加请求为空");
        }
        // 创建接口
        Interf interf = new Interf();
        // 复制接口添加请求中的相关属性至接口本身
        BeanUtils.copyProperties(interfAddRequest, interf);
        // 验证接口（接口合法性、接口唯一性、接口可用性）
        this.interfService.validInterf(interf, true);
        // 获取当前登录用户
        User loginUser = this.userService.getLoginUser(request);
        // 接口的创建者属性赋值
        long userId = loginUser.getId();
        interf.setInterfUserId(userId);
        // 保存接口
        boolean result = this.interfService.save(interf);
        // 如果接口添加失败，则抛出异常
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口添加失败");
        }
        // todo 有点问题，待研究
        // 添加【用户调用接口】
        long id = interf.getId();
        result = this.userInvokeInterfService.addInvokeCount(id, userId);
        // 如果【用户调用接口】添加失败，则抛出异常
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户调用接口添加失败");
        }
        // todo 这里是否需要添加Redis缓存
        // 获取新接口的ID
        long newInterfId = interf.getId();
        // 返回一个成功的响应，响应体中携带新接口的ID值
        return ResultUtils.success(newInterfId);
    }

    /**
     * 删除接口
     * 需要接口创建者或管理员权限。
     *
     * @param deleteRequest 删除请求对象
     * @param request       请求对象
     * @return 是否成功删除接口
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterf(@RequestBody DeleteRequest deleteRequest,
                                              HttpServletRequest request) {
        // 如果删除请求为空，则抛出异常
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除请求为空");
        }
        // 如果删除请求中的ID小于或等于零，则抛出异常
        if (deleteRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除请求中的ID小于等于零");
        }
        // 获取当前登录用户
        User loginUser = this.userService.getLoginUser(request);
        // 查询ID对应的接口
        long id = deleteRequest.getId();
        Interf oldInterf = this.interfService.getById(id);
        // 如果查询的接口为空，表示接口不存在，则抛出异常
        if (oldInterf == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "查询的接口不存在");
        }
        // 检查权限，只有接口创建者或管理员才可以删除接口
        if (!oldInterf.getInterfUserId().equals(loginUser.getId()) && !this.userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无权限删除此接口");
        }
        // 删除接口
        boolean result = this.interfService.removeById(id);
        // 如果接口删除失败，则抛出异常
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口删除失败");
        }
        // 创建【用户调用接口】
        UserInvokeInterf userInvokeInterf = new UserInvokeInterf();
        userInvokeInterf.setInterfId(id);
        // todo 有点问题，待研究
        // 删除【用户调用接口】
        result = this.userInvokeInterfService.removeById(userInvokeInterf);
        // 如果【用户调用接口】删除失败，则抛出异常
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户调用接口删除失败");
        }
        // todo 这里是否需要删除Redis缓存
        // 删除缓存
        String key = CACHE_INTERF_KEY + id;
        this.stringRedisTemplate.delete(key);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 更新接口
     * 需要接口创建者或管理员权限。
     *
     * @param interfUpdateRequest 更新接口请求对象
     * @param request             请求对象
     * @return 是否成功更新接口
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterf(@RequestBody InterfUpdateRequest interfUpdateRequest,
                                              HttpServletRequest request) {
        // 如果接口更新请求为空，则抛出异常
        if (interfUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口更新请求为空");
        }
        // 如果接口更新请求中的ID小于等于零，则抛出异常
        if (interfUpdateRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口更新请求中的ID小于等于零");
        }
        // 创建接口
        Interf interf = new Interf();
        // 复制接口更新请求中的相关属性至接口本身
        BeanUtils.copyProperties(interfUpdateRequest, interf);
        // 验证接口（接口合法性、接口唯一性、接口可用性）
        this.interfService.validInterf(interf, false);
        // 获取当前登录用户
        User user = this.userService.getLoginUser(request);
        // 查询ID对应的接口
        long id = interfUpdateRequest.getId();
        Interf oldInterf = this.interfService.getById(id);
        // 如果查询的接口为空，表示接口不存在，则抛出异常
        if (oldInterf == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "查询的接口不存在");
        }
        // 检查权限，只有接口创建者或管理员才可以更新接口
        if (!oldInterf.getInterfUserId().equals(user.getId()) && !this.userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无权限更新此接口");
        }
        // 更新接口
        boolean result = this.interfService.updateById(interf);
        // todo 这里是否需要删除Redis缓存
        // 删除缓存
        String key = CACHE_INTERF_KEY + id;
        this.stringRedisTemplate.delete(key);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 获取指定ID的接口
     *
     * @param id 接口的唯一标识符
     * @return 包含接口的响应对象
     */
    @GetMapping("/get")
    public BaseResponse<Interf> getInterfById(long id) {
        // 如果ID小于等于零，则抛出异常
        if (id <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID小于等于零");
        }
        // 尝试从缓存中获取接口
        String key = CACHE_INTERF_KEY + id;
        Map<Object, Object> entries = this.stringRedisTemplate.opsForHash().entries(key);
        String o = (String) entries.get("");
        // 如果缓存存在且不为空，则封装返回
        if (CollUtil.isNotEmpty(entries) && !"".equals(o)) {
            // 从缓存中封装接口
            Interf interf = BeanUtil.fillBeanWithMap(entries, new Interf(), false);
            // 返回一个成功的响应，响应体中携带interf信息
            return ResultUtils.success(interf);
        }
        // 如果缓存为空，表示缓存穿透，则抛出异常
        if ("".equals(o)) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "接口未找到");
        }
        // 使用互斥锁解决缓存击穿
        SimpleRedisLock lock = new SimpleRedisLock(this.stringRedisTemplate, LOCK_INTERF_BY_BREAK + id);
        Interf interf = null;
        try {
            // 尝试获取互斥锁，最多等待1200毫秒
            boolean isLock = lock.tryLock(1200);
            // 如果获取锁失败，等待50毫秒后再次尝试
            if (!isLock) {
                Thread.sleep(50);
                // 继续调用
                return this.getInterfById(id);
            }
            // 缓存不存在，查询接口
            interf = this.interfService.getById(id);
            // 如果不存在查询的接口，表示缓存穿透，则抛出异常
            if (BeanUtil.isEmpty(interf)) {
                // 缓存空对象，解决缓存穿透
                this.stringRedisTemplate.opsForHash().put(key, "", "");
                this.stringRedisTemplate.expire(key, 30L, TimeUnit.MINUTES);
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "接口未找到");
            }
            // 存在查询的接口，将接口封装至MAP对象放到缓存中
            Map<String, Object> map = BeanUtil.beanToMap(interf, new HashMap<>(),
                    CopyOptions.create().setIgnoreNullValue(false)
                            .setFieldValueEditor((keys, values) -> {
                                if (values == null) {
                                    values = null;
                                } else {
                                    values = values.toString();
                                }
                                return values;
                            }));
            // 将MAP对象放进Redis中，并设置超时时间
            this.stringRedisTemplate.opsForHash().putAll(key, map);
            this.stringRedisTemplate.expire(key, 30L, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 释放互斥锁
            lock.unlock();
        }
        // 返回一个成功的响应，响应体中携带interf信息
        return ResultUtils.success(interf);
    }

    /**
     * 列出所有接口
     * 需要管理员权限。
     *
     * @param interfQueryRequest 包含接口查询条件的请求体
     * @return 包含接口列表的响应对象
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Interf>> listInterf(InterfQueryRequest interfQueryRequest,
                                                 HttpServletRequest request) {
        // 创建接口
        Interf interfQuery = new Interf();
        // 如果接口查询请求不为空，则复制接口查询请求中的相关属性至接口本身，表示查询指定信息的接口
        // 如果接口查询请求为空，则表示查询所有接口
        if (interfQueryRequest != null) {
            BeanUtils.copyProperties(interfQueryRequest, interfQuery);
        }
        // 查询接口
        QueryWrapper<Interf> queryWrapper = new QueryWrapper<>(interfQuery);
        List<Interf> interfList = this.interfService.list(queryWrapper);
        // 返回一个成功的响应，响应体中携带interfList信息
        return ResultUtils.success(interfList);
    }

    /**
     * 分页列出接口
     *
     * @param interfQueryRequest 包含接口查询条件的请求体
     * @param request            请求对象
     * @return 包含分页接口列表的响应对象
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Interf>> listInterfByPage(InterfQueryRequest interfQueryRequest,
                                                       HttpServletRequest request) {
        // 如果接口查询请求为空，则抛出异常
        if (interfQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口查询请求为空");
        }
        // 创建接口
        Interf interfQuery = new Interf();
        // 复制接口查询请求中的相关属性至接口本身
        BeanUtils.copyProperties(interfQueryRequest, interfQuery);
        // 获取分页查询的相关信息
        long current = interfQueryRequest.getCurrent();
        long size = interfQueryRequest.getPageSize();
        String sortField = interfQueryRequest.getSortField();
        String sortOrder = interfQueryRequest.getSortOrder();
        String interfDescription = interfQuery.getInterfDescription();
        // 将查询的接口描述置空，原因在于描述无关且过长
        interfQuery.setInterfDescription(null);
        // 如果分页尺寸大于50，则抛出异常
        if (size > 50L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分页尺寸过大");
        }
        // 查询接口
        QueryWrapper<Interf> queryWrapper = new QueryWrapper<>(interfQuery);
        queryWrapper.like(StringUtils.isNotBlank(interfDescription), "interfDescription", interfDescription);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(SORT_ORDER_ASC), sortField);
        Page<Interf> interfPage = this.interfService.page(new Page<>(current, size), queryWrapper);
        // 返回一个成功的响应，响应体中携带interfPage信息
        return ResultUtils.success(interfPage);
    }

    /**
     * 发布接口
     * 需要管理员权限。
     *
     * @param idRequest 包含待发布接口ID的请求体
     * @param request   请求对象
     * @return 包含发布结果的响应对象
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterf(@RequestBody IdRequest idRequest,
                                              HttpServletRequest request) {
        // 如果ID请求为空，则抛出异常
        if (idRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID请求为空");
        }
        // 如果ID请求中的ID小于等于零，则抛出异常
        if (idRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID请求中的ID小于等于零");
        }
        // 查询ID对应的接口
        long id = idRequest.getId();
        Interf interf = this.interfService.getById(id);
        // 如果查询的接口为空，表示接口不存在，则抛出异常
        if (interf == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 创建接口
        Interf updateInterf = new Interf();
        updateInterf.setId(id);
        // 修改接口的状态为上线
        updateInterf.setInterfStatus(InterfStatusEnum.ONLINE.getValue());
        // 更新接口
        boolean result = this.interfService.updateById(updateInterf);
        // todo 这里是否需要删除Redis缓存
        // 删除缓存
        String key = CACHE_INTERF_KEY + id;
        this.stringRedisTemplate.delete(key);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 下线接口
     * 需要管理员权限。
     *
     * @param idRequest 包含待下线接口ID的请求体
     * @param request   请求对象
     * @return 包含下线结果的响应对象
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterf(@RequestBody IdRequest idRequest,
                                               HttpServletRequest request) {
        // 如果ID请求为空，则抛出异常
        if (idRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID请求为空");
        }
        // 如果ID请求中的ID小于等于零，则抛出异常
        if (idRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID请求中的ID小于等于零");
        }
        // 查询ID对应的接口
        long id = idRequest.getId();
        Interf interf = this.interfService.getById(id);
        // 如果查询的接口为空，表示接口不存在，则抛出异常
        if (interf == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 创建接口
        Interf updateInterf = new Interf();
        updateInterf.setId(id);
        // 修改接口的状态为下线
        updateInterf.setInterfStatus(InterfStatusEnum.OFFLINE.getValue());
        // 更新接口
        boolean result = this.interfService.updateById(updateInterf);
        // todo 这里是否需要删除Redis缓存
        // 删除缓存
        String key = CACHE_INTERF_KEY + id;
        this.stringRedisTemplate.delete(key);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 获取所有接口的名称列表
     *
     * @return 包含接口名称列表的响应对象
     */
    @GetMapping("/interfNameList")
    public BaseResponse<Map> interfNameList() {
        // 获取所有的接口
        List<Interf> list = this.interfService.list();
        // todo 这里需要改进
        // 创建MAP对象，用于存储接口名称和接口名称的映射关系
        Map<Object, Object> interfNameMap = new HashMap<>();
        // 遍历接口列表，将接口名称添加到映射中
        for (Interf interf : list) {
            String name = interf.getInterfName();
            interfNameMap.put(name, name);
        }
        // 返回一个成功的响应，响应体中携带interfNameMap信息
        return ResultUtils.success(interfNameMap);
    }

    /**
     * 获取当前用户拥有的接口列表
     * 需要用户登录。
     *
     * @param interfQueryRequest 包含查询条件的请求对象
     * @param request            请求对象
     * @return 包含当前用户接口列表的响应对象
     */
    @GetMapping("/myInterf")
    public BaseResponse<PageHelper<InterfVO>> selectMyInterf(InterfQueryRequest interfQueryRequest,
                                                             HttpServletRequest request) {
        // 获取当前登录用户的ID
        long id = this.userService.getLoginUser(request).getId();
        // 设置接口查询请求中的接口创建人属性为当前用户
        interfQueryRequest.setInterfUserId(id);
        // 查询当前用户拥有的接口
        PageHelper<InterfVO> myInterf = this.interfService.getMyInterf(interfQueryRequest);
        // 返回一个成功的响应，响应体中携带myInterf信息
        return ResultUtils.success(myInterf);
    }
}
