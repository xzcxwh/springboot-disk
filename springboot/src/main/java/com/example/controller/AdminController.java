package com.example.controller; // 定义包名，表示该类属于控制器层

import com.example.common.Result; // 导入统一返回结果类，用于封装API响应
import com.example.entity.Admin; // 导入管理员实体类
import com.example.service.AdminService; // 导入管理员服务类
import com.github.pagehelper.PageInfo; // 导入分页插件PageHelper的分页信息类
import org.springframework.web.bind.annotation.*; // 导入Spring MVC的Web注解
import javax.annotation.Resource; // 导入依赖注入注解
import java.util.List; // 导入List集合类

/**
 * 管理员前端操作接口
 **/
@RestController // 标识这是一个控制器类，并且所有方法返回的数据直接写入HTTP响应体（RESTful风格）
@RequestMapping("/admin") // 设置该类下所有请求的基础URL路径为 "/admin"
public class AdminController {

    @Resource // 依赖注入，根据类型自动装配AdminService实例
    private AdminService adminService;

    /**
     * 新增管理员
     */
    @PostMapping("/add") // 映射HTTP POST请求到 "/admin/add"
    public Result add(@RequestBody Admin admin) { // @RequestBody将请求体中的JSON数据绑定到Admin对象
        adminService.add(admin); // 调用服务层的添加方法，保存管理员信息
        return Result.success(); // 返回操作成功的统一响应（无数据）
    }

    /**
     * 根据指定ID删除管理员
     *
     * @param id 要删除的管理员ID
     * @return 返回操作结果
     */
    /**
     * 删除（单个）
     */
    @DeleteMapping("/delete/{id}") // 映射HTTP DELETE请求到 "/admin/delete/{id}"，{id}为路径变量
    public Result deleteById(@PathVariable Integer id) { // @PathVariable将路径中的id绑定到方法参数
        adminService.deleteById(id); // 调用服务层根据ID删除管理员
        return Result.success(); // 返回操作成功的统一响应
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch") // 映射HTTP DELETE请求到 "/admin/delete/batch"
    public Result deleteBatch(@RequestBody List<Integer> ids) { // @RequestBody接收请求体中的ID列表（JSON数组）
        adminService.deleteBatch(ids); // 调用服务层批量删除管理员
        return Result.success(); // 返回操作成功的统一响应
    }

    /**
     * 修改管理员信息
     */
    @PutMapping("/update") // 映射HTTP PUT请求到 "/admin/update"
    public Result updateById(@RequestBody Admin admin) { // @RequestBody将请求体中的JSON数据绑定到Admin对象
        adminService.updateById(admin); // 调用服务层根据ID更新管理员信息
        return Result.success(); // 返回操作成功的统一响应
    }

    /**
     * 根据ID查询管理员
     */
    @GetMapping("/selectById/{id}") // 映射HTTP GET请求到 "/admin/selectById/{id}"
    public Result selectById(@PathVariable Integer id) { // @PathVariable获取路径中的id
        Admin admin = adminService.selectById(id); // 调用服务层根据ID查询管理员
        return Result.success(admin); // 返回查询到的管理员数据（封装在统一响应中）
    }

    /**
     * 查询所有管理员（支持条件筛选）
     */
    @GetMapping("/selectAll") // 映射HTTP GET请求到 "/admin/selectAll"
    public Result selectAll(Admin admin ) { // 使用Admin对象作为查询条件，Spring MVC自动将请求参数绑定到对象属性
        List<Admin> list = adminService.selectAll(admin); // 调用服务层根据条件查询所有管理员
        return Result.success(list); // 返回查询到的管理员列表
    }

    /**
     * 分页查询管理员
     */
    @GetMapping("/selectPage") // 映射HTTP GET请求到 "/admin/selectPage"
    public Result selectPage(Admin admin, // 查询条件，自动绑定请求参数
                             @RequestParam(defaultValue = "1") Integer pageNum, // 页码，默认值为1
                             @RequestParam(defaultValue = "10") Integer pageSize) { // 每页条数，默认值为10
        PageInfo<Admin> page = adminService.selectPage(admin, pageNum, pageSize); // 调用服务层进行分页查询
        return Result.success(page); // 返回分页结果（包含当前页数据、总记录数等）
    }

}