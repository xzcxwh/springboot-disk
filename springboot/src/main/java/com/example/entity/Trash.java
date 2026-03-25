package com.example.entity; // 定义实体类所在的包名

import java.io.Serializable; // 导入Java的序列化接口，使得该实体类可以被序列化

public class Trash implements Serializable { // 定义一个名为Trash的公共类，并实现Serializable接口
    private static final long serialVersionUID = 1L; // 定义序列化版本号，用于在反序列化时确保类的版本一致性

    /** ID */ // 注释：ID，通常用于唯一标识该实体
    private Integer id; // 定义一个私有的Integer类型变量id，用于存储ID

    /** 文件ID */ // 注释：文件ID，用于标识与该实体关联的文件
    private Integer fileId; // 定义一个私有的Integer类型变量fileId，用于存储文件ID

    private Integer userId; // 定义一个私有的Integer类型变量userId，用于存储用户ID

    /** 文件名称 */ // 注释：文件名称，用于描述文件的名称
    private String name; // 定义一个私有的String类型变量name，用于存储文件名称

    /** 文件大小 */ // 注释：文件大小，用于描述文件的大小
    private Double size; // 定义一个私有的Double类型变量size，用于存储文件大小

    /** 删除时间 */ // 注释：删除时间，用于记录文件的删除时间
    private String time; // 定义一个私有的String类型变量time，用于存储删除时间

    private String userName; // 定义一个私有的String类型变量userName，用于存储用户名

    private String type; // 定义一个私有的String类型变量type，用于存储类型

    // 以下是一系列的getter和setter方法，用于获取和设置上述定义的私有变量

    public String getType() { // 定义一个公共方法，用于获取type变量的值
        return type; // 返回type变量的值
    }

    public void setType(String type) { // 定义一个公共方法，用于设置type变量的值
        this.type = type; // 将传入的参数值赋给type变量
    }

    public String getUserName() { // 定义一个公共方法，用于获取userName变量的值
        return userName; // 返回userName变量的值
    }

    public void setUserName(String userName) { // 定义一个公共方法，用于设置userName变量的值
        this.userName = userName; // 将传入的参数值赋给userName变量
    }

    public Integer getId() { // 定义一个公共方法，用于获取id变量的值
        return id; // 返回id变量的值
    }

    public void setId(Integer id) { // 定义一个公共方法，用于设置id变量的值
        this.id = id; // 将传入的参数值赋给id变量
    }

    public Integer getFileId() { // 定义一个公共方法，用于获取fileId变量的值
        return fileId; // 返回fileId变量的值
    }

    public void setFileId(Integer fileId) { // 定义一个公共方法，用于设置fileId变量的值
        this.fileId = fileId; // 将传入的参数值赋给fileId变量
    }

    public Integer getUserId() { // 定义一个公共方法，用于获取userId变量的值
        return userId; // 返回userId变量的值
    }

    public void setUserId(Integer userId) { // 定义一个公共方法，用于设置userId变量的值
        this.userId = userId; // 将传入的参数值赋给userId变量
    }

    public String getName() { // 定义一个公共方法，用于获取name变量的值
        return name; // 返回name变量的值
    }

    public void setName(String name) { // 定义一个公共方法，用于设置name变量的值
        this.name = name; // 将传入的参数值赋给name变量
    }

    public Double getSize() { // 定义一个公共方法，用于获取size变量的值
        return size; // 返回size变量的值
    }

    public void setSize(Double size) { // 定义一个公共方法，用于设置size变量的值
        this.size = size; // 将传入的参数值赋给size变量
    }

    public String getTime() { // 定义一个公共方法，用于获取time变量的值
        return time; // 返回time变量的值
    }

    public void setTime(String time) { // 定义一个公共方法，用于设置time变量的值
        this.time = time; // 将传入的参数值赋给time变量
    }
}