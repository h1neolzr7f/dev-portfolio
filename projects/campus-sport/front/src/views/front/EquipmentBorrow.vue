<script setup>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { reactive } from "vue";
import { useUserStore } from "@/stores/user";
import router from "@/router";

const userStore = useUserStore()
const user = userStore.getUser

if (user.id == null) {
  router.push('/login')
}

const state = reactive({
  tableData: []
})

const load = () => {
  request.get('/equipmentBorrow/my').then(res => {
    state.tableData = res.data || []
  })
}
load()

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
</script>

<template>
  <div>
    <div style="width:85%;margin: 0 auto;margin-bottom: 50px;">
      <div style="padding-bottom: 15px ;margin-top: 20px;text-align: left;">
        <span style="font-size: 14px;margin-right: 20px;">当前位置：首页 > 我的器材借用</span>
      </div>

      <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; text-align: left;display: flex;">
        <span style="font-weight: bold; font-size: 24px;float: left;flex: 3;color: #0099CC;">我的器材借用</span>
      </div>

      <el-table :data="state.tableData" style="width: 100%;margin-top: 20px;" stripe border :header-cell-class-name="'headerBg'">
        <el-table-column prop="id" label="序号" width="80">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="equipmentName" label="器材名称"></el-table-column>
        <el-table-column prop="borrowQuantity" label="数量"></el-table-column>
        <el-table-column prop="borrowTime" label="借用时间"></el-table-column>
        <el-table-column prop="returnTime" label="归还时间"></el-table-column>
        <el-table-column prop="stateRadio" label="状态"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="操作" width="120px">
          <template #default="scope">
            <a href="javascript:void(0)" @click="returnEquipment(scope.row.id)" class="delete-link" v-if="scope.row.stateRadio === '借用中'">归还</a>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
