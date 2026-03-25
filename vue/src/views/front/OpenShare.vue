<template>
  <div>
    <!-- 当validateFailed为true时，显示错误信息 -->
    <div style="margin: 150px auto; text-align: center; font-size: 30px; color: #666" v-if="validateFailed">分享文件不存在或已被取消</div>

    <!-- 当validateFailed为false时，显示分享文件信息以及文件列表 -->
    <div class="card" style="margin: 5px auto 100px auto; width: 70%" v-else>
      <!-- 显示分享文件的名称和分享时间 -->
      <div style="color: #666; display: flex; align-items: center">
        分享：<strong style="color: #333">{{ share.name }}</strong>
        <i class="el-icon-time" style="margin-left: 20px; margin-right: 5px"></i> {{ share.shareTime }}
        <!-- 显示分享状态，如果包含"小时"，则以红色显示 -->
        <span style="margin-left: 20px">
          <i class="el-icon-info" style="margin-right: 5px"></i>
          <span v-if="share.status?.includes('小时')" style="color: red">{{ share.status }}</span>
          <span v-else>{{ share.status }}</span>
        </span>
      </div>

      <div>
        <!-- 文件链接和文件夹链接的显示区域 -->
        <div style="padding: 15px 0; margin-top: 30px">
          <!-- 显示全部文件的链接，如果folders数组有内容，则显示箭头 -->
          <a style="color: #666" :href="'/front/openShare?code=' + code + '&shareId=' + shareId">全部文件 <i class="el-icon-arrow-right" v-if="folders.length"></i></a>
          <!-- 遍历folders数组，显示每个文件夹的链接，并在非最后一个文件夹后显示箭头 -->
          <a style="color: #666" :href="'/front/openShare?code=' + code + '&shareId=' + shareId + '&folderId=' + item.id" v-for="(item, index) in folders" :key="item.id">
            {{ item.name }} <i class="el-icon-arrow-right" v-if="index !== folders.length - 1"></i>
          </a>
        </div>

        <!-- 文件表格显示 -->
        <el-table size="medium" stripe :data="tableData">
          <!-- 文件名称列 -->
          <el-table-column label="名称">
            <template v-slot="scope">
              <div style="display: flex; cursor: pointer" @click="openFile(scope.row)">
                <div style="flex: 1">
                  <!-- 显示文件类型的图标和文件名称，如果文件未保存，则不显示名称 -->
                  <i style="color: #409EFF" :class="typeList.find(v => v.text === scope.row.type)?.icon || 'el-icon-file'"></i>
                  <span v-if="!scope.row.unSave" style="margin-left: 5px">{{ scope.row.name }}</span>
                </div>
                <!-- 如果文件夹不是'否'，则显示下载文件的提示和按钮 -->
                <div style="color: #409EFF; font-size: 14px">
                  <el-tooltip content="下载" effect="light" :open-delay="1000" v-if="scope.row.folder === '否'">
                    <i class="el-icon-download" style="margin-right: 10px; cursor: pointer" @click.stop="download(scope.row.file)"></i>
                  </el-tooltip>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="updateTime" label="修改时间" width="200"></el-table-column>
          <el-table-column prop="size" label="文件大小（KB）" width="200"></el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "OpenShare",
  data() {
    // 返回一个包含初始数据状态的对象
    return {
      // 从路由的查询参数中获取code
      code: this.$route.query.code,
      // 从路由的查询参数中获取shareId
      shareId: this.$route.query.shareId,
      // 初始化share对象，用于存储分享的相关信息
      share: {},
      // 验证失败的标志，默认为false
      validateFailed: false,   // 验证失败
      // 初始化文件夹列表为空数组
      folders: [],
      // 定义文件类型及其对应的图标列表
      typeList: [
        { text: 'mp3', icon: 'el-icon-mp3' },  // mp3文件类型及其图标
        { text: 'mp4', icon: 'el-icon-mp4' },  // mp4文件类型及其图标
        { text: 'jpg', icon: 'el-icon-jpg' },  // jpg文件类型及其图标
        { text: 'jpeg', icon: 'el-icon-jpeg' }, // jpeg文件类型及其图标
        { text: 'png', icon: 'el-icon-png' },  // png文件类型及其图标
        { text: 'pdf', icon: 'el-icon-pdf' },  // pdf文件类型及其图标
        { text: 'docx', icon: 'el-icon-docx' }, // docx文件类型及其图标
        { text: 'txt', icon: 'el-icon-text' },  // txt文件类型及其图标
        { text: 'zip', icon: 'el-icon-zip' },   // zip文件类型及其图标
        { text: 'folder', icon: 'el-icon-folder' }, // 文件夹类型及其图标
      ],
      // 初始化表格数据为空数组
      tableData: [],
      // 从路由的查询参数中获取folderId，并赋值给全局的folderId，用于后续查询数据
      folderId: this.$route.query.folderId   // 注意给 全局的folderId 赋值，要不然后面没法查询数据
    }
  },
  created() {
    // 组件创建时执行此方法
    if (!this.code || !this.shareId) {
      // 如果缺少必要的参数code或shareId
      this.validateFailed = true  // 将验证失败标志设置为true
      return  // 终止后续代码的执行
    }

    this.load()  // 调用load方法，加载分享数据
  },
  methods: {
    openFile(row) {
      // 定义一个方法，用于打开文件或文件夹
      if (row.folder === '是') {
        // 如果当前行数据是文件夹（row.folder的值为'是'）
        // 拼接URL，并跳转到对应文件夹的分享页面
        location.href = '/front/openShare?code=' + this.code + '&shareId=' + this.shareId + '&folderId=' + row.id
      } else {
        // 如果当前行数据是文件
        // 在新窗口或标签页中预览该文件
        window.open(this.$baseUrl + '/diskFiles/preview/' + row.id)
      }
    },
    download(url) {
      // 定义一个方法，用于下载文件
      window.open(url)  // 在新窗口或标签页中打开下载链接，进行文件下载
    },
    load() {
      // 使用请求库向 '/share/selectById/' + this.shareId 发送 GET 请求，获取分享信息
      this.$request.get('/share/selectById/' + this.shareId).then(res=> {
        // 将响应体中的 data 字段赋值给 this.share，若不存在则赋值为空对象
        this.share = res.data || {}
        // 如果响应体中存在 data 字段，则将 data 字段包装成数组赋值给 this.tableData，否则赋值为空数组
        this.tableData = res.data ? [res.data] : []
        // 判断分享的状态是否为 '已过期'
        if (this.share.status === '已过期') {
          // 如果分享已过期，则将 this.validateFailed 设置为 true
          this.validateFailed = true
        }

        // 验证响应体中的 code 字段是否与 this.code 一致
        // 验证code对不对
        if (this.share.code !== this.code) {
          // 如果 code 不一致，则将 this.validateFailed 设置为 true
          this.validateFailed = true
        } else {
          // 如果 code 一致，则调用 loadFiles 方法加载文件和文件夹信息
          this.loadFiles()
        }
      })
    },
    loadFiles() {
      // 发送 GET 请求到 '/diskFiles/selectShare'，查询当前目录的数据
      // 查询当前目录的数据
      this.$request.get('/diskFiles/selectShare', {
        params: {
          // 将 this.shareId 或 null 作为 shareId 参数传递
          shareId: this.shareId || null,
          // 将 this.folderId 或 null 作为 folderId 参数传递
          folderId: this.folderId || null
        }
      }).then(res => {
        // 将响应体中的 data 字段赋值给 this.tableData，若不存在则赋值为空数组
        this.tableData = res.data || []
      })

      // 发送 GET 请求到 '/diskFiles/selectFolders'，查询当前目录的路径数据
      // 查询当前目录的路径数据
      this.$request.get('/diskFiles/selectFolders', {
        params: {
          // 将 this.folderId 或 null 作为 folderId 参数传递
          folderId: this.folderId || null
        }
      }).then(res => {
        // 将响应体中的 data 字段赋值给 this.folders，若不存在则赋值为空数组
        this.folders = res.data || []
      })
    }
  }
}
</script>

<style scoped>

</style>