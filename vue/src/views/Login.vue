<template>
  <div class="container">
    <!-- 定义一个高度为60像素、位置固定的顶部导航栏，使其始终显示在页面的顶部 -->
    <div style="height: 60px; position: fixed; top: 0; display: flex; align-items: center; padding-left: 20px">
      <!-- 在导航栏内部放置一个图片元素，显示公司的Logo -->
      <img src="@/assets/imgs/logo.png" alt="" style="width: 50px">

      <!-- 在Logo图片的右侧，添加一个文本元素，显示“徐志聪网盘系统”的标题 -->
      <span style="color: #409EFF; font-size: 24px; font-weight: bold; margin-left: 5px">徐志聪网盘系统</span>
    </div>

    <!-- 定义一个区域，用于放置背景图片，占据剩余空间 -->
    <div style="flex: 1; display: flex; justify-content: center">
      <img src="@/assets/imgs/bg.png" alt="" style="width: 80%">
    </div>

    <!-- 定义一个区域，用于放置登录表单，占据剩余空间 -->
    <div style="flex: 1; display: flex; justify-content: center">
      <div style="width: 350px; position: relative; padding: 50px 30px; box-shadow: 0 0 10px rgba(0,0,0,.2); background-color: white; border-radius: 5px;">
        <!-- 如果slideVerifyShow为真，则显示滑动验证组件 -->
        <div style="position: absolute; background-color: white; top: 100px; z-index: 999" v-if="slideVerifyShow">
          <!-- 滑动验证组件，设置相关属性并绑定事件 -->
          <slide-verify :l="42"
                        :r="10"
                        :w="300"
                        :h="155"
                        :accuracy="5"
                        :imgs="imgs"
                        slider-text="向右滑动"
                        @success="onSuccess"
                        ref="slideVerifyRef"
          ></slide-verify>
        </div>
        <!-- 显示登录欢迎语 -->
        <div style="text-align: center; font-size: 24px; font-weight: bold; margin-bottom: 30px; color: #333">欢 迎 登 录</div>
        <!-- Element UI的表单组件，绑定数据模型、规则及引用 -->
        <el-form :model="form" :rules="rules" ref="formRef">
          <!-- 账号输入框 -->
          <el-form-item prop="username">
            <el-input size="medium" prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
          </el-form-item>
          <!-- 密码输入框，带有显示密码功能 -->
          <el-form-item prop="password">
            <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password"></el-input>
          </el-form-item>
          <!-- 角色选择下拉框 -->
          <el-form-item prop="role">
            <el-select style="width: 100%" size="medium" v-model="form.role">
              <el-option value="ADMIN" label="管理员"></el-option>
              <el-option value="USER" label="用户"></el-option>
            </el-select>
          </el-form-item>
          <!-- 登录按钮，点击时触发登录方法 -->
          <el-form-item>
            <el-button size="medium" style="width: 100%; background-color: #2a60c9; border-color: #2a60c9; color: white" @click="login">登 录</el-button>
          </el-form-item>
          <!-- 注册提示信息，提供注册链接 -->
          <div style="display: flex; align-items: center">
            <div style="flex: 1"></div>
            <div style="flex: 1; text-align: right">
              还没有账号？请 <a href="/register">注册</a>
            </div>
          </div>
        </el-form>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  // 组件名称为Login
  name: "Login",
  data() {
    return {
      // 对话框是否可见
      dialogVisible: true,
      // 表单数据对象
      form: { role: 'ADMIN' },
      // 表单验证规则对象
      rules: {
        // 用户名验证规则
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        // 密码验证规则
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ]
      },
      // 图片数组，用于展示
      imgs: [
        // 引入图片资源
        require('@/assets/imgs/1.jpg'),
        require('@/assets/imgs/2.jpg'),
        require('@/assets/imgs/3.jpg'),
      ],
      // 是否显示滑动验证
      slideVerifyShow: false
    }
  },
  // 组件创建时调用
  created() {

  },
  methods: {
    // 登录成功的回调函数
    onSuccess() {
      // 发起POST请求到/login接口，携带表单数据
      this.$request.post('/login', this.form).then(res => {
        // 如果返回状态码为200
        if (res.code === '200') {
          // 将用户数据存储到localStorage中
          localStorage.setItem("xm-user", JSON.stringify(res.data))  // 存储用户数据
          // 显示登录成功提示
          this.$message.success('登录成功')
          // 延迟500毫秒后根据用户角色进行页面跳转
          setTimeout(() => {
            if (res.data.role === 'ADMIN') {
              // 如果是管理员角色，跳转到/home页面
              location.href = '/home'
            } else {
              // 如果是其他角色，跳转到/front/home页面，并传递category参数为all
              location.href = '/front/home?category=all'
            }
          }, 500)
        } else {
          // 如果登录失败，显示错误提示
          this.$message.error(res.msg)
          // 隐藏滑动验证
          this.slideVerifyShow = false
        }
      })
    },
    // 登录按钮点击事件
    login() {
      // 调用表单验证方法
      this.$refs['formRef'].validate((valid) => {
        // 如果验证通过
        if (valid) {
          // 显示滑动验证
          this.slideVerifyShow = true
          // 验证通过，后续可以在这里添加其他逻辑
          // 验证通过
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/imgs/jxqy.jpg");
  background-size: 100%;
  display: flex;
  align-items: center;
  /*justify-content: center;*/
  /*color: #666;*/
}
a {
  color: #2a60c9;
}
</style>