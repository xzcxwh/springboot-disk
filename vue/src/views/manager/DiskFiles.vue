<template>
  <div>
    <!-- 搜索区域 -->
    <div class="search">
      <!-- 输入框，用于输入查询名称 -->
      <el-input placeholder="请输入名称查询" style="width: 200px" v-model="name"></el-input>
      <!-- 查询按钮，点击时调用load方法并传入1作为参数 -->
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <!-- 重置按钮，点击时调用reset方法 -->
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <!-- 操作区域 -->
    <div class="operation">
      <!-- 批量删除按钮，点击时调用delBatch方法 -->
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <!-- 表格区域 -->
    <div class="table">
      <!-- Element UI表格组件，绑定数据为tableData，显示条纹，选择项变化时调用handleSelectionChange方法 -->
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <!-- 选择列 -->
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <!-- 序号列，可排序 -->
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <!-- 文件名称列 -->
        <el-table-column prop="name" label="文件名称"></el-table-column>
        <!-- 是否文件夹列 -->
        <el-table-column prop="folder" label="是否文件夹"></el-table-column>
        <!-- 文件路径列，超出部分显示tooltip -->
        <el-table-column prop="file" label="文件路径" show-overflow-tooltip></el-table-column>
        <!-- 创建人ID列 -->
        <el-table-column prop="userId" label="创建人ID"></el-table-column>
        <!-- 创建人列 -->
        <el-table-column prop="userName" label="创建人"></el-table-column>
        <!-- 文件类型列 -->
        <el-table-column prop="type" label="文件类型"></el-table-column>
        <!-- 文件大小列 -->
        <el-table-column prop="size" label="文件大小"></el-table-column>
        <!-- 创建时间列 -->
        <el-table-column prop="crateTime" label="创建时间"></el-table-column>
        <!-- 修改时间列 -->
        <el-table-column prop="updateTime" label="修改时间"></el-table-column>
        <!-- 是否删除列，使用模板插槽显示不同的文本 -->
        <el-table-column prop="delete" label="是否删除">
          <template v-slot="scope">
            <!-- 如果scope.row.delete为true，则显示"是"，否则显示"否" -->
            <span v-if="scope.row.delete">是</span>
            <span v-else>否</span>
          </template>
        </el-table-column>
        <!-- 操作列，使用模板插槽添加删除按钮 -->
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <!-- 删除按钮，点击时调用del方法并传入scope.row.id作为参数 -->
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination">
        <!-- Element UI分页组件，具有背景色，当当前页改变时调用handleCurrentChange方法，
             绑定当前页为pageNum，可选的每页显示数量为[5, 10, 20]，当前每页显示数量为pageSize，
             布局为总条目数、上一页、页码、下一页，总条目数为total -->
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>



  </div>
</template>

<script>
export default {
  name: "DiskFiles",  // 组件名称
  data() {
    return {
      tableData: [],  // 存储表格数据的数组，初始为空
      pageNum: 1,   // 当前页码，初始为第一页
      pageSize: 10,  // 每页显示的记录数，初始为10条
      total: 0,      // 总记录数，初始为0
      name: null,    // 查询时使用的名称，初始为null
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),  // 从localStorage中获取用户信息，若不存在则使用空对象
      ids: []        // 存储选中行数据ID的数组，初始为空
    }
  },
  created() {
    this.load(1)  // 组件创建时，加载第一页的数据
  },
  methods: {
    // 单个删除方法
    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/diskFiles/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1)  // 删除成功后重新加载第一页数据
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        // 取消删除操作
      })
    },
    // 选中行数据变化时触发的方法
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id)  // 将选中的行数据转换为只包含ID的数组
    },
    // 批量删除方法
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/diskFiles/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1)  // 批量删除成功后重新加载第一页数据
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        // 取消批量删除操作
      })
    },
    // 分页查询方法
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum  // 如果传入了页码，则更新当前页码
      this.$request.get('/diskFiles/selectPage', {  // 发起GET请求，获取分页数据
        params: {
          pageNum: this.pageNum,  // 当前页码
          pageSize: this.pageSize,  // 每页显示的记录数
          name: this.name,  // 查询条件中的名称
        }
      }).then(res => {
        this.tableData = res.data?.list  // 更新表格数据
        this.total = res.data?.total  // 更新总记录数
      })
    },
    // 重置查询条件并加载第一页数据
    reset() {
      this.name = null  // 重置查询名称
      this.load(1)  // 重新加载第一页数据
    },
    // 页码改变时触发的方法
    handleCurrentChange(pageNum) {
      this.load(pageNum)  // 根据新的页码加载数据
    },
  }
}
</script>

<style scoped>

</style>
