<script setup>
import request from "@/utils/request";
import { reactive } from "vue";

const state = reactive({
  tableData: []
});

const load = () => {
  request.get("/activitySignup/my").then(res => {
    state.tableData = res.data || [];
  });
};
load();
</script>

<template>
  <div class="front-page">
    <div class="breadcrumb">当前位置：首页 > 个人中心 > 我的活动</div>
    <div class="page-title">我的活动</div>
    <el-table :data="state.tableData" stripe border class="table">
      <el-table-column prop="activityTitle" label="活动名称" min-width="160" />
      <el-table-column prop="activityTime" label="活动时间" min-width="160" />
      <el-table-column prop="applyTime" label="报名时间" min-width="160" />
      <el-table-column prop="stateRadio" label="审核状态" width="120" />
      <el-table-column prop="reviewerName" label="审核人" width="120" />
      <el-table-column prop="reviewTime" label="审核时间" min-width="160" />
      <el-table-column prop="reviewRemark" label="审核意见" min-width="160" />
    </el-table>
  </div>
</template>

<style scoped>
.front-page {
  width: 85%;
  margin: 0 auto 50px;
}
.breadcrumb {
  padding: 20px 0 15px;
  text-align: left;
  font-size: 14px;
}
.page-title {
  padding-bottom: 15px;
  border-bottom: 3px solid #0099cc;
  color: #0099cc;
  font-size: 24px;
  font-weight: 700;
  text-align: left;
}
.table {
  margin-top: 18px;
}
</style>
