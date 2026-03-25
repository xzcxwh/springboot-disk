package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 基础前端接口
 */
@RestController
public class WebController {

    @Resource
    private AdminService adminService;

    @Resource
    UserService userService;

    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        // 检查账户信息是否完整
        if (ObjectUtil.isEmpty(account.getUsername()) || ObjectUtil.isEmpty(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            // 如果账户信息不完整，返回参数缺失错误
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }

        // 根据角色调用相应的登录服务
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            // 如果是管理员角色，调用adminService的登录方法
            account = adminService.login(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            // 如果是普通用户角色，调用userService的登录方法
            account = userService.login(account);
        } else {
            // 如果角色不正确，返回参数错误
            return Result.error(ResultCodeEnum.PARAM_ERROR);
        }

        // 登录成功，返回成功结果和账户信息
        return Result.success(account);
    }


    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        } else {
            return Result.error(ResultCodeEnum.PARAM_ERROR);
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        // 检查用户名、旧密码和新密码是否有效
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            // 如果参数无效，则返回参数丢失错误
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }

        // 判断用户角色
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            // 如果是管理员角色，则调用管理员服务更新密码
            adminService.updatePassword(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            // 如果是普通用户角色，则调用用户服务更新密码
            userService.updatePassword(account);
        } else {
            // 如果角色无效，则返回参数错误
            return Result.error(ResultCodeEnum.PARAM_ERROR);
        }

        // 返回更新密码成功的结果
        return Result.success();
    }



}
