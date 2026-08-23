<script setup>
import { nextTick, reactive, ref } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import config from "../../config";
import { useUserStore } from "@/stores/user";

const name = ref('')
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const userStore = useUserStore()
const token = userStore.getBearerToken
const auths = userStore.getAuths

const state = reactive({
  tableData: [],
  form: {}
})

const multipleSelection = ref([])
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

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

const reset = () => {
  name.value = ''
  load()
}

const dialogFormVisible = ref(false)
const ruleFormRef = ref()
const rules = reactive({
  name: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
  totalStock: [{ required: true, message: '请输入总库存', trigger: 'blur' }],
  availableStock: [{ required: true, message: '请输入可借库存', trigger: 'blur' }]
})

const handleAdd = () => {
  dialogFormVisible.value = true
  nextTick(() => {
    ruleFormRef.value.resetFields()
    state.form = { totalStock: 0, availableStock: 0, stateRadio: '正常' }
  })
}

const handleEdit = (row) => {
  dialogFormVisible.value = true
  nextTick(() => {
    ruleFormRef.value.resetFields()
    state.form = JSON.parse(JSON.stringify(row))
  })
}

const save = () => {
  ruleFormRef.value.validate(valid => {
    if (!valid) return
    request.request({
      url: '/equipment',
      method: state.form.id ? 'put' : 'post',
      data: state.form
    }).then(res => {
      if (res.code === '200') {
        ElMessage.success('保存成功')
        dialogFormVisible.value = false
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  })
}

const del = (id) => {
  request.delete('/equipment/' + id).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const confirmDelBatch = () => {
  if (!multipleSelection.value || !multipleSelection.value.length) {
    ElMessage.warning("请选择数据")
    return
  }
  request.post('/equipment/del/batch', multipleSelection.value.map(v => v.id)).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const exportData = () => {
  window.open(`http://${config.serverUrl}/equipment/export`)
}

const handleImportSuccess = () => {
  load()
  ElMessage.success("导入成功")
}
</script>

<template>
  <div style="background-color: #ffffff;padding: 10px;border-radius: 10px;margin-top: 20px;">
    <div>
      <el-input v-model="name" placeholder="请输入器材名称" class="w300" />
      <el-button type="primary" class="ml5" @click="load">
        <el-icon style="vertical-align: middle"><Search /></el-icon>
        <span style="vertical-align: middle"> 查询 </span>
      </el-button>
      <el-button type="warning" class="ml5" @click="reset">
        <el-icon style="vertical-align: middle"><RefreshLeft /></el-icon>
        <span style="vertical-align: middle"> 清空 </span>
      </el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="success" @click="handleAdd" v-if="auths.includes('equipment.add')">
        <el-icon style="vertical-align: middle"><Plus /></el-icon>
        <span style="vertical-align: middle"> 添加 </span>
      </el-button>
      <el-upload
          v-if="auths.includes('equipment.import')"
          class="ml5"
          :show-file-list="false"
          style="display: inline-block; position: relative; top: 3px"
          :action='`http://${config.serverUrl}/equipment/import`'
          :on-success="handleImportSuccess"
          :headers="{ Authorization: token}"
      >
        <el-button type="primary">
          <el-icon style="vertical-align: middle"><Bottom /></el-icon>
          <span style="vertical-align: middle"> 导入 </span>
        </el-button>
      </el-upload>
      <el-button type="primary" @click="exportData" class="ml5" v-if="auths.includes('equipment.export')">
        <el-icon style="vertical-align: middle"><Top /></el-icon>
        <span style="vertical-align: middle"> 导出 </span>
      </el-button>
      <el-popconfirm title="您确定删除吗？" @confirm="confirmDelBatch" v-if="auths.includes('equipment.deleteBatch')">
        <template #reference>
          <el-button type="danger" style="margin-left: 5px">
            <el-icon style="vertical-align: middle"><Remove /></el-icon>
            <span style="vertical-align: middle"> 批量删除 </span>
          </el-button>
        </template>
      </el-popconfirm>
    </div>

    <div style="margin: 10px 0">
      <el-table :data="state.tableData" stripe border @selection-change="handleSelectionChange" :header-cell-class-name="'headerBg'">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="编号" width="80"></el-table-column>
        <el-table-column prop="name" label="器材名称"></el-table-column>
        <el-table-column prop="type" label="器材类型"></el-table-column>
        <el-table-column prop="totalStock" label="总库存"></el-table-column>
        <el-table-column prop="availableStock" label="可借库存"></el-table-column>
        <el-table-column prop="location" label="存放位置"></el-table-column>
        <el-table-column prop="stateRadio" label="状态"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)" v-if="auths.includes('equipment.edit')">修改<el-icon style="vertical-align: middle"><Edit /></el-icon></el-button>
            <el-popconfirm title="您确定删除吗？" @confirm="del(scope.row.id)" v-if="auths.includes('equipment.delete')">
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

    <el-dialog v-model="dialogFormVisible" title="体育器材信息" width="40%">
      <el-form ref="ruleFormRef" :rules="rules" :model="state.form" label-width="100px" style="padding: 0 20px" status-icon>
        <el-form-item prop="name" label="器材名称">
          <el-input v-model="state.form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="type" label="器材类型">
          <el-input v-model="state.form.type" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="totalStock" label="总库存">
          <el-input-number v-model="state.form.totalStock" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item prop="availableStock" label="可借库存">
          <el-input-number v-model="state.form.availableStock" :min="0" :max="state.form.totalStock || 0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item prop="location" label="存放位置">
          <el-input v-model="state.form.location" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="stateRadio" label="状态">
          <el-radio-group v-model="state.form.stateRadio">
            <el-radio label="正常">正常</el-radio>
            <el-radio label="维护中">维护中</el-radio>
            <el-radio label="停用">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="remark" label="备注">
          <el-input type="textarea" v-model="state.form.remark" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
