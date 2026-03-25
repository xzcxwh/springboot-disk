package com.example.controller; // 定义包名为 com.example.controller，存放控制器类

// 导入统一响应结果类 Result，用于封装 API 返回数据
import com.example.common.Result;
// 导入分享实体类 Share，对应数据库中的分享记录表
import com.example.entity.Share;
// 导入分享服务层接口 ShareService，用于调用业务逻辑
import com.example.service.ShareService;
// 导入分页插件 PageHelper 的分页结果封装类 PageInfo
import com.github.pagehelper.PageInfo;
// 导入 Spring MVC 中的 Web 绑定注解（如 @RestController、@RequestMapping 等）
import org.springframework.web.bind.annotation.*;

// 导入 JSR-250 的依赖注入注解 Resource，用于自动装配 Service 对象
import javax.annotation.Resource;
// 导入 List 接口，用于处理批量操作时的 ID 列表
import java.util.List;

/**
 * 文件分享前端操作接口
 * 该类提供分享记录相关的 RESTful API，包括分享记录的增删改查及分页查询等功能。
 * 通过 @RestController 声明为控制器，所有方法返回 JSON 数据。
 * 请求路径统一以 "/share" 开头。
 **/
@RestController // 标记该类为 REST 控制器，所有处理器方法的返回值会自动转换为 JSON 格式响应给客户端
@RequestMapping("/share") // 为控制器内所有请求映射设置基础路径前缀，即所有接口 URL 都以 "/share" 开头
public class ShareController { // 定义控制器类 ShareController

    @Resource // 依赖注入，根据类型从 Spring 容器中获取 ShareService 的实例
    private ShareService shareService; // 声明业务逻辑层的成员变量，用于调用分享相关的操作方法

    /**
     * 新增分享记录
     * 接收前端传递的 Share 对象（JSON 格式），调用服务层添加方法
     * @param share 包含分享信息的对象
     * @return 统一结果对象，操作成功时返回成功状态
     */
    @PostMapping("/add") // 映射 HTTP POST 请求到 "/share/add"，处理新增分享记录
    public Result add(@RequestBody Share share) { // @RequestBody 将请求体中的 JSON 数据绑定到 Share 对象
        shareService.add(share); // 调用服务层的添加方法，将分享记录存入数据库
        return Result.success(); // 返回操作成功的结果（无数据）
    }

    /**
     * 根据指定 ID 删除分享记录
     * @param id 要删除的分享记录 ID
     * @return 返回操作结果
     */
    @DeleteMapping("/delete/{id}") // 映射 HTTP DELETE 请求到 "/share/delete/{id}"，{id} 为路径变量
    public Result deleteById(@PathVariable Integer id) { // @PathVariable 从 URL 路径中获取 id 参数
        shareService.deleteById(id); // 调用服务层根据 ID 删除分享记录的方法
        return Result.success(); // 返回操作成功的结果
    }

    /**
     * 批量删除分享记录
     * 接收前端传递的 ID 列表（JSON 数组），调用服务层批量删除方法
     * @param ids 要删除的分享记录 ID 列表
     * @return 返回操作结果
     */
    @DeleteMapping("/delete/batch") // 映射 HTTP DELETE 请求到 "/share/delete/batch"
    public Result deleteBatch(@RequestBody List<Integer> ids) { // @RequestBody 将请求体中的 JSON 数组绑定到 List<Integer>
        shareService.deleteBatch(ids); // 调用服务层的批量删除方法
        return Result.success(); // 返回操作成功的结果
    }

    /**
     * 修改分享记录
     * 接收前端传递的 Share 对象（包含 ID 及更新字段），调用服务层更新方法
     * @param share 包含更新信息的分享记录对象
     * @return 返回操作结果
     */
    @PutMapping("/update") // 映射 HTTP PUT 请求到 "/share/update"
    public Result updateById(@RequestBody Share share) { // @RequestBody 将请求体中的 JSON 数据绑定到 Share 对象
        shareService.updateById(share); // 调用服务层根据 ID 更新分享记录的方法
        return Result.success(); // 返回操作成功的结果
    }

    /**
     * 根据 ID 查询分享记录详情（并增加访问次数）
     * @param id 分享记录 ID
     * @return 包含分享记录信息的成功结果
     */
    @GetMapping("/selectById/{id}") // 映射 HTTP GET 请求到 "/share/selectById/{id}"
    public Result selectById(@PathVariable Integer id) { // @PathVariable 从 URL 路径中获取 id 参数
        Share share = shareService.selectById(id); // 调用服务层根据 ID 查询分享记录的方法，获取分享对象
        shareService.updateCount(id);  // 调用服务层方法，将该分享记录的访问次数（count）加1
        return Result.success(share); // 返回成功结果，并将查询到的分享对象作为数据返回
    }

    /**
     * 查询所有分享记录（支持条件筛选）
     * 接收可选的 Share 对象作为查询条件，调用服务层查询所有符合条件的分享记录
     * @param share 查询条件对象（属性不为空时作为过滤条件）
     * @return 包含分享记录列表的成功结果
     */
    @GetMapping("/selectAll") // 映射 HTTP GET 请求到 "/share/selectAll"
    public Result selectAll(Share share ) { // Spring MVC 自动将请求参数绑定到 Share 对象的属性（支持条件查询）
        List<Share> list = shareService.selectAll(share); // 调用服务层查询所有分享记录（带条件）的方法，返回列表
        return Result.success(list); // 返回成功结果，并将分享记录列表作为数据返回
    }

    /**
     * 分页查询分享记录
     * 支持条件筛选，通过 pageNum 和 pageSize 控制分页
     * @param share 查询条件对象，可传空
     * @param pageNum 当前页码，默认值为1
     * @param pageSize 每页记录数，默认值为10
     * @return 返回包含分页信息的成功结果
     */
    @GetMapping("/selectPage") // 映射 HTTP GET 请求到 "/share/selectPage"
    public Result selectPage(Share share, // 查询条件，自动绑定
                             @RequestParam(defaultValue = "1") Integer pageNum, // @RequestParam 从请求参数中获取 pageNum，默认为1
                             @RequestParam(defaultValue = "10") Integer pageSize) { // 获取 pageSize，默认为10
        PageInfo<Share> page = shareService.selectPage(share, pageNum, pageSize); // 调用服务层分页查询方法，返回 PageInfo 对象（包含分页数据及分页信息）
        return Result.success(page); // 返回成功结果，并将分页对象作为数据返回
    }

}