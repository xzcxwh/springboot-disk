<template>
  <div>
    <!-- 定义一个包含按钮和表格的容器 -->
    <div style="padding: 15px; border-bottom: 1px solid #ddd">
      <!-- 定义一个危险类型的按钮，点击时触发delBatch方法，用于批量删除 -->
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>
    <!-- 定义一个中等大小的表格，数据绑定到tableData，有条纹效果，选择变化时触发handleSelectionChange方法 -->
    <el-table size="medium" :data="tableData" stripe @selection-change="handleSelectionChange">
      <!-- 定义一个选择框列，宽度为55，内容居中对齐 -->
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <!-- 定义一个表格列，标签为“文件名称” -->
      <el-table-column label="文件名称">
        <!-- 使用模板插槽来自定义列的内容 -->
        <template v-slot="scope">
          <!-- 定义一个可点击的flex容器，鼠标进入和离开时分别触发mouseEnter和mouseLeave方法 -->
          <div style="display: flex; cursor: pointer"  @mouseenter="mouseEnter(scope.row)" @mouseleave="mouseLeave(scope.row)">
            <!-- flex容器的一部分，显示文件类型的图标和名称 -->
            <div style="flex: 1">
              <!-- 根据scope.row.type的值在typeList中查找对应的图标，否则使用默认的el-icon-file图标 -->
              <i style="color: #409EFF" :class="typeList.find(v => v.text === scope.row.type)?.icon || 'el-icon-file'"></i>
              <!-- 如果scope.row.unSave为假，则显示文件名 -->
              <span v-if="!scope.row.unSave" style="margin-left: 5px">{{ scope.row.name }}</span>
            </div>
            <!-- 如果scope.row.optShow为真，则显示删除和还原的提示和图标 -->
            <div style="color: #409EFF; font-size: 14px" v-if="scope.row.optShow">
              <!-- 删除提示和图标，点击时触发del方法，传入scope.row.fileId作为参数 -->
              <el-tooltip content="删除" effect="light" :open-delay="1000">
                <i class="el-icon-delete" style="margin-right: 10px; cursor: pointer" @click="del(scope.row.fileId)"></i>
              </el-tooltip>
              <!-- 还原提示和图标，点击时触发restore方法，传入scope.row.fileId作为参数 -->
              <el-tooltip content="还原" effect="light" :open-delay="1000">
                <i class="el-icon-refresh" style="margin-right: 10px; cursor: pointer" @click="restore(scope.row.fileId)"></i>
              </el-tooltip>
            </div>
          </div>
        </template>
      </el-table-column>
      <!-- 定义一个表格列，显示“删除时间”，数据绑定到scope.row.updateTime，宽度为300 -->
      <el-table-column prop="updateTime" label="删除时间" width="300"></el-table-column>
      <!-- 定义一个表格列，显示“文件大小（KB）”，数据绑定到scope.row.size，宽度为300 -->
      <el-table-column prop="size" label="文件大小（KB）" width="300"></el-table-column>
    </el-table>
  </div>
</template>


<script>
// 导出默认组件对象
export default {
  // 组件名称
  name: "Trash",
  // 组件接收的属性列表
  props: {
    // 接收一个名为typeList的属性，其值可以为任意类型，这里初始化为null
    typeList: null
  },
  // 组件的初始数据对象
  data() {
    return {
      // 初始化一个名为tableData的空数组，用于存放表格数据
      tableData: []
    }
  },
  // 组件创建后调用的钩子函数
  created() {
    // 组件创建后立即调用load方法加载数据
    this.load()
  },
  // 组件的方法列表
  methods: {
    // 恢复文件的方法，传入文件ID
    restore(fileId) {
      // 发起PUT请求到指定URL，以恢复文件
      this.$request.put('/diskFiles/restore/' + fileId).then(res => {
        // 如果返回的状态码为200，表示操作成功
        if (res.code === '200') {
          // 弹出操作成功的提示信息
          this.$message.success('操作成功')
          // 重新加载数据
          this.load()
        } else {
          // 否则弹出错误信息
          this.$message.error(res.msg)
        }
      })
    },
    // 单个删除文件的方法，传入文件ID
    del(fileId) {
      // 弹出确认框，询问用户是否确定删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        // 发起DELETE请求到指定URL，以删除文件
        this.$request.delete('/diskFiles/delete/' + fileId).then(res => {
          // 如果返回的状态码为200，表示操作成功
          if (res.code === '200') {
            // 弹出操作成功的提示信息
            this.$message.success('操作成功')
            // 重新加载数据
            this.load()
          } else {
            // 否则弹出错误信息
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        // 忽略取消删除的操作
      })
    },
    // 当表格选择项发生变化时触发的方法，参数rows是当前选中的所有行数据
    handleSelectionChange(rows) {
      // 使用map方法遍历选中的行数据，将每行数据的fileId字段值组成数组赋值给this.ids
      this.ids = rows.map(v => v.fileId)
    },
    // 批量删除文件的方法
    delBatch() {
      // 如果this.ids数组为空，即没有选中任何数据
      if (!this.ids.length) {
        // 弹出警告信息提示用户选择数据
        this.$message.warning('请选择数据')
        // 终止方法执行
        return
      }
      // 弹出确认框，询问用户是否确定批量删除
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        // 发起DELETE请求到指定URL，以批量删除文件，传入this.ids数组作为要删除的文件ID列表
        this.$request.delete('/diskFiles/delete/batch', {data: this.ids}).then(res => {
          // 如果返回的状态码为200，表示操作成功
          if (res.code === '200') {
            // 弹出操作成功的提示信息
            this.$message.success('操作成功')
            // 重新加载数据
            this.load()
          } else {
            // 否则弹出错误信息
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        // 忽略取消删除的操作
      })
    },
    // 加载数据的方法
    load() {
      // 发起GET请求到指定URL，以获取数据
      this.$request.get('/diskFiles/selectTrash').then(res => {
        // 将返回的数据赋值给this.tableData，如果返回的数据不存在，则赋值为空数组
        this.tableData = res.data || []
      })
    },
    // 鼠标移入行时触发的方法，参数row是移入的行数据
    mouseEnter(row) {
      // 使用Vue的$set方法设置row对象的optShow属性为true，表示显示操作选项
      this.$set(row, 'optShow', false)
    }
  }
}
</script>

<style scoped>

</style>