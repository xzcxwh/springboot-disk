//总的来说，这段代码是 Vue.js 项目的启动点，
// 它导入了必要的库和组件，配置了 Vue，并创建和挂载了 Vue 实例。这样，应用就可以开始运行了。

import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/css/global.css'
import '@/assets/css/theme/index.css'
import request from "@/utils/request";
import '@/assets/css/iconfont/iconfont.css'

import SlideVerify from 'vue-monoplasty-slide-verify';

Vue.config.productionTip = false

Vue.prototype.$request = request
Vue.prototype.$baseUrl = process.env.VUE_APP_BASEURL

Vue.use(ElementUI, {size: "small"})
Vue.use(SlideVerify);

new Vue({
    router,
    render: h => h(App)
}).$mount('#app')
