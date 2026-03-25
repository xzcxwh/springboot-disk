<template>
  <div>
    <!-- 卡片容器，设置内边距 -->
    <div class="card" style="padding: 15px">
      <!-- 欢迎信息，使用可选链操作符?.来安全访问user对象的name属性 -->
      您好，{{ user?.name }}！欢迎使用本系统
    </div>

    <!-- 使用Flex布局，设置列间隙和上下外边距 -->
    <div style="display: flex; grid-gap: 10px; margin: 10px 0">
      <!-- 左侧卡片，宽度占50% -->
      <div style="width: 50%" class="card">
        <!-- 下边距设置 -->
        <div style="margin-bottom: 20px">
          <!-- 下拉选择框，用于选择天数，并绑定到days变量上，选择改变时触发loadLine方法 -->
          <el-select style="width: 150px" v-model="days" @change="loadLine">
            <!-- 下拉选项，近一周，绑定值为7 -->
            <el-option :value="7" label="近一周"></el-option>
            <!-- 下拉选项，近2周，绑定值为14 -->
            <el-option :value="14" label="近2周"></el-option>
            <!-- 下拉选项，近一个月，绑定值为30 -->
            <el-option :value="30" label="近一个月"></el-option>
          </el-select>
        </div>
        <!-- 容器，用于展示折线图，设置宽度和高度 -->
        <div style="width: 100%; height: 400px" id="line"></div>
      </div>

      <!-- 右侧卡片，宽度占50% -->
      <div style="width: 50%;" class="card">
        <!-- 标题，设置下边距、字体大小和加粗 -->
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">公告列表</div>
        <!-- 公告列表容器 -->
        <div>
          <!-- 时间线组件，反向展示，通过slot指定时间戳的展示位置 -->
          <el-timeline reverse slot="reference">
            <!-- 时间线项，使用v-for遍历notices数组，绑定key和timestamp，显示公告的标题和内容 -->
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <!-- 弹出框组件，鼠标悬停时触发，显示公告内容 -->
              <el-popover placement="right" width="200" trigger="hover" :content="item.content">
                <!-- 弹出框引用的元素，显示公告的标题 -->
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts' // 导入echarts图表库

export default {
  name: 'Home', // 定义组件名称为Home
  data() {
    return {
      // 从localStorage中获取用户信息，若不存在则返回一个空对象
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      // 公告列表
      notices: [],
      // 默认选择的时间范围为7天
      days: 7,
      // 折线图的配置项
      lineOption: {
        title: {
          // 折线图标题
          text: '文件上传趋势图',
          // 标题居中对齐
          left: 'center'
        },
        tooltip: {
          // 提示框组件，设置触发类型为坐标轴触发
          trigger: 'axis'
        },
        legend: {
          // 图例组件，设置位置为左侧
          left: 'left'
        },
        xAxis: {
          // x轴配置
          type: 'category',
          // x轴数据为空数组，等待后续填充
          data: []
        },
        yAxis: {
          // y轴配置
          type: 'value',
          // 注意：这里的data是不必要的，y轴通常不需要定义data属性
          data: [] // 此处data应删除
        },
        series: [
          {
            // 系列列表
            data: [], // 数据为空数组，等待后续填充
            type: 'line', // 图表类型为折线图
            smooth: true // 曲线平滑
          }
        ]
      }
    }
  },
  // 组件创建时调用
  created() {
    // 发送GET请求到/notice/selectAll接口，获取公告列表
    this.$request.get('/notice/selectAll').then(res => {
      // 将返回的数据赋值给公告列表
      this.notices = res.data || []
    })
  },
  // 组件挂载到DOM后调用
  mounted() {
    // 加载折线图数据
    this.loadLine()
  },
  methods: {
    // 加载折线图数据的方法
    loadLine() {
      // 获取折线图容器DOM元素
      // 折线图
      let lineDom = document.getElementById('line')
      // 初始化折线图实例
      let lineChart = echarts.init(lineDom)
      // 发送GET请求到/diskFiles/count接口，根据days参数获取文件上传数量数据
      this.$request.get('/diskFiles/count', { params: { days: this.days } }).then(res => {
        // 将返回的数据中的日期映射到x轴数据
        this.lineOption.xAxis.data = res.data?.map(v => v.date) || []
        // 将返回的数据中的数量映射到折线数据
        this.lineOption.series[0].data = res.data?.map(v => v.count) || []
        // 使用最新的配置项和数据更新折线图
        lineChart.setOption(this.lineOption)
      })
    }
  }
}
</script>