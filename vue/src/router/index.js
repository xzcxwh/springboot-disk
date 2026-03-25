import Vue from 'vue'  // 导入Vue框架
import VueRouter from 'vue-router'  // 导入Vue Router

Vue.use(VueRouter)  // 使用Vue Router

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push  // 保存原始的push方法
VueRouter.prototype.push = function push (location) {  // 重写push方法
  return originalPush.call(this, location).catch(err => err)  // 捕获push方法的错误
}

// 定义路由配置
const routes = [
  {
    path: '/',  // 路径为根路径
    name: 'Manager',  // 路由名称为Manager
    component: () => import('../views/Manager.vue'),  // 异步加载Manager组件
    redirect: '/home',  // 重定向到主页
    children: [  // 子路由
      { path: '403', name: 'NoAuth', meta: { name: '无权限' }, component: () => import('../views/manager/403') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'admin', name: 'Admin', meta: { name: '管理员信息' }, component: () => import('../views/manager/Admin') },
      { path: 'adminPerson', name: 'AdminPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/AdminPerson') },
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'notice', name: 'Notice', meta: { name: '公告信息' }, component: () => import('../views/manager/Notice') },
      { path: 'user', name: 'User', meta: { name: '用户信息' }, component: () => import('../views/manager/User') },
      { path: 'diskFiles', name: 'DiskFiles', meta: { name: '网盘文件' }, component: () => import('../views/manager/DiskFiles') },
      { path: 'share', name: 'Share', meta: { name: '文件分享' }, component: () => import('../views/manager/Share') },
      { path: 'trash', name: 'Trash', meta: { name: '垃圾箱' }, component: () => import('../views/manager/Trash') },
    ]
  },
  {
    path: '/front',  // 前台路径
    name: 'Front',  // 前台路由名称为Front
    component: () => import('../views/Front.vue'),  // 异步加载Front组件
    children: [  // 前台子路由
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/front/Home') },
      { path: 'person', name: 'Person', meta: { name: '个人信息' }, component: () => import('../views/front/Person') },
      { path: 'openShare', name: 'OpenShare', meta: { name: '下载分享内容' }, component: () => import('../views/front/OpenShare') },
    ]
  },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },  // 登录路由
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },  // 注册路由
  { path: '*', name: 'NotFound', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },  // 未找到路由
]

const router = new VueRouter({
  mode: 'history',  // 使用history模式
  base: process.env.BASE_URL,  // 基本URL
  routes  // 注册路由配置
})

// 注：不需要前台的项目，可以注释掉该路由守卫
// 路由守卫
// router.beforeEach((to ,from, next) => {
//   let user = JSON.parse(localStorage.getItem("xm-user") || '{}');
//   if (to.path === '/') {
//     if (user.role) {
//       if (user.role === 'USER') {
//         next('/front/home')
//       } else {
//         next('/home')
//       }
//     } else {
//       next('/login')
//     }
//   } else {
//     next()
//   }
// })

export default router  // 导出路由
