<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from "vue";
import request from "@/utils/request";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";

const state = reactive({
  cards: {},
  bookingStatus: {},
  borrowStatus: {},
  activityStatus: {},
  equipmentStock: [],
  areaViews: [],
  recentBookings: [],
  recentBorrows: [],
  recentActivitySignups: [],
  loading: false,
  lastUpdate: ''
})

const bookingChartRef = ref()
const borrowChartRef = ref()
const activityChartRef = ref()
const stockChartRef = ref()
const areaChartRef = ref()
let bookingChart
let borrowChart
let activityChart
let stockChart
let areaChart
let timer

const cardList = [
  { key: 'userCount', label: '系统用户', suffix: '人' },
  { key: 'memberCount', label: '学生用户', suffix: '人' },
  { key: 'areaCount', label: '体育场地', suffix: '个' },
  { key: 'availableSlotCount', label: '可预约时段', suffix: '个' },
  { key: 'bookingCount', label: '场地预约', suffix: '单' },
  { key: 'commentCount', label: '场地评价', suffix: '条' },
  { key: 'avgScore', label: '平均评分', suffix: '分' },
  { key: 'equipmentCount', label: '体育器材', suffix: '类' },
  { key: 'totalEquipmentStock', label: '器材总库存', suffix: '件' },
  { key: 'availableEquipmentStock', label: '可借器材', suffix: '件' },
  { key: 'activeBorrowCount', label: '借用中', suffix: '单' },
  { key: 'activityCount', label: '活动数量', suffix: '个' },
  { key: 'activitySignupCount', label: '活动报名', suffix: '条' },
  { key: 'activityPendingCount', label: '活动待审核', suffix: '条' },
]

const load = () => {
  state.loading = true
  request.get('/dashboard/summary').then(res => {
    if (res.code === '200') {
      Object.assign(state, res.data)
      state.lastUpdate = new Date().toLocaleString()
      nextTick(renderCharts)
    } else {
      ElMessage.error(res.msg)
    }
  }).finally(() => {
    state.loading = false
  })
}

const pieOption = (title, items) => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [{
    name: title,
    type: 'pie',
    radius: ['45%', '70%'],
    data: items.map(([name, value]) => ({ name, value }))
  }]
})

const renderCharts = () => {
  bookingChart = bookingChart || echarts.init(bookingChartRef.value)
  borrowChart = borrowChart || echarts.init(borrowChartRef.value)
  activityChart = activityChart || echarts.init(activityChartRef.value)
  stockChart = stockChart || echarts.init(stockChartRef.value)
  areaChart = areaChart || echarts.init(areaChartRef.value)

  bookingChart.setOption(pieOption('预约状态', Object.entries(state.bookingStatus || {})))
  borrowChart.setOption(pieOption('借用状态', Object.entries(state.borrowStatus || {})))
  activityChart.setOption(pieOption('活动报名审核', Object.entries(state.activityStatus || {})))

  stockChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 40, right: 20, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: state.equipmentStock.map(item => item.name) },
    yAxis: { type: 'value' },
    series: [
      { name: '总库存', type: 'bar', data: state.equipmentStock.map(item => item.totalStock) },
      { name: '可借库存', type: 'bar', data: state.equipmentStock.map(item => item.availableStock) },
      { name: '已借出', type: 'bar', data: state.equipmentStock.map(item => item.borrowedStock) }
    ]
  })

  areaChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: state.areaViews.map(item => item.name).reverse() },
    series: [{
      name: '浏览次数',
      type: 'bar',
      data: state.areaViews.map(item => item.views).reverse()
    }]
  })
}

const resizeCharts = () => {
  bookingChart && bookingChart.resize()
  borrowChart && borrowChart.resize()
  activityChart && activityChart.resize()
  stockChart && stockChart.resize()
  areaChart && areaChart.resize()
}

onMounted(() => {
  load()
  timer = window.setInterval(load, 30000)
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.clearInterval(timer)
  window.removeEventListener('resize', resizeCharts)
  bookingChart && bookingChart.dispose()
  borrowChart && borrowChart.dispose()
  activityChart && activityChart.dispose()
  stockChart && stockChart.dispose()
  areaChart && areaChart.dispose()
})
</script>

<template>
  <div class="dashboard" v-loading="state.loading">
    <div class="dashboard-header">
      <div>
        <div class="title">实时数据报表</div>
        <div class="sub-title">自动每 30 秒刷新一次，展示当前场地预约、器材借用、活动报名和审核现状。</div>
      </div>
      <div class="header-actions">
        <span>最后更新：{{ state.lastUpdate || '加载中' }}</span>
        <el-button type="primary" @click="load">立即刷新</el-button>
      </div>
    </div>

    <div class="metric-grid">
      <div class="metric-card" v-for="item in cardList" :key="item.key">
        <div class="metric-label">{{ item.label }}</div>
        <div class="metric-value">{{ state.cards[item.key] ?? 0 }}<span>{{ item.suffix }}</span></div>
      </div>
    </div>

    <div class="chart-grid">
      <div class="panel">
        <div class="panel-title">场地预约状态分布</div>
        <div ref="bookingChartRef" class="chart"></div>
      </div>
      <div class="panel">
        <div class="panel-title">器材借用状态分布</div>
        <div ref="borrowChartRef" class="chart"></div>
      </div>
      <div class="panel">
        <div class="panel-title">活动报名审核状态</div>
        <div ref="activityChartRef" class="chart"></div>
      </div>
      <div class="panel">
        <div class="panel-title">场地浏览热度</div>
        <div ref="areaChartRef" class="chart"></div>
      </div>
      <div class="panel full">
        <div class="panel-title">器材库存现状</div>
        <div ref="stockChartRef" class="chart"></div>
      </div>
    </div>

    <div class="table-grid">
      <div class="panel">
        <div class="panel-title">最近场地预约</div>
        <el-table :data="state.recentBookings" stripe border>
          <el-table-column prop="name" label="预约号"></el-table-column>
          <el-table-column prop="stateRadio" label="状态" width="90"></el-table-column>
          <el-table-column prop="price" label="定金" width="90"></el-table-column>
          <el-table-column prop="total" label="总金额" width="90"></el-table-column>
          <el-table-column prop="createTime" label="下单时间"></el-table-column>
        </el-table>
      </div>
      <div class="panel">
        <div class="panel-title">最近器材借用</div>
        <el-table :data="state.recentBorrows" stripe border>
          <el-table-column prop="equipmentName" label="器材"></el-table-column>
          <el-table-column prop="userName" label="用户"></el-table-column>
          <el-table-column prop="borrowQuantity" label="数量" width="80"></el-table-column>
          <el-table-column prop="stateRadio" label="状态" width="90"></el-table-column>
          <el-table-column prop="borrowTime" label="借用时间"></el-table-column>
        </el-table>
      </div>
      <div class="panel full">
        <div class="panel-title">最近活动报名</div>
        <el-table :data="state.recentActivitySignups" stripe border>
          <el-table-column prop="activityTitle" label="活动"></el-table-column>
          <el-table-column prop="userName" label="报名学生" width="120"></el-table-column>
          <el-table-column prop="stateRadio" label="审核状态" width="100"></el-table-column>
          <el-table-column prop="reviewerName" label="审核人" width="120"></el-table-column>
          <el-table-column prop="applyTime" label="报名时间"></el-table-column>
          <el-table-column prop="reviewTime" label="审核时间"></el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  background: #ffffff;
  border-radius: 8px;
  padding: 16px;
  min-height: calc(100vh - 100px);
}

.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid #e8eef5;
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #1f5f85;
}

.sub-title {
  margin-top: 6px;
  color: #637381;
  font-size: 13px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #637381;
  white-space: nowrap;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(120px, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.metric-card {
  border: 1px solid #e6edf3;
  border-radius: 8px;
  padding: 14px;
  background: #f8fbfd;
}

.metric-label {
  color: #637381;
  font-size: 13px;
}

.metric-value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 700;
  color: #1f5f85;
}

.metric-value span {
  margin-left: 4px;
  font-size: 13px;
  color: #637381;
  font-weight: 400;
}

.chart-grid,
.table-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.panel {
  border: 1px solid #e6edf3;
  border-radius: 8px;
  padding: 14px;
  background: #ffffff;
  min-width: 0;
}

.full {
  grid-column: 1 / -1;
}

.panel-title {
  color: #1f5f85;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 8px;
}

.chart {
  height: 300px;
  width: 100%;
}

@media (max-width: 1400px) {
  .metric-grid {
    grid-template-columns: repeat(4, minmax(120px, 1fr));
  }
}

@media (max-width: 900px) {
  .dashboard-header,
  .header-actions {
    align-items: flex-start;
    flex-direction: column;
  }

  .metric-grid,
  .chart-grid,
  .table-grid {
    grid-template-columns: 1fr;
  }
}
</style>
