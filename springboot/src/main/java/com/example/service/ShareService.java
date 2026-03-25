package com.example.service;

// 导入 Hutool 工具包中日期时间处理的类
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

// 导入 Share 实体类
import com.example.entity.Share;

// 导入 ShareMapper 接口，用于数据库操作
import com.example.mapper.ShareMapper;

// 导入 PageHelper 和 PageInfo，用于分页查询
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

// 使用 @Service 注解标识该类为一个服务类，用于业务逻辑处理
import org.springframework.stereotype.Service;

// 导入 Resource 注解，用于注入依赖
import javax.annotation.Resource;

// 导入 BigDecimal 和 RoundingMode，用于处理浮点数运算
import java.math.BigDecimal;
import java.math.RoundingMode;

// 导入 List 接口，用于处理集合数据
import java.util.List;

/**
 * 文件分享信息表业务处理类
 **/
@Service
public class ShareService {

    // 使用 @Resource 注解注入 ShareMapper，用于数据库操作
    @Resource
    private ShareMapper shareMapper;

    /**
     * 新增文件分享信息
     *
     * @param share 要新增的文件分享信息
     */
    public void add(Share share) {
        // 调用 shareMapper 的 insert 方法将文件分享信息插入到数据库
        shareMapper.insert(share);
    }

    /**
     * 根据ID删除文件分享信息
     *
     * @param id 文件分享信息的ID
     */
    public void deleteById(Integer id) {
        // 调用 shareMapper 的 deleteById 方法根据ID删除文件分享信息
        shareMapper.deleteById(id);
    }

    /**
     * 批量删除文件分享信息
     *
     * @param ids 文件分享信息的ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        // 遍历ID列表
        for (Integer id : ids) {
            // 调用 shareMapper 的 deleteById 方法逐个删除文件分享信息
            shareMapper.deleteById(id);
        }
    }



    /**
     * 修改
     */
    /**
     * 修改分享信息
     *
     * @param share 分享对象，包含要更新的分享信息
     */
    public void updateById(Share share) {
        // 调用shareMapper的updateById方法，根据share对象的ID更新数据库中的分享信息
        shareMapper.updateById(share);
    }

    /**
     * 根据ID查询分享信息
     *
     * @param id 分享ID
     * @return 查询到的分享对象
     */
    public Share selectById(Integer id) {
        // 调用shareMapper的selectById方法，根据ID查询分享信息
        Share share = shareMapper.selectById(id);
        // 调用packageShare方法，对查询到的分享信息进行包装处理
        this.packageShare(share);
        // 返回处理后的分享对象
        return share;
    }

    /**
     * 更新分享访问次数
     *
     * @param id 分享ID
     */
    public void updateCount(Integer id) {
        // 调用shareMapper的updateCount方法，将指定ID的分享访问次数加1
        // 访问次数+1
        shareMapper.updateCount(id);
    }

    /**
     * 查询所有分享信息
     *
     * @param share 分享对象，用于筛选分享信息
     * @return 分享信息列表
     */
    /**
     * 查询所有分享信息
     *
     * @param share 分享对象，用于筛选分享信息
     * @return 分享信息列表
     */
    public List<Share> selectAll(Share share) {
        // 调用shareMapper的selectAll方法，根据share对象筛选分享信息并返回列表
        List<Share> shareList = shareMapper.selectAll(share);
        // 遍历分享列表，对每条分享信息进行包装处理
        for (Share s : shareList) {
            this.packageShare(s);
        }
        // 返回处理后的分享信息列表
        return shareList;
    }

    /**
     * 包装分享信息
     *
     * @param share 分享对象
     */
    private void packageShare(Share share) {
        // 如果分享对象为null，则直接返回
        if (share == null) {
            return;
        }
        // 获取分享截止时间字符串
        String endTime = share.getEndTime();  // 获取截止分享的时间
        // 将截止时间字符串解析为DateTime对象
        DateTime endDatetime = DateUtil.parseDateTime(endTime);
        // 获取当前时间
        DateTime now = new DateTime();
        // 比较截止时间与当前时间的先后顺序
        if (endDatetime.isBeforeOrEquals(now)) {  //  比较截止时间跟当前时间的先后顺序
            // 如果截止时间在当前时间之前或相等，则设置分享状态为“已过期”
            share.setStatus("已过期");
        } else {  // 截止时间在当前时间之后
            // 计算截止时间与当前时间的时差（小时数）
            long offsetHours = endDatetime.between(now, DateUnit.HOUR);
            // 如果时差小于24小时，则设置分享状态为“X小时后过期”
            if (offsetHours < 24) {
                share.setStatus(offsetHours + 1 + "小时后过期");
            } else {
                // 如果时差大于等于24小时，则计算天数并设置分享状态为“X天后过期”
                int days = BigDecimal.valueOf(offsetHours).divide(BigDecimal.valueOf(24), 0, RoundingMode.HALF_UP).intValue();
                share.setStatus(days + "天后过期");
            }
        }
    }

    /**
     * 分页查询分享信息
     *
     * @param share 分享对象，用于筛选分享信息
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @return 分页查询结果
     */
    /**
     * 分页查询分享信息
     *
     * @param share 分享对象，用于筛选分享信息
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @return 分页查询结果
     */
    public PageInfo<Share> selectPage(Share share, Integer pageNum, Integer pageSize) {
        // 使用PageHelper进行分页，设置当前页码和每页记录数
        PageHelper.startPage(pageNum, pageSize);
        // 调用shareMapper的selectAll方法，根据share对象筛选分享信息并返回列表
        List<Share> list = shareMapper.selectAll(share);

        return PageInfo.of(list);
    }

}