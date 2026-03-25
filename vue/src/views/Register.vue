<template>
  <!-- 定义一个容器div -->
  <div class="container">
    <!-- 顶部导航栏，固定位置，显示logo和标题 -->
    <div style="height: 60px; position: fixed; top: 0; display: flex; align-items: center; padding-left: 20px">
      <!-- 放置logo图片 -->
      <img src="@/assets/imgs/logo.png" alt="" style="width: 50px">
      <!-- 标题文字 -->
      <span style="color: #409EFF; font-size: 24px; font-weight: bold; margin-left: 5px">个人网盘系统</span>
    </div>
    <!-- 主体内容部分，居中显示 -->
    <div style="display: flex; justify-content: center; width: 100%">
      <!-- 注册表单容器 -->
      <div style="width: 350px; padding: 50px 30px; box-shadow: 0 0 10px rgba(0,0,0,.2); background-color: #e2ceff; border-radius: 5px;">
        <!-- 注册欢迎词 -->
        <div style="text-align: center; font-size: 24px; font-weight: bold; margin-bottom: 30px; color: #333">欢 迎 注 册</div>
        <!-- Element UI 的表单组件，用于收集用户输入 -->
        <el-form :model="form" :rules="rules" ref="formRef">
          <!-- 账号输入框 -->
          <el-form-item prop="username">
            <el-input size="medium" prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
          </el-form-item>
          <!-- 密码输入框 -->
          <el-form-item prop="password">
            <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password v-model="form.password"></el-input>
          </el-form-item>
          <!-- 确认密码输入框 -->
          <el-form-item prop="confirmPass">
            <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请确认密码" show-password v-model="form.confirmPass"></el-input>
          </el-form-item>
          <!-- 注册按钮 -->
          <el-form-item>
            <el-button style="width: 100%; background-color: #2a60c9; border-color: #2a60c9; color: white" @click="register">注 册</el-button>
          </el-form-item>
          <!-- 登录提示部分 -->
          <div style="display: flex; align-items: center">
            <!-- 空白占位 -->
            <div style="flex: 1"></div>
            <!-- 文本提示和登录链接 -->
            <div style="flex: 1; text-align: right">
              已有账号？请 <a href="/login">登录</a>
            </div>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  // 组件名称
  name: "Register",
  data() {
    return {
      // 表单数据
      form: { role: 'USER' },
      // 表单验证规则
      rules: {
        username: [
          // 用户名验证规则
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
          // 密码验证规则
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        confirmPass: [
          // 确认密码验证规则，使用自定义验证函数
          { validator: validatePassword, trigger: 'blur' }
        ]
      }
    }

    // 自定义的验证密码函数
    // rule: 验证规则
    // confirmPass: 确认密码的值
    // callback: 回调函数，验证结果通过此函数返回
    const validatePassword = (rule, confirmPass, callback) => {
      // 如果确认密码为空
      if (confirmPass === '') {
        // 调用回调函数，返回错误信息
        callback(new Error('请确认密码'))
      } else if (confirmPass !== this.form.password) {
        // 如果确认密码与原始密码不一致
        // 调用回调函数，返回错误信息
        callback(new Error('两次输入的密码不一致'))
      } else {
        // 如果验证通过
        // 调用回调函数，不传递错误信息，表示验证成功
        callback()
      }
    }
  },
  // 组件创建后调用的生命周期钩子
  created() {

  },
  methods: {
    // 注册方法
    register() {
      // 调用表单的验证方法
      this.$refs['formRef'].validate((valid) => {
        // 如果验证通过
        if (valid) {
          // 发起注册请求
          // this.$request.post 是假设的一个用于发起 POST 请求的方法
          this.$request.post('/register', this.form).then(res => {
            // 请求成功后的回调
            if (res.code === '200') {
              // 如果返回状态码为200，表示注册成功
              // 跳转到登录页面
              this.$router.push('/login')
              // 显示成功提示信息
              this.$message.success('注册成功')
            } else {
              // 如果注册失败，显示错误信息
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>
<style scoped>
/* 定义一个名为container的类，它的样式仅在当前组件中生效 */
.container {
  /* 设置容器的高度为视口高度的100% */
  height: 100vh;
  /* 隐藏超出容器的内容，避免滚动条出现 */
  overflow: hidden;
  /* 设置背景图片，路径为项目的assets目录下的imgs文件夹中的logo.png */
  background-image: url("@/assets/imgs/logo.png");
  /* 背景图片大小设置为容器的100% */
  background-size: 100%;
  /* 使用flex布局 */
  display: flex;
  /* 垂直方向上居中对齐子元素 */
  align-items: center;
  /* 这行代码被注释掉了，如果取消注释，水平方向上会居中对齐子元素 */
  /*justify-content: center;*/
  /* 这行代码也被注释掉了，如果取消注释，会设置文字颜色为深灰色 */
  /*color: #666;*/
}

/* 定义a标签的样式 */
a {
  /* 设置a标签的文本颜色为深蓝色 */
  color: #2a60c9;
}
</style>