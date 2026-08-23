<script setup>
import { nextTick, reactive, ref } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import config from "../../config";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const token = userStore.getBearerToken;
const auths = userStore.getAuths;

const name = ref("");
const stateRadio = ref("");
const pageNum = ref(1);
const pageSize = ref(8);
const total = ref(0);
const dialogFormVisible = ref(false);
const signupDialogVisible = ref(false);
const ruleFormRef = ref();
const multipleSelection = ref([]);

const state = reactive({
  tableData: [],
  signupData: [],
  form: {},
  currentActivity: null
});

const rules = reactive({
  title: [{ required: true, message: "请输入活动名称", trigger: "blur" }],
  activityTime: [{ required: true, message: "请选择活动时间", trigger: "blur" }],
  location: [{ required: true, message: "请输入活动地点", trigger: "blur" }]
});

const load = () => {
  request.get("/activity/page", {
    params: {
      name: name.value,
      stateRadio: stateRadio.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
  }).then(res => {
    state.tableData = res.data.records;
    total.value = res.data.total;
  });
};
load();

const reset = () => {
  name.value = "";
  stateRadio.value = "";
  pageNum.value = 1;
  load();
};

const handleAdd = () => {
  dialogFormVisible.value = true;
  nextTick(() => {
    ruleFormRef.value && ruleFormRef.value.resetFields();
    state.form = { stateRadio: "已发布", capacity: 30 };
  });
};

const handleEdit = (raw) => {
  dialogFormVisible.value = true;
  nextTick(() => {
    ruleFormRef.value && ruleFormRef.value.resetFields();
    state.form = JSON.parse(JSON.stringify(raw));
  });
};

const save = () => {
  ruleFormRef.value.validate(valid => {
    if (!valid) return;
    request.request({
      url: "/activity",
      method: state.form.id ? "put" : "post",
      data: state.form
    }).then(res => {
      if (res.code === "200") {
        ElMessage.success("保存成功");
        dialogFormVisible.value = false;
        load();
      } else {
        ElMessage.error(res.msg);
      }
    });
  });
};

const del = (id) => {
  request.delete("/activity/" + id).then(res => {
    if (res.code === "200") {
      ElMessage.success("删除成功");
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};

const confirmDelBatch = () => {
  if (!multipleSelection.value.length) {
    ElMessage.warning("请选择数据");
    return;
  }
  request.post("/activity/del/batch", multipleSelection.value.map(item => item.id)).then(res => {
    if (res.code === "200") {
      ElMessage.success("删除成功");
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const publish = (id) => {
  request.put("/activity/publish/" + id).then(res => {
    if (res.code === "200") {
      ElMessage.success("已发布");
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const closeActivity = (id) => {
  request.put("/activity/close/" + id).then(res => {
    if (res.code === "200") {
      ElMessage.success("已下架");
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const openSignups = (row) => {
  state.currentActivity = row;
  signupDialogVisible.value = true;
  loadSignups();
};

const loadSignups = () => {
  request.get("/activitySignup/page", {
    params: {
      activityId: state.currentActivity.id,
      pageNum: 1,
      pageSize: 200
    }
  }).then(res => {
    state.signupData = res.data.records;
  });
};

const approve = (id) => {
  request.put("/activitySignup/approve/" + id).then(res => {
    if (res.code === "200") {
      ElMessage.success("审核通过");
      loadSignups();
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const reject = (id) => {
  request.put("/activitySignup/reject/" + id, { reviewRemark: "不符合本次活动安排" }).then(res => {
    if (res.code === "200") {
      ElMessage.success("已拒绝");
      loadSignups();
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const exportData = () => {
  window.open(`http://${config.serverUrl}/activity/export`);
};

const handleImportSuccess = () => {
  load();
  ElMessage.success("导入成功");
};
</script>

<template>
  <div class="page-wrap">
    <div class="toolbar">
      <el-input v-model="name" placeholder="请输入活动名称" class="w220" clearable />
      <el-select v-model="stateRadio" placeholder="活动状态" class="w160" clearable>
        <el-option label="已发布" value="已发布" />
        <el-option label="草稿" value="草稿" />
        <el-option label="已下架" value="已下架" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="warning" @click="reset">清空</el-button>
    </div>

    <div class="actions">
      <el-button type="success" @click="handleAdd" v-if="auths.includes('activity.add')">新增活动</el-button>
      <el-upload
          v-if="auths.includes('activity.import')"
          :show-file-list="false"
          :action="`http://${config.serverUrl}/activity/import`"
          :on-success="handleImportSuccess"
          :headers="{ Authorization: token }"
      >
        <el-button type="primary">导入</el-button>
      </el-upload>
      <el-button type="primary" @click="exportData" v-if="auths.includes('activity.export')">导出</el-button>
      <el-popconfirm title="您确定删除选中的活动吗？" @confirm="confirmDelBatch" v-if="auths.includes('activity.deleteBatch')">
        <template #reference>
          <el-button type="danger">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>

    <el-table :data="state.tableData" stripe border @selection-change="handleSelectionChange" header-cell-class-name="headerBg">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="活动名称" min-width="160" />
      <el-table-column prop="location" label="地点" min-width="130" />
      <el-table-column prop="activityTime" label="活动时间" min-width="160" />
      <el-table-column prop="deadline" label="报名截止" min-width="160" />
      <el-table-column prop="capacity" label="名额" width="80" />
      <el-table-column label="报名" width="140">
        <template #default="scope">
          {{ scope.row.approvedCount || 0 }}/{{ scope.row.capacity || 0 }}
          <span class="muted">待审 {{ scope.row.pendingCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="stateRadio" label="状态" width="90" />
      <el-table-column prop="publisherName" label="发布人" width="120" />
      <el-table-column label="操作" width="360" fixed="right">
        <template #default="scope">
          <el-button type="primary" @click="handleEdit(scope.row)" v-if="auths.includes('activity.edit')">编辑</el-button>
          <el-button type="success" @click="publish(scope.row.id)" v-if="auths.includes('activity.publish') && scope.row.stateRadio !== '已发布'">发布</el-button>
          <el-button type="warning" @click="closeActivity(scope.row.id)" v-if="auths.includes('activity.publish') && scope.row.stateRadio === '已发布'">下架</el-button>
          <el-button type="info" @click="openSignups(scope.row)" v-if="auths.includes('activity.review')">报名审核</el-button>
          <el-popconfirm title="您确定删除吗？" @confirm="del(scope.row.id)" v-if="auths.includes('activity.delete')">
            <template #reference>
              <el-button type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
          @current-change="load"
          @size-change="load"
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          background
          :page-sizes="[8, 12, 16]"
          layout="prev, pager, next, total"
          :total="total"
      />
    </div>

    <el-dialog v-model="dialogFormVisible" title="活动信息" width="48%">
      <el-form ref="ruleFormRef" :model="state.form" :rules="rules" label-width="100px" status-icon>
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="state.form.title" />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="state.form.location" />
        </el-form-item>
        <el-form-item label="活动时间" prop="activityTime">
          <el-date-picker v-model="state.form.activityTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报名截止" prop="deadline">
          <el-date-picker v-model="state.form.deadline" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="人数上限">
          <el-input-number v-model="state.form.capacity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="state.form.stateRadio">
            <el-radio label="草稿">草稿</el-radio>
            <el-radio label="已发布">已发布</el-radio>
            <el-radio label="已下架">已下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input v-model="state.form.content" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="state.form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="signupDialogVisible" :title="`报名审核 - ${state.currentActivity ? state.currentActivity.title : ''}`" width="70%">
      <el-table :data="state.signupData" stripe border>
        <el-table-column prop="userName" label="学生" width="120" />
        <el-table-column prop="applyTime" label="报名时间" width="170" />
        <el-table-column prop="remark" label="申请备注" />
        <el-table-column prop="stateRadio" label="状态" width="100" />
        <el-table-column prop="reviewerName" label="审核人" width="120" />
        <el-table-column prop="reviewTime" label="审核时间" width="170" />
        <el-table-column label="操作" width="170">
          <template #default="scope">
            <el-button size="small" type="success" @click="approve(scope.row.id)" :disabled="scope.row.stateRadio === '已通过'">通过</el-button>
            <el-button size="small" type="danger" @click="reject(scope.row.id)" :disabled="scope.row.stateRadio === '已拒绝'">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-wrap {
  background: #ffffff;
  border-radius: 8px;
  margin-top: 20px;
  padding: 14px;
}
.toolbar,
.actions {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 12px;
}
.w220 {
  width: 220px;
}
.w160 {
  width: 160px;
}
.muted {
  margin-left: 6px;
  color: #8492a6;
  font-size: 12px;
}
.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
