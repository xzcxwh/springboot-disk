package com.example.service; // 声明服务层所在的包

import com.example.entity.Trash; // 导入垃圾箱实体类
import com.example.mapper.TrashMapper; // 导入垃圾箱的Mapper接口
import com.github.pagehelper.PageHelper; // 导入分页工具类
import com.github.pagehelper.PageInfo; // 导入分页信息类
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import javax.annotation.Resource; // 导入Spring的资源注入注解
import java.util.List; // 导入Java的列表接口

/**
 * 垃圾箱信息表业务处理
 **/
@Service // 声明这是一个Spring的Service组件
public class TrashService {

    @Resource // 使用资源注入注解，将TrashMapper接口的实例注入到trashMapper变量中
    private TrashMapper trashMapper; // 定义TrashMapper类型的成员变量trashMapper

    /**
     * 新增
     * 将传入的垃圾箱对象添加到数据库中
     */
    public void add(Trash trash) {
        trashMapper.insert(trash); // 调用trashMapper的insert方法，将trash对象插入数据库
    }

    /**
     * 删除
     * 根据传入的ID删除对应的垃圾箱
     */
    public void deleteById(Integer id) {
        trashMapper.deleteById(id); // 调用trashMapper的deleteById方法，根据id删除垃圾箱
    }

    /**
     * 批量删除
     * 根据传入的ID列表批量删除对应的垃圾箱
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) { // 遍历ID列表
            trashMapper.deleteById(id); // 调用trashMapper的deleteById方法，逐个删除垃圾箱
        }
    }

    /**
     * 修改
     * 根据传入的垃圾箱对象更新数据库中的对应记录
     */
    public void updateById(Trash trash) {
        trashMapper.updateById(trash); // 调用trashMapper的updateById方法，更新垃圾箱信息
    }

    /**
     * 根据ID查询
     * 根据传入的ID查询对应的垃圾箱信息
     */
    public Trash selectById(Integer id) {
        return trashMapper.selectById(id); // 调用trashMapper的selectById方法，返回查询到的垃圾箱对象
    }

    /**
     * 查询所有
     * 查询所有垃圾箱信息
     */
    public List<Trash> selectAll(Trash trash) {
        return trashMapper.selectAll(trash); // 调用trashMapper的selectAll方法，返回所有垃圾箱列表
    }

    /**
     * 分页查询
     * 根据条件进行分页查询垃圾箱信息
     */
    public PageInfo<Trash> selectPage(Trash trash, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); // 调用PageHelper的startPage方法，开始分页
        List<Trash> list = trashMapper.selectAll(trash); // 调用trashMapper的selectAll方法，获取分页后的垃圾箱列表
        return PageInfo.of(list); // 将列表转换为PageInfo对象，并返回
    }

    /**
     * 根据文件ID删除
     * 根据传入的文件ID删除与之关联的垃圾箱
     */
    public void deleteByFileId(Integer fileId) {
        trashMapper.deleteByFileId(fileId); // 调用trashMapper的deleteByFileId方法，根据文件ID删除垃圾箱
    }
}