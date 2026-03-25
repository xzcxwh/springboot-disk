package com.example.service;

import cn.hutool.core.util.ObjectUtil; // 导入Hutool工具类库中的ObjectUtil工具类
import com.example.common.Constants; // 导入项目中的常量类
import com.example.common.enums.ResultCodeEnum; // 导入项目中的结果枚举类
import com.example.common.enums.RoleEnum; // 导入项目中的角色枚举类
import com.example.entity.Account; // 导入用户账号实体类
import com.example.entity.User; // 导入用户实体类
import com.example.exception.CustomException; // 导入项目中的自定义异常类
import com.example.mapper.UserMapper; // 导入用户映射接口
import com.example.utils.TokenUtils; // 导入项目中的Token工具类
import com.github.pagehelper.PageHelper; // 导入分页插件的PageHelper类
import com.github.pagehelper.PageInfo; // 导入分页插件的PageInfo类
import org.springframework.beans.BeanUtils; // 导入Spring框架中的BeanUtils工具类
import org.springframework.stereotype.Service; // 导入Spring框架中的Service注解

import javax.annotation.Resource; // 导入Java的Resource注解，用于注入依赖
import java.util.List; // 导入Java的List接口

// 使用Service注解，表明这是一个Spring服务组件
@Service
public class UserService {

    // 使用Resource注解，将UserMapper接口注入为userMapper字段
    @Resource
    private UserMapper userMapper;

    /**
     * 新增用户
     *
     * @param user 待新增的用户对象
     */
    public void add(User user) {
        // 调用userMapper的selectByUsername方法，查询数据库中是否存在该用户
        User dbUser = userMapper.selectByUsername(user.getUsername());
        // 如果数据库中已存在该用户
        if (ObjectUtil.isNotNull(dbUser)) {
            // 抛出用户已存在的异常
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        // 如果用户密码为空
        if (ObjectUtil.isEmpty(user.getPassword())) {
            // 设置默认密码
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        // 如果用户姓名为空
        if (ObjectUtil.isEmpty(user.getName())) {
            // 将用户名赋值给姓名
            user.setName(user.getUsername());
        }
        // 设置用户角色为普通用户
        user.setRole(RoleEnum.USER.name());
        // 调用userMapper的insert方法，将用户信息插入到数据库中
        userMapper.insert(user);
    }

    /**
     * 根据ID删除用户
     *
     * @param id 用户的ID
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户的ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.deleteById(id);
        }
    }

    /**
     * 根据ID修改用户信息
     *
     * @param user 要修改的用户信息
     */
    public void updateById(User user) {
        userMapper.updateById(user);
    }

    /**
     * 根据ID查询用户信息
     *
     * @param id 用户的ID
     * @return 查询到的用户信息
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询所有用户信息
     *
     * @param user 查询条件（可能为null）
     * @return 所有用户信息的列表
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 分页查询用户信息
     *
     * @param user 查询条件（可能为null）
     * @param pageNum 页码
     * @param pageSize 每页的记录数
     * @return 分页查询结果，包含分页信息和用户列表
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        // 使用PageHelper开始分页
        // 开始分页
        PageHelper.startPage(pageNum, pageSize);
        // 调用userMapper查询所有用户信息
        // 查询所有用户
        List<User> list = userMapper.selectAll(user);
        // 使用PageInfo封装查询结果，得到分页信息
        // 封装分页信息
        return PageInfo.of(list);
    }



    /**
     * 登录方法，用于验证用户账号并返回用户信息
     *
     * @param account 用户账号信息
     * @return 登录成功后的用户信息
     * @throws CustomException 自定义异常，登录失败时抛出
     */
    public Account login(Account account) {
        // 通过用户名查询数据库中的用户信息
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        // 如果查询结果为空，即用户不存在
        if (ObjectUtil.isNull(dbUser)) {
            // 抛出用户不存在的异常
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        // 验证传入的密码与数据库中用户的密码是否一致
        if (!account.getPassword().equals(dbUser.getPassword())) {
            // 抛出账号密码错误的异常
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 根据用户ID和角色信息生成token数据
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        // 调用TokenUtils工具类创建token
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        // 将生成的token设置到用户信息中
        dbUser.setToken(token);
        // 返回登录成功后的用户信息
        return dbUser;
    }

    /**
     * 注册方法，用于将账号信息保存到数据库中
     *
     * @param account 待注册的用户账号信息
     */
    public void register(Account account) {
        // 创建一个新的User对象
        User user = new User();
        // 使用BeanUtils工具类将Account对象的属性复制到User对象中
        BeanUtils.copyProperties(account, user);
        // 调用add方法将User对象保存到数据库中
        this.add(user);
    }

    /**
     * 修改密码方法，用于更新用户的密码信息
     *
     * @param account 包含新密码信息的用户账号对象
     * @throws CustomException 自定义异常，修改密码失败时抛出
     */
    public void updatePassword(Account account) {
        // 通过用户名查询数据库中的用户信息
        User dbUser = userMapper.selectByUsername(account.getUsername());
        // 如果查询结果为空，即用户不存在
        if (ObjectUtil.isNull(dbUser)) {
            // 抛出用户不存在的异常
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        // 验证传入的旧密码与数据库中用户的密码是否一致
        if (!account.getPassword().equals(dbUser.getPassword())) {
            // 抛出旧密码错误的异常
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        // 更新用户的密码为新的密码
        dbUser.setPassword(account.getNewPassword());
        // 调用userMapper的updateById方法更新数据库中的用户密码信息
        userMapper.updateById(dbUser);
    }

}
