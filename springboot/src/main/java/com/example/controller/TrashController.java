package com.example.controller; // 声明包名，该控制器类属于com.example.controller包

import com.example.common.Result; // 导入通用的返回结果类
import com.example.entity.Trash; // 导入垃圾箱实体类
import com.example.service.TrashService; // 导入垃圾箱服务类
import com.github.pagehelper.PageInfo; // 导入分页助手类
import org.springframework.web.bind.annotation.*; // 导入Spring Web相关的注解

import javax.annotation.Resource; // 导入资源注入注解
import java.util.List; // 导入列表接口

/**
 * 垃圾箱前端操作接口
 **/
@RestController // 声明这是一个RESTful风格的控制器
@RequestMapping("/trash") // 设置该控制器下的所有请求路径前缀为/trash
public class TrashController {

    @Resource // 使用资源注入注解，将TrashService的实例注入到trashService变量中
    private TrashService trashService;

    /**
     * 新增
     */
    @PostMapping("/add") // 处理POST请求，路径为/trash/add
    public Result add(@RequestBody Trash trash) { // 接收一个JSON格式的Trash对象作为请求体
        trashService.add(trash); // 调用服务层的add方法，将垃圾箱添加到数据库
        return Result.success(); // 返回操作成功的通用结果
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}") // 处理DELETE请求，路径为/trash/delete/{id}，其中{id}为路径变量
    public Result deleteById(@PathVariable Integer id) { // 从路径变量中获取id
        trashService.deleteById(id); // 调用服务层的deleteById方法，根据id删除垃圾箱
        return Result.success(); // 返回操作成功的通用结果
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch") // 处理DELETE请求，路径为/trash/delete/batch
    public Result deleteBatch(@RequestBody List<Integer> ids) { // 接收一个包含多个id的列表作为请求体
        trashService.deleteBatch(ids); // 调用服务层的deleteBatch方法，根据id列表批量删除垃圾箱
        return Result.success(); // 返回操作成功的通用结果
    }

    /**
     * 修改
     */
    @PutMapping("/update") // 处理PUT请求，路径为/trash/update
    public Result updateById(@RequestBody Trash trash) { // 接收一个JSON格式的Trash对象作为请求体
        trashService.updateById(trash); // 调用服务层的updateById方法，根据垃圾箱信息更新数据库中的记录
        return Result.success(); // 返回操作成功的通用结果
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}") // 处理GET请求，路径为/trash/selectById/{id}，其中{id}为路径变量
    public Result selectById(@PathVariable Integer id) { // 从路径变量中获取id
        Trash trash = trashService.selectById(id); // 调用服务层的selectById方法，根据id查询垃圾箱信息
        return Result.success(trash); // 返回操作成功的通用结果，并附带查询到的垃圾箱信息
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll") // 处理GET请求，路径为/trash/selectAll
    public Result selectAll(Trash trash ) { // 接收一个Trash对象作为查询条件（可能用于筛选，但在代码中未使用）
        List<Trash> list = trashService.selectAll(trash); // 调用服务层的selectAll方法，查询所有垃圾箱信息
        return Result.success(list); // 返回操作成功的通用结果，并附带查询到的垃圾箱列表
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage") // 处理GET请求，路径为/trash/selectPage
    public Result selectPage(Trash trash, // 接收一个Trash对象作为查询条件
                             @RequestParam(defaultValue = "1") Integer pageNum, // 接收一个名为pageNum的请求参数，默认为1
                             @RequestParam(defaultValue = "10") Integer pageSize) { // 接收一个名为pageSize的请求参数，默认为10
        PageInfo<Trash> page = trashService.selectPage(trash, pageNum, pageSize); // 调用服务层的selectPage方法进行分页查询
        return Result.success(page); // 返回操作成功的通用结果，并附带分页查询到的垃圾箱信息
    }


}