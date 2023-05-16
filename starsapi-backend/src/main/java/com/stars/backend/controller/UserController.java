package com.stars.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stars.backend.common.*;
import com.stars.backend.constant.RedisConstants;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.model.dto.user.UserLoginRequest;
import com.stars.backend.model.dto.user.UserQueryRequest;
import com.stars.backend.model.dto.user.UserRegisterRequest;
import com.stars.backend.model.dto.user.UserUpdateRequest;
import com.stars.backend.service.UserService;
import com.stars.common.model.entity.User;
import com.stars.common.model.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.stars.backend.constant.RedisConstants.CACHE_USERINFO_KEY;

/**
 * 用户控制器
 *
 * @author stars
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求对象
     * @return 注册后的用户ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 如果用户注册请求为空，则抛出异常
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户注册请求为空");
        }
        // 获取用户注册请求中的参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 如果用户注册请求中的参数为空或空白，则抛出异常
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户注册请求中的参数为空或空白");
        }
        long result = this.userService.userRegister(userAccount, userPassword, checkPassword);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 登录请求对象
     * @param request          请求对象
     * @return 登录成功的用户对象
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 如果用户登录请求为空，则抛出异常
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录请求为空");
        }
        // 获取用户登录请求中的参数
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 如果用户登录请求中的参数为空或空白，则抛出异常
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录请求中的参数为空或空白");
        }
        User user = this.userService.userLogin(userAccount, userPassword, request);
        // 返回一个成功的响应，响应体中携带user信息
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request 请求对象
     * @return 注销是否成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        // 如果请求为空，则抛出异常
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空");
        }
        boolean result = this.userService.userLogout(request);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 更新用户请求对象
     * @param request           请求对象
     * @return 是否成功更新用户信息
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {
        // 如果用户更新请求为空，则抛出异常
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户更新请求为空");
        }
        // 如果用户更新请求中的ID小于等于零，则抛出异常
        if (userUpdateRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户更新请求中的ID小于等于零");
        }
        // 创建用户
        User user = new User();
        // 复制用户更新请求中的相关属性至用户本身
        BeanUtils.copyProperties(userUpdateRequest, user);
        // 将用户更新至数据库中
        boolean result = this.userService.updateById(user);
        // todo 这里是否需要删除Redis缓存
        // 删除缓存
        String key = CACHE_USERINFO_KEY + user.getId();
        this.stringRedisTemplate.delete(key);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 根据用户ID获取用户
     *
     * @param id      用户ID
     * @param request 请求对象
     * @return 用户对象
     */
    @GetMapping("/get")
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        // 如果ID小于等于零，则抛出异常
        if (id <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID小于等于零");
        }
        // 获取用户
        User user = this.userService.getById(id);
        // 返回一个成功的响应，响应体中携带user信息
        return ResultUtils.success(user);
    }

    /**
     * 列出所有用户
     *
     * @param userQueryRequest 用户查询请求对象
     * @param request          请求对象
     * @return 用户列表
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        // 创建用户
        User userQuery = new User();
        // 如果用户查询请求不为空，则复制用户查询请求中的相关属性至用户本身，表示查询指定信息的用户
        // 如果用户查询请求为空，则表示查询所有用户
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = this.userService.list(queryWrapper);
        // 用户脱敏，将用户列表转换为用户视图对象列表
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        // 返回一个成功的响应，响应体中携带userVOList信息
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页列出用户
     *
     * @param userQueryRequest 用户查询请求对象
     * @param request          请求对象
     * @return 分页后的用户列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        // 如果用户查询请求为空，则抛出异常
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户查询请求为空");
        }
        // 创建用户
        User userQuery = new User();
        // 复制用户查询请求中的相关属性至用户本身
        BeanUtils.copyProperties(userQueryRequest, userQuery);
        // 指定页号和页面大小
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 如果分页尺寸大于10，则抛出异常
        if (size > 10L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分页尺寸过大");
        }
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = this.userService.page(new Page<>(current, size), queryWrapper);
        // 用户脱敏，将用户分页转换为用户视图分页
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        // 返回一个成功的响应，响应体中携带userVOPage信息
        return ResultUtils.success(userVOPage);
    }

    /**
     * 根据用户ID获取用户视图
     *
     * @param id      用户ID
     * @param request 请求对象
     * @return 用户视图对象
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        // 构建缓存的键
        String key = CACHE_USERINFO_KEY + id;
        // 从缓存中获取用户
        String userInfo = this.stringRedisTemplate.opsForValue().get(key);
        // 如果缓存中存在对应的用户，则反序列化
        if (userInfo != null && userInfo.length() != 0) {
            // 将User反序列化为UserVO，并返回
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new FormatUtils()).create();
            UserVO userVO1 = (UserVO) gson.fromJson(userInfo, UserVO.class);
            return ResultUtils.success(userVO1);
        }
        // 获取响应对象
        BaseResponse<User> response = getUserById(id, request);
        // 从响应对象中获取用户
        User user = (User) response.getData();
        // 根据用户获取用户视图
        UserVO userVO = this.userService.getUserVO(user);
        // 将用户视图反序列化为JSON，并放入缓存
        try {
            String userInfoJSON = new ObjectMapper().writeValueAsString(userVO);
            this.stringRedisTemplate.opsForValue().set(key, userInfoJSON);
            this.stringRedisTemplate.expire(key, RedisConstants.USER_INFO_TIME_OUT, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 返回一个成功的响应，响应体中携带userVO信息
        return ResultUtils.success(userVO);
    }


    /**
     * 更新密钥
     *
     * @param idRequest ID请求对象
     * @param request   请求对象
     * @return 是否成功更新密钥
     */
    @PostMapping("/update/secret_key")
    public BaseResponse<Boolean> updateSecretKey(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        // 如果ID请求为空，则抛出异常
        if (idRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID请求为空");
        }
        // 如果ID请求中的ID小于等于零，则抛出异常
        if (idRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID请求中的ID小于等于零");
        }
        // 查询ID对应的用户
        long id = idRequest.getId();
        boolean result = this.userService.updateSecretKey(id);
        // todo 这里是否需要删除Redis缓存
        // 删除缓存
        String key = CACHE_USERINFO_KEY + id;
        this.stringRedisTemplate.delete(key);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }
}
