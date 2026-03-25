<template>
  <div>
    <div style="display: flex; color: #666">
      <!-- 左侧菜单区域 -->
      <div style="width: 200px; text-align: center;  border-right: 1px solid #ddd; min-height: calc(100vh - 86px)">
        <!-- 第一部分：分类列表 -->
        <!-- 注释：这里显示的是文件分类列表 -->
        <div style="padding: 10px 0">
          <!-- 遍历categoryList数组，渲染分类项 -->
          <!-- 注释：每个分类项都有点击事件，点击后调用loadFiles方法，并传递当前分类 -->
          <div @click="loadFiles(item.category)" class="category" style="padding: 10px 0" v-for="item in categoryList" :key="item.text"
               :class="{ 'category-active' : category === item.category}">
            <!-- 如果分类项有图标，显示图标 -->
            <!-- 注释：这里使用v-if判断分类项是否有图标，如果有则显示 -->
            <i v-if="item.icon" :class="item.icon" style="margin-right: 5px"></i>
            <!-- 显示分类项名称 -->
            <!-- 注释：这里显示的是分类项的文本内容 -->
            <span>{{ item.text }}</span>
          </div>
        </div>

        <!-- 第二部分：固定分类项 -->
        <!-- 注释：这里显示的是固定的两个分类项：我的分享和回收站 -->
        <div style="border-top: 1px solid #ddd; text-align: center; padding-top: 10px">
          <!-- 我的分享分类项 -->
          <!-- 注释：点击后会调用loadFiles方法，并传递'share'作为参数 -->
          <div @click="loadFiles('share')" style="padding: 10px 0; display: flex; justify-content: center" class="category" :class="{ 'category-active' : category === 'share'}">
            <div style="width: 75px; text-align: left">
              <!-- 分享图标 -->
              <!-- 注释：显示一个分享图标 -->
              <i class="el-icon-share" style="margin-right: 5px"></i>
              <!-- 分类项名称 -->
              <!-- 注释：这里显示的是“我的分享”文本 -->
              <span>我的分享</span>
            </div>
          </div>
          <!-- 回收站分类项 -->
          <!-- 注释：点击后会调用loadFiles方法，并传递'trash'作为参数 -->
          <div @click="loadFiles('trash')" style="padding: 10px 0; display: flex; justify-content: center" class="category" :class="{ 'category-active' : category === 'trash'}">
            <div style="width: 75px; text-align: left">
              <!-- 删除图标 -->
              <!-- 注释：显示一个删除图标 -->
              <i class="el-icon-delete" style="margin-right: 5px"></i>
              <!-- 分类项名称 -->
              <!-- 注释：这里显示的是“回收站”文本 -->
              <span>回收站</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧内容区域 -->
      <!-- 注释：这里是根据左侧选中的分类来显示对应的内容组件 -->
      <div style="flex: 1">
        <!-- 如果当前分类是'trash'，显示回收站组件 -->
        <!-- 注释：Trash组件用于展示回收站的内容 -->
        <Trash :type-list="typeList"  v-if="category === 'trash'" />
        <!-- 如果当前分类是'share'，显示分享组件 -->
        <!-- 注释：Share组件用于展示分享的内容 -->
        <Share :type-list="typeList" v-if="category === 'share'" />
        <!-- 如果当前分类不是'trash'和'share'，显示主内容组件 -->
        <!-- 注释：Main组件用于展示主内容区域 -->
        <Main :type-list="typeList" v-else />
      </div>
    </div>
  </div>
</template>

<script>
// 导入 Main 组件
import Main from "@/components/Main";
// 导入 Trash 组件
import Trash from "@/components/Trash";
// 导入 Share 组件
import Share from "@/components/Share";

// 导出默认对象，该对象定义了当前 Vue 组件
export default {
  // 定义组件内部使用的子组件
  components: {
    Share,    // Share 组件
    Trash,    // Trash 组件
    Main      // Main 组件
  },

  // 定义组件的响应式数据对象
  data() {
    return {
      // 定义一个包含文件分类信息的数组
      categoryList: [
        {"text": '全部文件', category: 'all', icon: ''},  // 全部文件分类
        {"text": '图片', category: 'img', icon: 'el-icon-picture-outline'},  // 图片分类
        {"text": '视频', category: 'video', icon: 'el-icon-video-play'},  // 视频分类
        {"text": '压缩', category: 'zip', icon: 'el-icon-box'},  // 压缩文件分类
      ],
      // 定义一个包含文件类型信息的数组
      typeList: [
        { text: 'mp3', icon: 'el-icon-mp3' },  // mp3 类型
        { text: 'mp4', icon: 'el-icon-mp4' },  // mp4 类型
        { text: 'jpg', icon: 'el-icon-jpg' },  // jpg 类型
        { text: 'jpeg', icon: 'el-icon-jpeg' },  // jpeg 类型
        { text: 'png', icon: 'el-icon-png' },  // png 类型
        { text: 'pdf', icon: 'el-icon-pdf' },  // pdf 类型
        { text: 'docx', icon: 'el-icon-docx' },  // docx 类型
        { text: 'txt', icon: 'el-icon-text' },  // txt 类型
        { text: 'zip', icon: 'el-icon-zip' },  // zip 类型
        { text: 'folder', icon: 'el-icon-folder' },  // 文件夹类型
      ],
      // 从本地存储中获取用户信息，并解析为 JSON 对象
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      // 从路由查询参数中获取分类信息，若不存在则设置为 null
      category: this.$route.query.category || null,  // 分类
    }
  },

  // 定义组件挂载后的操作
  mounted() {
    // 挂载后的逻辑代码可以放在这里，当前为空
  },

  // 定义组件的方法
  methods: {
    // 定义一个用于加载文件的方法，接收分类作为参数
    loadFiles(category) {
      // 通过修改 location.href 来触发页面跳转，并将分类作为查询参数传递
      location.href = '/front/home?category=' + category
    }
  }
}
</script>

<style scoped>
.category {
  cursor: pointer;
}
.category:hover {
  background-color: #f8f8f8;
  color: #000;
}
.category-active {
  color: #409EFF;
}
</style>
