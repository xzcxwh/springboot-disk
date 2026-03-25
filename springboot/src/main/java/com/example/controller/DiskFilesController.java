package com.example.controller; // 声明该类所在的包为 com.example.controller，用于组织控制器类

// 导入 Hutool 工具包中的 Dict 类，用于方便地操作键值对结构，常用于返回统计图表数据
import cn.hutool.core.lang.Dict;
// 导入统一响应结果类 Result，该类封装了 API 接口的统一返回格式（如 code、msg、data）
import com.example.common.Result;
// 导入网盘文件实体类 DiskFiles，对应数据库中的文件/文件夹表
import com.example.entity.DiskFiles;
// 导入分享实体类 Share，对应分享记录表
import com.example.entity.Share;
// 导入回收站实体类 Trash，对应回收站记录表
import com.example.entity.Trash;
// 导入网盘文件业务逻辑处理接口 DiskFilesService，用于调用具体业务方法
import com.example.service.DiskFilesService;
// 导入分页插件 PageHelper 的分页结果封装类 PageInfo，用于返回分页数据及分页信息
import com.github.pagehelper.PageInfo;
// 导入 Spring MVC 中的 Web 绑定注解，包括 @RestController、@RequestMapping 等
import org.springframework.web.bind.annotation.*;
// 导入 Spring 的文件上传处理类 MultipartFile，用于接收前端上传的文件
import org.springframework.web.multipart.MultipartFile;

// 导入 JSR-250 的依赖注入注解 Resource，用于自动装配 Service 对象
import javax.annotation.Resource;
// 导入 HttpServletResponse，用于向客户端输出文件流（如下载、预览）
import javax.servlet.http.HttpServletResponse;
// 导入 ArrayList 集合类，用于创建可变的列表对象
import java.util.ArrayList;
// 导入 Collections 工具类，用于反转列表顺序等操作
import java.util.Collections;
// 导入 List 接口，用于定义列表类型的变量
import java.util.List;

/**
 * 网盘文件前端操作接口
 * 该类提供网盘文件相关的 RESTful API，包括文件的上传、复制、分享、下载、预览、移入回收站、还原、删除、查询、分页、目录路径查询、回收站查询、分享数据查询及统计等功能。
 * 通过 @RestController 声明为控制器，所有方法返回 JSON 数据或直接输出文件流。
 * 请求路径统一以 "/diskFiles" 开头。
 **/
@RestController // 标记该类为 REST 控制器，所有处理器方法的返回值会自动转换为 JSON 格式响应给客户端
@RequestMapping("/diskFiles") // 为控制器内所有请求映射设置基础路径前缀，即所有接口 URL 都以 "/diskFiles" 开头
public class DiskFilesController { // 定义控制器类 DiskFilesController

    @Resource // 依赖注入，根据类型从 Spring 容器中获取 DiskFilesService 的实例
    private DiskFilesService diskFilesService; // 声明业务逻辑层的成员变量，用于调用文件相关的操作方法

    /**
     * 新增（上传文件）
     * 接收前端上传的文件及附加信息，调用服务层保存文件
     * @param file   上传的文件对象（由 Spring 自动封装）
     * @param name   文件显示名称（可能用于重命名或展示）
     * @param folder 文件夹路径字符串（如 "/documents/工作"）
     * @param folderId 所属文件夹的数据库 ID
     * @return 操作成功结果
     */
    @PostMapping("/add") // 映射 HTTP POST 请求到 "/diskFiles/add"，处理文件上传
    public Result add(MultipartFile file, String name, String folder, Integer folderId) {
        // 调用服务层的添加方法，将文件物理存储到磁盘，并将文件元数据保存到数据库
        diskFilesService.add(file, name, folder, folderId);
        // 返回统一成功响应（无数据）
        return Result.success();
    }

    /**
     * 复制文件
     * 根据指定的文件 ID 复制一份新的文件（或目录）
     * @param id 要复制的文件 ID
     * @return 操作成功结果
     */
    @PostMapping("/copy/{id}") // 映射 HTTP POST 请求到 "/diskFiles/copy/{id}"，{id} 为路径变量
    public Result copy(@PathVariable Integer id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数 id
        // 调用服务层的复制方法，第二个参数为 null 表示复制到原位置（当前文件夹）
        diskFilesService.copy(id, null);
        return Result.success(); // 返回操作成功响应
    }

    /**
     * 分享文件
     * 接收前端传递的 DiskFiles 对象（包含要分享的文件信息），生成分享记录
     * @param diskFiles 包含分享信息的文件对象（通常包含文件 ID、分享类型、有效期等）
     * @return 返回包含分享信息的成功结果
     */
    @PostMapping("/share") // 映射 HTTP POST 请求到 "/diskFiles/share"
    public Result share(@RequestBody DiskFiles diskFiles) { // @RequestBody 将请求体中的 JSON 数据绑定到 diskFiles 对象
        // 调用服务层的分享方法，生成分享链接/码并保存分享记录，返回 Share 对象
        Share share = diskFilesService.share(diskFiles);
        // 返回成功响应，并将生成的分享信息（如分享码、有效期）放入 data 字段返回给前端
        return Result.success(share);
    }

    /**
     * 下载文件
     * 根据分享标识（flag）下载对应的文件（常用于分享链接的下载）
     * @param flag     分享标识（通常是由分享时生成的唯一字符串）
     * @param response HttpServletResponse 对象，用于向客户端输出文件流
     */
    @GetMapping("/download/{flag}") // 映射 HTTP GET 请求到 "/diskFiles/download/{flag}"
    public void download(@PathVariable String flag, HttpServletResponse response) {
        // 调用服务层的下载方法，根据 flag 找到对应的文件，并将文件内容写入 response 的输出流
        diskFilesService.download(flag, response);
    }

    /**
     * 预览文件
     * 根据文件 ID 在浏览器中预览文件（如图片、PDF 等）
     * @param id       要预览的文件 ID
     * @param response HttpServletResponse 对象，用于向客户端输出文件流
     */
    @GetMapping("/preview/{id}") // 映射 HTTP GET 请求到 "/diskFiles/preview/{id}"
    public void preview(@PathVariable Integer id, HttpServletResponse response) {
        // 调用服务层的预览方法，根据 ID 获取文件，设置合适的 Content-Type，并将文件内容写入 response
        diskFilesService.preview(id, response);
    }

    /**
     * 移入回收站（递归删除）
     * 将指定 ID 的文件（或目录及其所有子内容）移入回收站
     * @param id 要移入回收站的文件 ID
     * @return 操作成功结果
     */
    @DeleteMapping("/trash/{id}") // 映射 HTTP DELETE 请求到 "/diskFiles/trash/{id}"
    public Result trash(@PathVariable Integer id) {
        // 调用服务层的方法，将文件状态标记为“已删除”（逻辑删除），并记录到回收站表
        diskFilesService.trashById(id);
        return Result.success(); // 返回操作成功响应
    }

    /**
     * 批量移入回收站
     * 将多个文件批量移入回收站
     * @param ids 要移入回收站的文件 ID 列表（JSON 数组）
     * @return 操作成功结果
     */
    @DeleteMapping("/trash/batch") // 映射 HTTP DELETE 请求到 "/diskFiles/trash/batch"
    public Result trashBatch(@RequestBody List<Integer> ids) { // @RequestBody 将请求体中的 JSON 数组绑定到 ids 参数
        // 调用服务层的批量移入回收站方法
        diskFilesService.trashBatch(ids);
        return Result.success(); // 返回操作成功响应
    }

    /**
     * 彻底删除文件
     * 从数据库中彻底删除指定 ID 的文件（注意：通常先从回收站中删除）
     * @param id 要删除的文件 ID
     * @return 操作成功结果
     */
    @DeleteMapping("/delete/{id}") // 映射 HTTP DELETE 请求到 "/diskFiles/delete/{id}"
    public Result deleteById(@PathVariable Integer id) {
        // 调用服务层的彻底删除方法（物理删除，从数据库和存储中移除）
        diskFilesService.deleteById(id);
        return Result.success(); // 返回操作成功响应
    }

    /**
     * 批量彻底删除文件
     * 从数据库中彻底删除多个文件
     * @param ids 要删除的文件 ID 列表（JSON 数组）
     * @return 操作成功结果
     */
    @DeleteMapping("/delete/batch") // 映射 HTTP DELETE 请求到 "/diskFiles/delete/batch"
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        // 调用服务层的批量彻底删除方法
        diskFilesService.deleteBatch(ids);
        return Result.success(); // 返回操作成功响应
    }

    /**
     * 修改文件信息
     * 接收前端传递的 DiskFiles 对象（包含 ID 及更新字段），调用服务层更新
     * @param diskFiles 包含更新信息的文件对象（如文件名、所在文件夹等）
     * @return 操作成功结果
     */
    @PutMapping("/update") // 映射 HTTP PUT 请求到 "/diskFiles/update"
    public Result updateById(@RequestBody DiskFiles diskFiles) {
        // 调用服务层的更新方法，根据 ID 更新文件的非空字段
        diskFilesService.updateById(diskFiles);
        return Result.success(); // 返回操作成功响应
    }

    /**
     * 还原文件（从回收站恢复）
     * 将指定 ID 的文件从回收站还原
     * @param id 要还原的文件 ID
     * @return 操作成功结果
     */
    @PutMapping("/restore/{id}") // 映射 HTTP PUT 请求到 "/diskFiles/restore/{id}"
    public Result restore(@PathVariable Integer id) {
        // 调用服务层的还原方法，将文件状态从“已删除”恢复为“正常”，并从回收站表中移除记录
        diskFilesService.restore(id);
        return Result.success(); // 返回操作成功响应
    }

    /**
     * 根据 ID 查询文件详情
     * @param id 文件 ID
     * @return 包含文件信息的成功结果
     */
    @GetMapping("/selectById/{id}") // 映射 HTTP GET 请求到 "/diskFiles/selectById/{id}"
    public Result selectById(@PathVariable Integer id) {
        // 调用服务层根据 ID 查询文件的方法
        DiskFiles diskFiles = diskFilesService.selectById(id);
        // 返回成功响应，将查询到的文件对象作为 data 返回
        return Result.success(diskFiles);
    }

    /**
     * 查询所有文件（支持条件筛选）
     * 接收可选的 DiskFiles 对象作为查询条件，返回所有符合条件的数据（不分页）
     * @param diskFiles 查询条件对象（属性不为空时作为过滤条件）
     * @return 包含文件列表的成功结果
     */
    @GetMapping("/selectAll") // 映射 HTTP GET 请求到 "/diskFiles/selectAll"
    public Result selectAll(DiskFiles diskFiles) {
        // 调用服务层查询所有符合条件文件的方法
        List<DiskFiles> list = diskFilesService.selectAll(diskFiles);
        // 返回成功响应，将文件列表作为 data 返回
        return Result.success(list);
    }

    /**
     * 分页查询文件
     * 支持条件筛选，通过 pageNum 和 pageSize 控制分页
     * @param diskFiles 查询条件对象
     * @param pageNum   当前页码，默认1
     * @param pageSize  每页记录数，默认10
     * @return 包含分页信息的成功结果（PageInfo 对象中包含当前页数据及总记录数等）
     */
    @GetMapping("/selectPage") // 映射 HTTP GET 请求到 "/diskFiles/selectPage"
    public Result selectPage(DiskFiles diskFiles,
                             @RequestParam(defaultValue = "1") Integer pageNum,  // @RequestParam 从请求参数中获取 pageNum，默认为1
                             @RequestParam(defaultValue = "10") Integer pageSize) { // 获取 pageSize，默认为10
        // 调用服务层分页查询方法，返回 PageInfo 对象（包含分页数据及分页信息）
        PageInfo<DiskFiles> page = diskFilesService.selectPage(diskFiles, pageNum, pageSize);
        // 返回成功响应，将分页对象作为 data 返回
        return Result.success(page);
    }

    /**
     * 查询所有父级的目录名称（向上递归获取路径）
     * 根据文件夹 ID 获取其所有祖先目录的名称列表，用于面包屑导航
     * @param folderId 当前文件夹 ID
     * @return 包含目录名称列表的成功结果（顺序从根目录到当前目录）
     */
    @GetMapping("/selectFolders") // 映射 HTTP GET 请求到 "/diskFiles/selectFolders"
    public Result selectFolders(Integer folderId) {
        List<DiskFiles> list = new ArrayList<>(); // 创建一个空的 ArrayList，用于存储路径上的目录对象
        if (folderId == null) { // 如果未传入文件夹 ID，直接返回空列表（表示根目录）
            return Result.success(list);
        }
        // 调用服务层递归查询父级目录，将查询到的目录对象依次添加到 list 中（顺序从当前目录向上）
        diskFilesService.selectFolderNames(folderId, list);
        Collections.reverse(list); // 反转列表，使顺序变为从根目录到当前目录，便于前端展示面包屑
        return Result.success(list); // 返回成功响应，携带目录列表
    }

    /**
     * 查询回收站中的文件
     * @return 包含回收站文件列表的成功结果（通常包含文件原名、删除时间等）
     */
    @GetMapping("/selectTrash") // 映射 HTTP GET 请求到 "/diskFiles/selectTrash"
    public Result selectTrash() {
        // 调用服务层查询回收站中的文件，返回 Trash 对象列表（每个 Trash 包含原文件信息和删除时间）
        List<Trash> list = diskFilesService.selectTrash();
        return Result.success(list); // 返回成功响应，携带回收站列表
    }

    /**
     * 查询分享的数据
     * 根据分享 ID 和文件夹 ID 获取分享内容（用于分享页面展示）
     * @param shareId  分享记录的 ID
     * @param folderId 当前浏览的文件夹 ID（用于分目录展示）
     * @return 包含分享文件列表的成功结果
     */
    @GetMapping("/selectShare") // 映射 HTTP GET 请求到 "/diskFiles/selectShare"
    public Result selectShare(Integer shareId, Integer folderId) {
        // 调用服务层方法，根据分享 ID 和文件夹 ID 获取对应的文件列表（支持目录结构）
        List<DiskFiles> list = diskFilesService.selectShare(shareId, folderId);
        return Result.success(list); // 返回成功响应，携带文件列表
    }

    /**
     * 统计文件操作数据
     * 根据指定天数统计文件的上传、下载、分享等数量（用于后台仪表盘图表）
     * @param days 统计的天数（例如最近7天）
     * @return 包含统计结果的字典列表，每个 Dict 包含日期和对应的计数
     */
    @GetMapping("/count") // 映射 HTTP GET 请求到 "/diskFiles/count"
    public Result count(@RequestParam Integer days) {
        // 调用服务层的统计方法，获取指定天数内的统计数据
        List<Dict> list = diskFilesService.count(days);
        return Result.success(list); // 返回成功响应，携带统计列表
    }

}