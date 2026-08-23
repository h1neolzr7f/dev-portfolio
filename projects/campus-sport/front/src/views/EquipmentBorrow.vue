<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import config from "../../config";
import { useUserStore } from "@/stores/user";

const name = ref('')
const stateRadio = ref('')
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const userStore = useUserStore()
const auths = userStore.getAuths
const token = userStore.getBearerToken

const state = reactive({
  tableData: []
})

const load = () => {
  request.get('/equipmentBorrow/page', {
    params: {
      name: name.value,
      stateRadio: stateRadio.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
  }).then(res => {
    state.tableData = res.data.records
    total.value = res.data.total
  })
}
load()

const reset = () => {
  name.value = ''
  stateRadio.value = ''
  load()
}

const returnEquipment = (id) => {
  request.put('/equipmentBorrow/return/' + id).then(res => {
    if (res.code === '200') {
      ElMessage.success('归还成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const del = (id) => {
  request.delete('/equipmentBorrow/' + id).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const exportData = () => {
  window.open(`http://${config.serverUrl}/equipmentBorrow/export`)
}

const handleImportSuccess = () => {
  load()
  ElMessage.success("导入成功")
}
</script>

<template>
  <div style="background-color: #ffffff;padding: 10px;border-radius: 10px;margin-top: 20px;">
    <div>
      <el-input v-model="name" placeholder="请输入器材或用户名称" class="w300" />
      <el-select v-model="stateRadio" placeholder="请选择状态" clearable class="ml5" style="width: 160px">
        <el-option label="借用中" value="借用中"></el-option>
        <el-option label="已归还" value="已归还"></el-option>
      </el-select>
      <el-button type="primary" class="ml5" @click="load">
        <el-icon style="vertical-align: middle"><Search /></el-icon>
        <span style="vertical-align: middle"> 查询 </span>
      </el-button>
      <el-button type="warning" class="ml5" @click="reset">
        <el-icon style="vertical-align: middle"><RefreshLeft /></el-icon>
        <span style="vertical-align: middle"> 清空 </span>
      </el-button>
      <el-upload
          v-if="auths.includes('equipmentBorrow.import')"
          class="ml5"
          :show-file-list="false"
          style="display: inline-block; position: relative; top: 3px"
          :action='`http://${config.serverUrl}/equipmentBorrow/import`'
          :on-success="handleImportSuccess"
          :headers="{ Authorization: token}"
      >
        <el-button type="primary">
          <el-icon style="vertical-align: middle"><Bottom /></el-icon>
          <span style="vertical-align: middle"> 导入 </span>
        </el-button>
      </el-upload>
      <el-button type="primary" @click="exportData" class="ml5" v-if="auths.includes('equipmentBorrow.export')">
        <el-icon style="vertical-align: middle"><Top /></el-icon>
        <span style="vertical-align: middle"> 导出 </span>
      </el-button>
    </div>

    <div style="margin: 10px 0">
      <el-table :data="state.tableData" stripe border :header-cell-class-name="'headerBg'">
        <el-table-column prop="id" label="编号" width="80"></el-table-column>
        <el-table-column prop="equipmentName" label="器材名称"></el-table-column>
        <el-table-column prop="userName" label="借用用户"></el-table-column>
        <el-table-column prop="borrowQuantity" label="数量"></el-table-column>
        <el-table-column prop="borrowTime" label="借用时间"></el-table-column>
        <el-table-column prop="returnTime" label="归还时间"></el-table-column>
        <el-table-column prop="stateRadio" label="状态"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button type="primary" @click="returnEquipment(scope.row.id)" v-if="scope.row.stateRadio === '借用中' && auths.includes('equipmentBorrow.return')">归还</el-button>
            <el-popconfirm title="您确定删除吗？" @confirm="del(scope.row.id)" v-if="auths.includes('equipmentBorrow.delete')">
              <template #reference>
                <el-button type="danger">删除<el-icon style="vertical-align: middle"><Remove /></el-icon></el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="page">
      <el-pagination
          prev-text="上一页"
          next-text="下一页"
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
  </div>
</template>
