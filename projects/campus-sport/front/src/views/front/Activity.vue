<script setup>
import router from "@/router";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { reactive, ref } from "vue";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const user = userStore.getUser;
const name = ref("");
const pageNum = ref(1);
const pageSize = ref(8);
const total = ref(0);
const dialogFormVisible = ref(false);

const state = reactive({
  tableData: [],
  currentActivity: null,
  form: { remark: "" }
});

const load = () => {
  request.get("/activity/published", {
    params: {
      name: name.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
  }).then(res => {
    state.tableData = res.data.records;
    total.value = res.data.total;
  });
};
load();

const search = () => {
  pageNum.value = 1;
  load();
};

const openApply = (item) => {
  if (!user.id) {
    router.push("/login");
    return;
  }
  state.currentActivity = item;
  state.form = { activityId: item.id, remark: "" };
  dialogFormVisible.value = true;
};

const saveApply = () => {
  request.post("/activitySignup/apply", state.form).then(res => {
    if (res.code === "200") {
      ElMessage.success("报名申请已提交，请等待审核");
      dialogFormVisible.value = false;
      router.push("/front/activity-signup");
    } else {
      ElMessage.error(res.msg);
    }
  });
};
</script>

<template>
  <div class="front-page">
    <div class="breadcrumb">当前位置：首页 > 活动预约</div>
    <div class="page-title">
      <span>活动预约</span>
      <div>
        <el-input v-model="name" placeholder="请输入活动名称" clearable class="search-input" />
        <el-button class="search-button" @click="search">搜索</el-button>
      </div>
    </div>

    <el-row :gutter="18" class="activity-grid">
      <el-col :span="6" v-for="item in state.tableData" :key="item.id">
        <div class="activity-card">
          <div class="activity-title">{{ item.title }}</div>
          <div class="line">地点：{{ item.location || "待通知" }}</div>
          <div class="line">时间：{{ item.activityTime || "待通知" }}</div>
          <div class="line">截止：{{ item.deadline || "不限" }}</div>
          <div class="line">名额：{{ item.approvedCount || 0 }}/{{ item.capacity || 0 }}</div>
          <div class="content">{{ item.content || "暂无活动说明" }}</div>
          <el-button
              type="primary"
              class="apply-button"
              :disabled="item.capacity > 0 && item.approvedCount >= item.capacity"
              @click="openApply(item)"
          >
            预约参与
          </el-button>
        </div>
      </el-col>
    </el-row>

    <div class="page">
      <el-pagination
          @current-change="load"
          @size-change="load"
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          background
          :page-sizes="[4, 8, 12, 16]"
          layout="prev, pager, next, total"
          :total="total"
      />
    </div>

    <el-dialog v-model="dialogFormVisible" title="活动报名" width="34%">
      <el-form :model="state.form" label-width="80px">
        <el-form-item label="活动">
          <el-input :model-value="state.currentActivity && state.currentActivity.title" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="state.form.remark" type="textarea" :rows="4" placeholder="可填写特长、班级或参与说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveApply">提交报名</el-button>
      </template>
    </el-dialog>
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 3px solid #0099cc;
  color: #0099cc;
  font-size: 24px;
  font-weight: 700;
}
.search-input {
  width: 220px;
}
.search-button,
.apply-button {
  margin-left: 6px;
  background-color: #0099cc;
  color: #ffffff;
}
.activity-grid {
  margin-top: 20px;
}
.activity-card {
  min-height: 280px;
  border: 1px solid #e6edf3;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 18px;
  text-align: left;
  background: #ffffff;
}
.activity-title {
  color: #0099cc;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 10px;
}
.line {
  line-height: 28px;
  color: #4f5d6b;
}
.content {
  min-height: 70px;
  margin-top: 8px;
  color: #6b7785;
  line-height: 24px;
}
.apply-button {
  width: 100%;
  margin-top: 14px;
}
.page {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
