<template>
  <div>
    <!-- 取消分享按钮 -->
    <div style="padding: 15px; border-bottom: 1px solid #ddd">
      <el-button type="danger" plain @click="delBatch" :disabled="!tableData.length || !ids.length">取消分享</el-button>
    </div>

    <!-- 文件列表展示 -->
    <el-table size="medium" :data="tableData" stripe @selection-change="handleSelectionChange">
      <!-- 多选列 -->
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <!-- 文件名称列 -->
      <el-table-column label="文件名称">
        <template v-slot="scope">
          <div style="display: flex; cursor: pointer"  @mouseenter="mouseEnter(scope.row)" @mouseleave="mouseLeave(scope.row)">
            <div style="flex: 1">
              <!-- 根据文件类型显示不同图标 -->
              <i style="color: #409EFF" :class="typeList.find(v => v.text === scope.row.type)?.icon || 'el-icon-file'"></i>
              <span v-if="!scope.row.unSave" style="margin-left: 5px">{{ scope.row.name }}</span>
            </div>
            <div style="color: #409EFF; font-size: 14px" v-if="scope.row.optShow">
              <!-- 显示操作按钮 -->
              <el-tooltip content="取消分享" effect="light" :open-delay="1000">
                <i class="el-icon-remove-outline" style="margin-right: 10px; cursor: pointer" @click="del(scope.row.id)"></i>
              </el-tooltip>
              <el-tooltip content="复制链接" effect="light" :open-delay="1000">
                <i class="el-icon-document-copy" style="margin-right: 10px; cursor: pointer" @click="copy(scope.row)"></i>
              </el-tooltip>
            </div>
          </div>
        </template>
      </el-table-column>
      <!-- 分享时间列 -->
      <el-table-column prop="shareTime" label="分享时间" width="200"></el-table-column>
      <!-- 状态列 -->
      <el-table-column prop="status" label="状态" width="200">
        <template v-slot="scope">
          <!-- 如果状态包含“小时”则显示红色，否则正常显示 -->
          <span style="color: red" v-if="scope.row.status.includes('小时')">{{ scope.row.status }}</span>
          <span v-else>{{ scope.row.status }}</span>
        </template>
      </el-table-column>
      <!-- 访问次数列 -->
      <el-table-column prop="count" label="访问次数" width="200"></el-table-column>
    </el-table>
  </div>
</template>


<template>
  <div>
    <!-- 取消分享按钮 -->
    <div style="padding: 15px; border-bottom: 1px solid #ddd">
      <el-button type="danger" plain @click="delBatch" :disabled="!tableData.length || !ids.length">取消分享</el-button>
    </div>

    <!-- 文件列表展示 -->
    <el-table size="medium" :data="tableData" stripe @selection-change="handleSelectionChange">
      <!-- 多选列 -->
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <!-- 文件名称列 -->
      <el-table-column label="文件名称">
        <template v-slot="scope">
          <div style="display: flex; cursor: pointer"  @mouseenter="mouseEnter(scope.row)" @mouseleave="mouseLeave(scope.row)">
            <div style="flex: 1">
              <!-- 根据文件类型显示不同图标 -->
              <i style="color: #409EFF" :class="typeList.find(v => v.text === scope.row.type)?.icon || 'el-icon-file'"></i>
              <span v-if="!scope.row.unSave" style="margin-left: 5px">{{ scope.row.name }}</span>
            </div>
            <div style="color: #409EFF; font-size: 14px" v-if="scope.row.optShow">
              <!-- 显示操作按钮 -->
              <el-tooltip content="取消分享" effect="light" :open-delay="1000">
                <i class="el-icon-remove-outline" style="margin-right: 10px; cursor: pointer" @click="del(scope.row.id)"></i>
              </el-tooltip>
              <el-tooltip content="复制链接" effect="light" :open-delay="1000">
                <i class="el-icon-document-copy" style="margin-right: 10px; cursor: pointer" @click="copy(scope.row)"></i>
              </el-tooltip>
            </div>
          </div>
        </template>
      </el-table-column>
      <!-- 分享时间列 -->
      <el-table-column prop="shareTime" label="分享时间" width="200"></el-table-column>
      <!-- 状态列 -->
      <el-table-column prop="status" label="状态" width="200">
        <template v-slot="scope">
          <!-- 如果状态包含“小时”则显示红色，否则正常显示 -->
          <span style="color: red" v-if="scope.row.status.includes('小时')">{{ scope.row.status }}</span>
          <span v-else>{{ scope.row.status }}</span>
        </template>
      </el-table-column>
      <!-- 访问次数列 -->
      <el-table-column prop="count" label="访问次数" width="200"></el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  name: "ShareComponent",  // 组件名称
  props: {
    typeList: null  // 接收的属性，文件类型列表
  },
  data() {
    return {
      tableData: [],  // 表格数据
      ids: []  // 选中行的id集合
    }
  },
  created() {
    this.load()  // 组件创建时加载数据
  },
  methods: {
    copy(row) {  // 复制分享链接
      let currentUrl = location.href.substring(0, location.href.indexOf('/front'))  // 获取当前页面的URL
      let shareLink = currentUrl + '/front/openShare?code=' + row.code + '&shareId=' + row.id  // 构建分享链接

      let _input = document.createElement("input");   // 创建一个input元素
      _input.value = shareLink;  // 设置input的值为分享链接
      document.body.appendChild(_input);    // 将input添加到页面
      _input.select();   // 选择input的内容
      document.execCommand("Copy");   // 执行复制操作
      document.body.removeChild(_input)  // 复制完成后移除input元素
      this.$message.success("复制成功")  // 弹出复制成功的提示
    },
    del(id) {   // 单个删除分享
      this.$confirm('您确定取消分享吗？', '确认取消分享', {type: "warning"}).then(response => {  // 弹出确认框
        this.$request.delete('/share/delete/' + id).then(res => {  // 发送请求取消分享
          if (res.code === '200') {   // 如果操作成功
            this.$message.success('操作成功')  // 弹出操作成功的提示
            this.load()  // 重新加载数据
          } else {
            this.$message.error(res.msg)  // 弹出错误信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的行发生改变时触发
      this.ids = rows.map(v => v.id)   // 更新选中行的id集合
    },
    delBatch() {   // 批量取消分享
      if (!this.ids.length) {
        this.$message.warning('请选择数据')  // 提示未选择数据
        return
      }
      this.$confirm('您确定批量取消分享吗？', '确认取消分享', {type: "warning"}).then(response => {  // 弹出确认框
        this.$request.delete('/share/delete/batch', {data: this.ids}).then(res => {  // 发送批量取消分享的请求
          if (res.code === '200') {   // 如果操作成功
            this.$message.success('操作成功')  // 弹出操作成功的提示
            this.load()  // 重新加载数据
          } else {
            this.$message.error(res.msg)  // 弹出错误信息
          }
        })
      }).catch(() => {
      })
    },
    load() {  // 加载数据
      this.$request.get('/share/selectAll').then(res => {  // 请求获取分享数据
        this.tableData = res.data || []  // 更新表格数据
      })
    },
    mouseEnter(row) {  // 鼠标移入行
      this.$set(row, 'optShow', true)  // 设置显示操作按钮
    },
    mouseLeave(row) {  // 鼠标移出行
      this.$set(row, 'optShow', false)  // 隐藏操作按钮
    }
  }
}
</script>


<style scoped>

</style>