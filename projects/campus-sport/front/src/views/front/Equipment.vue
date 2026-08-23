<script setup>
import router from "@/router";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { reactive, ref } from "vue";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore()
const user = userStore.getUser
const name = ref(router.currentRoute.value.query.name || '')

const state = reactive({
  tableData: [],
  borrowForm: {
    borrowQuantity: 1,
    remark: ''
  },
  currentEquipment: null
})

const pageNum = ref(1)
const pageSize = ref(8)
const total = ref(0)
const dialogFormVisible = ref(false)

const load = () => {
  request.get('/equipment/page', {
    params: {
      name: name.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
  }).then(res => {
    state.tableData = res.data.records
    total.value = res.data.total
  })
}
load()

const search = () => {
  pageNum.value = 1
  load()
}

const openBorrow = (item) => {
  if (!user.id) {
    router.push('/login')
    return
  }
  state.currentEquipment = item
  state.borrowForm = {
    equipmentId: item.id,
    borrowQuantity: 1,
    remark: ''
  }
  dialogFormVisible.value = true
}

const saveBorrow = () => {
  if (!state.borrowForm.borrowQuantity || state.borrowForm.borrowQuantity <= 0) {
    ElMessage.error('借用数量必须大于0')
    return
  }
  request.post('/equipmentBorrow/borrow', state.borrowForm).then(res => {
    if (res.code === '200') {
      ElMessage.success('借用成功')
      dialogFormVisible.value = false
      load()
      router.push('/front/equipment-borrow')
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
        <span style="font-size: 14px;margin-right: 20px;">当前位置：首页 > 体育器材借用</span>
      </div>

      <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; text-align: left;display: flex;">
        <span style="font-weight: bold; font-size: 24px;flex: 3;color: #0099CC;">体育器材借用</span>
        <span style="flex: 1;text-align: right;">
          <el-input style="width: 220px" placeholder="请输入器材名称" v-model="name" clearable></el-input>
          <el-button style="margin-left: 5px;background-color: #0099CC;color: #FFFFFF;" @click="search">搜索</el-button>
        </span>
      </div>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="6" v-for="item in state.tableData" :key="item.id" style="margin-bottom: 20px">
          <el-card shadow="hover">
            <div style="font-weight: bold; font-size: 18px;color:#0099CC;">{{ item.name }}</div>
            <div style="margin-top: 10px;line-height: 28px;text-align:left;">
              <div>类型：{{ item.type || '通用器材' }}</div>
              <div>存放位置：{{ item.location || '体育馆器材室' }}</div>
              <div>总库存：{{ item.totalStock }}</div>
              <div>可借库存：{{ item.availableStock }}</div>
              <div>状态：{{ item.stateRadio }}</div>
              <div>备注：{{ item.remark || '无' }}</div>
            </div>
            <el-button
                type="primary"
                style="width:100%;margin-top: 15px;background-color:#0099CC;"
                :disabled="item.stateRadio !== '正常' || item.availableStock <= 0"
                @click="openBorrow(item)"
            >
              申请借用
            </el-button>
          </el-card>
        </el-col>
      </el-row>

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

    <el-dialog v-model="dialogFormVisible" title="器材借用" width="30%">
      <el-form :model="state.borrowForm" label-width="80px" style="padding: 0 20px" status-icon>
        <el-form-item label="器材">
          <el-input :model-value="state.currentEquipment && state.currentEquipment.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number
              v-model="state.borrowForm.borrowQuantity"
              :min="1"
              :max="state.currentEquipment ? state.currentEquipment.availableStock : 1"
              style="width: 100%"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="state.borrowForm.remark" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="saveBorrow">确定借用</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
