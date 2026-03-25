<template>
  <div class="manager-container">
    <!--  头部区域  -->
    <div class="manager-header">
      <!--  头部左侧区域，包含logo和标题  -->
      <div class="manager-header-left">
        <!--  logo图片  -->
        <img src="@/assets/imgs/logo.png" />
        <!--  标题文字  -->
        <div class="title">网盘后台管理</div>
      </div>

      <!--  头部中间区域，包含面包屑导航  -->
      <div class="manager-header-center">
        <!--  面包屑导航组件，使用Element UI的el-breadcrumb组件  -->
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <!--  面包屑导航的第一项，指向首页  -->
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <!--  面包屑导航的第二项，显示当前路由的meta信息中的name字段  -->
          <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!--  头部右侧区域，包含用户信息下拉菜单  -->
      <div class="manager-header-right">
        <!--  下拉菜单组件，使用Element UI的el-dropdown组件  -->
        <el-dropdown placement="bottom">
          <!--  下拉菜单的触发区域，包含用户头像和名称  -->
          <div class="avatar">
            <!--  用户头像，如果user.avatar不存在则使用默认头像  -->
            <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <!--  用户名称，如果user.name不存在则显示“管理员”  -->
            <div>{{ user.name ||  '管理员' }}</div>
          </div>
          <!--  下拉菜单列表，包含多个菜单项  -->
          <el-dropdown-menu slot="dropdown">
            <!--  下拉菜单项，点击后调用goToPerson方法  -->
            <el-dropdown-item @click.native="goToPerson">个人信息</el-dropdown-item>
            <!--  下拉菜单项，点击后跳转到修改密码页面  -->
            <el-dropdown-item @click.native="$router.push('/password')">修改密码</el-dropdown-item>
            <!--  下拉菜单项，点击后调用logout方法  -->
            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!--  主体区域  -->
    <div class="manager-main">
      <!--  侧边栏区域  -->
      <div class="manager-main-left">
        <!--  侧边栏菜单组件，使用Element UI的el-menu组件  -->
        <el-menu :default-openeds="['info', 'user']" router style="border: none" :default-active="$route.path">
          <!--  侧边栏菜单项，指向系统首页  -->
          <el-menu-item index="/home">
            <!--  菜单项的图标  -->
            <i class="el-icon-s-home"></i>
            <!--  菜单项的标题  -->
            <span slot="title">系统首页</span>
          </el-menu-item>

          <!--  信息管理子菜单  -->
          <el-submenu index="info">
            <!--  子菜单的标题  -->
            <template slot="title">
              <!--  子菜单的图标  -->
              <i class="el-icon-menu"></i><span>信息管理</span>
            </template>
            <!--  子菜单项，指向网盘文件页面  -->
            <el-menu-item index="/diskFiles">网盘文件</el-menu-item>
            <!--  子菜单项，指向文件分享页面  -->
            <el-menu-item index="/share">文件分享</el-menu-item>
            <!--  子菜单项，指向垃圾箱管理页面  -->
            <el-menu-item index="/trash">垃圾箱管理</el-menu-item>
            <!--  子菜单项，指向公告信息页面  -->
            <el-menu-item index="/notice">公告信息</el-menu-item>
          </el-submenu>


          <el-submenu index="user">
            <template slot="title">
              <i class="el-icon-menu"></i><span>用户管理</span>
            </template>
            <el-menu-item index="/admin">管理员信息</el-menu-item>
            <el-menu-item index="/user">用户信息</el-menu-item>
          </el-submenu>
        </el-menu>
      </div>

      <!--  数据表格  -->
      <div class="manager-main-right">
        <router-view @update:user="updateUser" />
      </div>
    </div>

  </div>
</template>

<script>
export default {
  // 组件的名称
  name: "Manager",

  // 定义组件的初始数据
  data() {
    return {
      // 从localStorage中获取'xm-user'项，如果不存在则返回'{}'，然后将字符串解析为JSON对象
      // 该对象表示当前登录的用户信息
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    }
  },

  // 组件创建时调用的生命周期钩子
  created() {
    // 检查用户信息中是否存在id，如果不存在则跳转到登录页面
    if (!this.user.id) {
      this.$router.push('/login')
    }
  },

  // 定义组件的方法
  methods: {
    // 更新用户信息的方法
    updateUser() {
      // 重新从localStorage中获取'xm-user'项，并将其解析为JSON对象，然后更新组件的user数据
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}')   // 重新获取下用户的最新信息
    },

    // 跳转到个人信息页面的方法
    goToPerson() {
      // 如果当前用户的角色是'ADMIN'，则跳转到管理员个人信息页面
      if (this.user.role === 'ADMIN') {
        this.$router.push('/adminPerson')
      }
    },

    // 登出方法
    logout() {
      // 从localStorage中移除'xm-user'项，即删除用户登录信息
      localStorage.removeItem('xm-user')
      // 跳转到登录页面
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
/* 导入本地的manager.css样式文件，scoped属性意味着这些样式只作用于当前组件 */
@import "@/assets/css/manager.css";
</style>