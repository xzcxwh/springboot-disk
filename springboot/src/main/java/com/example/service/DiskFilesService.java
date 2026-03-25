package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.DiskFiles;
import com.example.entity.Share;
import com.example.entity.Trash;
import com.example.exception.CustomException;
import com.example.mapper.DiskFilesMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 网盘文件信息表业务处理
 **/
@Service
public class DiskFilesService {

    // 定义一个私有的静态常量filePath，表示文件路径，该路径为系统用户目录（user.dir）下的disk文件夹
    private static final String filePath = System.getProperty("user.dir") + "/disk/";

    // 定义一个私有的静态常量log，用于记录日志，通过LoggerFactory获取DiskFilesService类的Logger实例
    private static final Logger log = LoggerFactory.getLogger(DiskFilesService.class);

    // 使用@Value注解，从配置文件中获取server.port属性的值，默认为9090，并赋值给port变量
    @Value("${server.port:9090}")
    private String port;

    // 使用@Value注解，从配置文件中获取ip属性的值，默认为localhost，并赋值给ip变量
    @Value("${ip:localhost}")
    private String ip;

    // 使用@Resource注解，通过自动装配的方式，将DiskFilesMapper接口的实例注入到diskFilesMapper变量中
    @Resource
    private DiskFilesMapper diskFilesMapper;

    // 使用@Resource注解，通过自动装配的方式，将TrashService接口的实例注入到trashService变量中
    @Resource
    TrashService trashService;

    // 使用@Resource注解，通过自动装配的方式，将ShareService接口的实例注入到shareService变量中
    @Resource
    ShareService shareService;

    /**
     * 新增
     */
    public void add(MultipartFile file, String name, String folder, Integer folderId) {
        // 创建一个DiskFiles对象，用于存储文件或文件夹的信息
        DiskFiles diskFiles = new DiskFiles();
        // 获取当前时间，用于设置文件的创建时间和更新时间
        String now = DateUtil.now();
        // 设置文件的创建时间
        diskFiles.setCrateTime(now);
        // 设置文件的更新时间
        diskFiles.setUpdateTime(now);
        // 设置文件夹标识，用于标识此条记录是文件夹还是文件
        diskFiles.setFolder(folder);  // 是否是目录
        // 设置文件或文件夹的名称
        diskFiles.setName(name);
        // 初始设置文件类型为文件夹，后续如果是文件会覆盖这个值
        diskFiles.setType(Constants.FILE_TYPE_FOLDER);  // 默认是文件夹  后面如果是文件的话 会覆盖这个值
        // 获取当前登录用户的信息
        Account currentUser = TokenUtils.getCurrentUser();
        // 如果当前用户角色是用户，则设置文件/文件夹的所属用户ID
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            diskFiles.setUserId(currentUser.getId());
        }
        // 设置文件/文件夹的父目录ID
        diskFiles.setFolderId(folderId); // 当前文件/文件夹 外传的目录ID

        // 如果folder的值表示不是文件夹，则进行文件操作
        if (Constants.NOT_FOLDER.equals(folder)) {   // 文件操作
            // 获取上传文件的原始文件名
            String originalFilename = file.getOriginalFilename();
            // 设置文件的名字为上传文件的原始文件名
            diskFiles.setName(originalFilename);
            // 获取文件的后缀名
            String extName = FileUtil.extName(originalFilename);   // 获取文件的后缀
            // 设置文件的类型为其后缀名
            diskFiles.setType(extName);
            // 生成一个时间戳，用于生成文件在磁盘上的存储名
            long flag = System.currentTimeMillis();
            // 拼接生成文件在磁盘上的存储名
            String fileName = flag + "-" + originalFilename;  // 文件存储在磁盘的文件名
            // 如果文件存储路径不存在，则创建该路径
            if (!FileUtil.exist(filePath)) {
                FileUtil.mkdir(filePath);
            }
            try {
                // 获取上传文件的字节数组
                byte[] bytes = file.getBytes();  // byte
                // 计算文件大小，单位为KB，并保留三位小数
                double size = BigDecimal.valueOf(bytes.length).divide(BigDecimal.valueOf(1024), 3, RoundingMode.HALF_UP).doubleValue();
                // 设置文件大小
                diskFiles.setSize(size);
                // 上传文件到指定路径
                // 文件上传
                file.transferTo(new File(filePath + fileName));
            } catch (Exception e) {
                // 捕获文件上传过程中的异常，并记录日志
                log.error("网盘文件上传错误", e);
            }
            // 拼接文件的访问URL
            String url = "http://" + ip + ":" + port + "/diskFiles/download/" + fileName;
            // 设置文件的访问URL
            diskFiles.setFile(url);
        }
        // 将文件或文件夹信息插入到数据库中
        diskFilesMapper.insert(diskFiles);

        // 如果文件夹ID不为空，表示有外层目录
        if (folderId != null) {  // 外层有目录
            // 根据外层目录ID查询外层目录的信息
            DiskFiles parentFolder = this.selectById(folderId);  // 获取到外层的目录
            // 获取外层目录的根目录ID
            Integer rootFolderId = parentFolder.getRootFolderId();
            // 设置文件/文件夹的根目录ID
            diskFiles.setRootFolderId(rootFolderId);
        } else {
            // 如果folder的值表示是新建文件夹
            if (Constants.IS_FOLDER.equals(folder)) {  // 当前是新建目录操作
                // 获取刚才插入到数据库的文件的ID
                Integer diskFilesId = diskFiles.getId();  // 刚才插入到数据库的文件的ID
                // 设置文件/文件夹的根目录ID为其自身的ID
                diskFiles.setRootFolderId(diskFilesId);
            }
        }
        // 更新数据库中的文件/文件夹信息，包括根目录ID等字段
        this.updateById(diskFiles);  // 更新 root_folder_id 字段的值
    }

    /**
     * 网盘文件下载
     */
// 定义一个名为download的方法，接收一个字符串类型的flag和一个HttpServletResponse类型的response作为参数。
    public void download(String flag, HttpServletResponse response) {
        // 声明一个OutputStream类型的变量os，用于表示输出流。
        OutputStream os;
        try {
            // 使用StrUtil.isNotEmpty方法检查flag是否不为空。
            if (StrUtil.isNotEmpty(flag)) {
                // 在响应头中添加一个Content-Disposition字段，设置文件名为flag，并对flag进行UTF-8编码，防止文件名中的特殊字符导致问题。
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(flag, "UTF-8"));
                // 设置响应的内容类型为application/octet-stream，这是一种通用的二进制流类型，适用于文件下载。
                response.setContentType("application/octet-stream");
                // 使用FileUtil.readBytes方法读取文件路径（filePath + flag）指定的文件内容，并将其转换为字节数组。
                byte[] bytes = FileUtil.readBytes(filePath + flag);
                // 获取HttpServletResponse的输出流，用于将文件内容发送给客户端。
                os = response.getOutputStream();
                // 将字节数组写入输出流，即向客户端发送文件内容。
                os.write(bytes);
                // 刷新输出流，确保所有待发送的字节都已经被发送。
                os.flush();
                // 关闭输出流，释放相关资源。
                os.close();
            }
        } catch (Exception e) {
            // 如果在文件下载过程中发生异常，捕获该异常并打印出“文件下载失败”的提示信息。
            System.out.println("文件下载失败");
        }
    }

    /**
     * 网盘文件预览
     */
    public void preview(Integer id, HttpServletResponse response) {
        DiskFiles diskFiles = this.selectById(id);
        if (diskFiles == null) {
            throw new CustomException(ResultCodeEnum.FILE_NOT_FOUND);
        }
        ArrayList<String> typeList = CollUtil.newArrayList("jpg", "jpeg", "png", "pdf", "gif");
        if (!typeList.contains(diskFiles.getType())) {
            throw new CustomException(ResultCodeEnum.TYPE_NOT_SUPPORT);
        }
        OutputStream os;
        try {
            String file = diskFiles.getFile();
            String flag = file.substring(file.lastIndexOf("/"));
            response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(diskFiles.getName(), "UTF-8"));
            byte[] bytes = FileUtil.readBytes(filePath + flag);
            os = response.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }

    /**
     * 移入垃圾箱
     */
    @Transactional
    public void trashById(Integer id) {
        DiskFiles diskFiles = this.selectById(id);
        this.deepTrash(id);  // 递归删除子节点

        // 移入文件记录到垃圾箱表
        Trash trash = new Trash();
        trash.setTime(DateUtil.now());
        trash.setFileId(id);
        trash.setName(diskFiles.getName());
        trash.setUserId(diskFiles.getUserId());
        trash.setSize(diskFiles.getSize());
        trashService.add(trash);
    }

    private void deepTrash(Integer id) {
        DiskFiles diskFiles = this.selectById(id);
        if (diskFiles == null) {
            return;
        }
        diskFilesMapper.trashById(id);  // 删除当前的文件
        if (Constants.NOT_FOLDER.equals(diskFiles.getFolder())) {  // 是文件
            return;
        }
        List<DiskFiles> children = diskFilesMapper.selectByFolderId(id);
        if (CollUtil.isEmpty(children)) {
            return;
        }
        for (DiskFiles child : children) {
            this.deepTrash(child.getId());  // 递归寻找子节点
        }
    }

    /**
     * 批量移入垃圾箱
     */
    public void trashBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.trashById(id);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        this.deepDelete(id);  // id 是文件的ID
        trashService.deleteByFileId(id);
    }

    private void deepDelete(Integer id) {
        DiskFiles diskFiles = this.selectById(id);
        if (diskFiles == null) {
            return;
        }
        diskFilesMapper.deleteById(id);  // 删除当前的文件记录
        if (Constants.NOT_FOLDER.equals(diskFiles.getFolder())) {  // 是文件
            // 删除文件
            String file = diskFiles.getFile();
            String path = filePath + file.substring(file.lastIndexOf("/"));
            FileUtil.del(path);
            return;
        }
        List<DiskFiles> children = diskFilesMapper.selectByFolderId(id);
        if (CollUtil.isEmpty(children)) {
            return;
        }
        for (DiskFiles child : children) {
            this.deepTrash(child.getId());  // 递归寻找子节点
        }
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(DiskFiles diskFiles) {
        diskFilesMapper.updateById(diskFiles);
    }

    /**
     * 根据ID查询
     */
    public DiskFiles selectById(Integer id) {
        return diskFilesMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<DiskFiles> selectAll(DiskFiles diskFiles) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            diskFiles.setUserId(currentUser.getId());
        }
        return diskFilesMapper.selectAll(diskFiles);
    }

    /**
     * 分页查询
     */
    public PageInfo<DiskFiles> selectPage(DiskFiles diskFiles, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<DiskFiles> list = diskFilesMapper.selectAllData(diskFiles);
        return PageInfo.of(list);
    }


    public List<DiskFiles> selectFolderNames(Integer folderId, List<DiskFiles> list) {
        DiskFiles diskFiles = this.selectById(folderId);
        if (diskFiles == null) {
            return list;
        }
        list.add(diskFiles);  // 把当前节点的名称加到list里面去  最后一起返回
        Integer parentFolderId = diskFiles.getFolderId(); // 父级ID
        if (parentFolderId == null) {  // 当前目录的外层没有目录  就结束
            return list;
        }
        return this.selectFolderNames(parentFolderId, list);
    }

    public List<Trash> selectTrash() {
        Integer userId = null;
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            userId = currentUser.getId();
        }
        return diskFilesMapper.selectTrash(userId);
    }

    /**
     * 还原
     */
    public void restore(Integer id) {
        this.deepRestore(id);
        trashService.deleteByFileId(id);  // 删除回收站的记录
    }

    private void deepRestore(Integer id) {
        DiskFiles diskFiles = this.selectById(id);
        if (diskFiles == null) {
            return;
        }
        diskFilesMapper.restoreById(id);  // 删除当前的文件记录
        if (Constants.NOT_FOLDER.equals(diskFiles.getFolder())) {  // 是文件
            return;
        }
        List<DiskFiles> children = diskFilesMapper.selectAllByFolderId(id);
        if (CollUtil.isEmpty(children)) {
            return;
        }
        for (DiskFiles child : children) {
            this.deepRestore(child.getId());  // 递归寻找子节点
        }
    }

    /**
     * 复制
     * folderId 表示外层的目录的Id
     */
    public void copy(Integer id, Integer folderId) {
        DiskFiles diskFiles = this.selectById(id);
        if (diskFiles == null) {
            return;
        }
        // 新建一个对象
        diskFiles.setId(null);
        String now = DateUtil.now();
        diskFiles.setCrateTime(now);
        diskFiles.setUpdateTime(now);
        if (Constants.NOT_FOLDER.equals(diskFiles.getFolder())) {
            String mainName = FileUtil.mainName(diskFiles.getName());
            String extName = FileUtil.extName(diskFiles.getName());
            diskFiles.setName(mainName + "-拷贝." + extName);  // 表示它是复制的文件
        } else {
            diskFiles.setName(diskFiles.getName() + "-拷贝");  // 表示它是复制的文件
        }
        diskFiles.setFolderId(folderId);  // 把拷贝后的文件夹下的所有子节点的folderId设置成当前的文件夹的ID
        diskFilesMapper.insert(diskFiles);
        if (diskFiles.getFolder().equals(Constants.IS_FOLDER)) {  // 是目录的  进行递归复制子文件
            List<DiskFiles> children = diskFilesMapper.selectByFolderId(id); // 排除被删除的文件或者文件夹
            if (CollUtil.isEmpty(children)) {
                return;
            }
            for (DiskFiles child : children) {
                this.copy(child.getId(), diskFiles.getId());  // 递归寻找子节点  复制
            }
        }
    }

    // 定义一个公共方法share，接受DiskFiles类型的参数diskFiles，并返回一个Share类型的对象
    public Share share(DiskFiles diskFiles) {
        // 创建一个新的Share对象，用于存放将要分享的文件的信息
        Share share = new Share();

        // 设置Share对象的name属性为diskFiles对象的name属性
        share.setName(diskFiles.getName());

        // 设置Share对象的shareTime属性为当前时间
        share.setShareTime(DateUtil.now());

        // 设置Share对象的fileId属性为diskFiles对象的id属性
        share.setFileId(diskFiles.getId());

        // 获取当前登录的用户信息
        Account currentUser = TokenUtils.getCurrentUser();

        // 判断当前用户角色是否为普通用户
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            // 如果是普通用户，则设置Share对象的userId属性为当前用户的id
            share.setUserId(currentUser.getId());
        }

        // 获取diskFiles对象的days属性，代表分享有效天数
        Integer days = diskFiles.getDays();

        // 根据当前时间和有效天数计算分享结束时间
        DateTime dateTime = DateUtil.offsetDay(new Date(), days);

        // 将结束时间格式化为字符串
        String endTime = DateUtil.formatDateTime(dateTime);

        // 设置Share对象的endTime属性为格式化后的结束时间字符串
        share.setEndTime(endTime);  // 结束时间

        // 生成一个唯一的标识，作为本次分享的验证码，并设置给Share对象的code属性
        share.setCode(IdUtil.getSnowflakeNextIdStr()); // 生成一个唯一的标识  作为本次分享的验证码

        // 设置Share对象的type属性为diskFiles对象的type属性
        share.setType(diskFiles.getType());

        // 调用shareService的add方法，将Share对象添加到数据库或其他存储中
        shareService.add(share);

        // 返回Share对象
        return share;
    }
    public List<DiskFiles> selectShare(Integer shareId, Integer folderId) {
        // 如果文件夹ID为空
        if (folderId == null) {
            // 调用shareService的selectById方法，根据分享ID查询分享信息
            Share share = shareService.selectById(shareId);
            // 获取分享信息中的文件ID
            Integer fileId = share.getFileId();
            // 调用当前类的selectById方法，根据文件ID查询对应的DiskFiles对象，并将其包装成List返回
            return CollUtil.newArrayList(this.selectById(fileId));
        } else {
            // 如果文件夹ID不为空
            // 创建一个新的DiskFiles对象
            DiskFiles diskFiles = new DiskFiles();
            // 设置DiskFiles对象的文件夹ID
            diskFiles.setFolderId(folderId);
            // 调用当前类的selectAll方法，根据DiskFiles对象中的文件夹ID查询对应的DiskFiles列表并返回
            return this.selectAll(diskFiles);
        }
    }
    public List<Dict> count(Integer days) {
        List<Dict> list = new ArrayList<>();
        Date now = new Date();
        DateTime end = DateUtil.offsetDay(now, -1);// 前一天  -1  ~ -7
        DateTime start = DateUtil.offsetDay(now, -days);// 前 n天
        List<DateTime> dateTimeList = DateUtil.rangeToList(start, end, DateField.DAY_OF_YEAR);
        List<String> dateList = dateTimeList.stream().map(DateUtil::formatDate).sorted(String::compareTo).collect(Collectors.toList());
        for (String date : dateList) {
            Integer count = diskFilesMapper.selectByDate(date);
            Dict dict = Dict.create().set("date", date).set("count", count);
            list.add(dict);
        }
        return list;
    }

}