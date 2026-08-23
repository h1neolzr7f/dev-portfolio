<script setup>
import { nextTick, reactive, ref } from "vue";
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import config from "../../config";
import {useUserStore} from "@/stores/user";
import { onBeforeUnmount, shallowRef} from "vue" 
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'


const name = ref('')
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)

const userStore = useUserStore()
const token = userStore.getBearerToken
const auths =  userStore.getAuths
const user = userStore.getUser

const state = reactive({
  tableData: [],
  form: {}
})

const valueHtml = ref('')  // 富文本内容

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      fieldName: 'file',
      server: `http://${config.serverUrl}/file/uploadImg`,
      headers: {
        Authorization: token
      }
    }
  }
}
const content = ref('')
const viewShow = ref(false)
const view = (value) => {
  viewShow.value = true
  content.value = value
}
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
state.userOptions = []
request.get('/user').then(res => state.userOptions = res.data)
state.areaOptions = []
request.get('/area').then(res => state.areaOptions = res.data)
state.puserOptions = []
request.get('/puser').then(res => state.puserOptions = res.data)
state.ordersOptions = []
request.get('/orders').then(res => state.ordersOptions = res.data)


const multipleSelection = ref([])

// 批量删除
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const confirmDelBatch = () => {
  if (!multipleSelection.value || !multipleSelection.value.length) {
    ElMessage.warning("请选择数据")
    return
  }
  const idArr = multipleSelection.value.map(v => v.id)
  request.post('/comments/del/batch', idArr).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      load()  // 刷新表格数据
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const load = () => {
  request.get('/comments/page', {
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
load()  // 调用 load方法拿到后台数据

const reset = () => {
  name.value = ''
  load()
}

const dialogFormVisible = ref(false)

const rules = reactive({
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' },
  ]
})
const ruleFormRef = ref()

// 新增
const handleAdd = () => {
  dialogFormVisible.value = true
  nextTick(() => {
    ruleFormRef.value.resetFields()
    state.form = {}
    valueHtml.value = ''  // 富文本
  })
}

// 保存
const save = () => {
  ruleFormRef.value.validate(valid => {   // valid就是校验的结果
    if (valid) {
      state.form.content = valueHtml.value  // 富文本保存内容
      request.request({
        url: '/comments',
        method: state.form.id ? 'put' : 'post',
        data: state.form
      }).then(res => {
        if (res.code === '200') {
          ElMessage.success('保存成功')
          dialogFormVisible.value = false
          load()  // 刷新表格数据
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

// 编辑
const handleEdit = (raw) => {
  dialogFormVisible.value = true
  nextTick(() => {
    ruleFormRef.value.resetFields()
    state.form = JSON.parse(JSON.stringify(raw))
    valueHtml.value = raw.content  // 富文本
  })
}

// 删除
const del = (id) => {
  request.delete('/comments/' + id).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      load()  // 刷新表格数据
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 导出接口
const exportData = () => {
  window.open(`http://${config.serverUrl}/comments/export`)
}


const handleImportSuccess = () => {
  // 刷新表格
  load()
  ElMessage.success("导入成功")
}

const handleFileUploadSuccess = (res) => {
  state.form.file = res.data
  ElMessage.success('上传成功')
}
const handleImgUploadSuccess = (res) => {
  state.form.img = res.data
  ElMessage.success('上传成功')
}
</script>

<template>
  <div style="background-color: #ffffff;padding: 10px;border-radius: 10px;margin-top: 20px;">
    <div>
      <el-input v-model="name" placeholder="请输入名称" class="w300" />
      <el-button type="primary" class="ml5" @click="load">
        <el-icon style="vertical-align: middle">
          <Search />
        </el-icon>  <span style="vertical-align: middle"> 查询 </span>
      </el-button>
      <el-button type="warning" class="ml5" @click="reset">
        <el-icon style="vertical-align: middle">
          <RefreshLeft />
        </el-icon>  <span style="vertical-align: middle"> 清空 </span>
      </el-button>

    </div>

    <div style="margin: 10px 0">
      <el-button type="success" @click="handleAdd" v-if="auths.includes('comments.add')">
        <el-icon style="vertical-align: middle">
          <Plus />
        </el-icon>  <span style="vertical-align: middle"> 添加 </span>
      </el-button>
      <el-upload
          v-if="auths.includes('comments.import')"
          class="ml5"
          :show-file-list="false"
          style="display: inline-block; position: relative; top: 3px"
          :action='`http://${config.serverUrl}/comments/import`'
          :on-success="handleImportSuccess"
          :headers="{ Authorization: token}"
      >
        <el-button type="primary">
          <el-icon style="vertical-align: middle">
            <Bottom />
          </el-icon>  <span style="vertical-align: middle"> 导入 </span>
        </el-button>
      </el-upload>
      <el-button type="primary" @click="exportData" class="ml5" v-if="auths.includes('comments.export')">
        <el-icon style="vertical-align: middle">
          <Top />
        </el-icon>  <span style="vertical-align: middle"> 导出 </span>
      </el-button>
      <el-popconfirm title="您确定删除吗？" @confirm="confirmDelBatch" v-if="auths.includes('comments.deleteBatch')">
        <template #reference>
          <el-button type="danger" style="margin-left: 5px">
            <el-icon style="vertical-align: middle">
              <Remove />
            </el-icon>  <span style="vertical-align: middle"> 批量删除 </span>
          </el-button>
        </template>
      </el-popconfirm>
    </div>

    <div style="margin: 10px 0">
      <el-table :data="state.tableData" stripe border  @selection-change="handleSelectionChange" :header-cell-class-name="'headerBg'">
        <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="评论编号"></el-table-column>
      <el-table-column label="评论内容"><template #default="scope"><el-button @click="view(scope.row.content)">查看</el-button></template></el-table-column>
      <el-table-column label="用户"><template #default="scope"><span v-if="scope.row.userId">{{ state.userOptions.find(v => v.id === scope.row.userId) ? state.userOptions.find(v => v.id === scope.row.userId).name : '' }}</span></template></el-table-column>
      <el-table-column label="场地编号"><template #default="scope"><span v-if="scope.row.areaId">{{ state.areaOptions.find(v => v.id === scope.row.areaId) ? state.areaOptions.find(v => v.id === scope.row.areaId).name : '' }}</span></template></el-table-column>
      <el-table-column prop="pid" label="父评论ID"></el-table-column>
      <el-table-column label="父级用户ID"><template #default="scope"><span v-if="scope.row.puserId">{{ state.userOptions.find(v => v.id === scope.row.puserId) ? state.userOptions.find(v => v.id === scope.row.puserId).name : '' }}</span></template></el-table-column>
      <el-table-column prop="score" label="评论星级"></el-table-column>
      <el-table-column label="订单编号"><template #default="scope"><span v-if="scope.row.ordersId">{{ state.ordersOptions.find(v => v.id === scope.row.ordersId) ? state.ordersOptions.find(v => v.id === scope.row.ordersId).name : '' }}</span></template></el-table-column>

        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)" v-if="auths.includes('comments.edit')">修改<el-icon style="vertical-align: middle"><Edit /></el-icon></el-button>
            <el-popconfirm title="您确定删除吗？" @confirm="del(scope.row.id)" v-if="auths.includes('comments.delete')">
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

    <el-dialog v-model="dialogFormVisible" title="体育场评价信息" width="40%">
      <el-form ref="ruleFormRef" :rules="rules" :model="state.form" label-width="80px" style="padding: 0 20px" status-icon>
        <el-form-item label-width="100" prop="content" label="评论内容">
          <div style="border: 1px solid #ccc">
    <Toolbar
        style="border-bottom: 1px solid #ccc"
        :editor="editorRef"
        :mode="'simple'"
    />
    <Editor
        style="height: 300px; overflow-y: hidden;"
        v-model="valueHtml"
        :defaultConfig="editorConfig"
        :mode="'simple'"
        @onCreated="handleCreated"
    />
  </div>        </el-form-item>
        <el-form-item label-width="100" prop="areaId" label="场地编号">
          <el-select clearable v-model="state.form.areaId" placeholder="请选择"  style="width: 100%">
            <el-option v-for="item in state.areaOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label-width="100" prop="pid" label="父评论ID">
          <el-input v-model="state.form.pid" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label-width="100" prop="score" label="评论星级">
          <el-input v-model="state.form.score" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label-width="100" prop="ordersId" label="订单编号">
          <el-select clearable v-model="state.form.ordersId" placeholder="请选择"  style="width: 100%">
            <el-option v-for="item in state.ordersOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="save">
          保存
        </el-button>
      </span>
      </template>
    </el-dialog>

    <el-dialog v-model="viewShow" title="预览" width="40%">
      <div  id="editor-content-view" class="editor-content-view" v-html="content" style="padding: 0 20px"></div>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="viewShow = false">关闭</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>
<style>
.page {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 13px;
  color: #656565;
  margin-top: 20px;
  padding-bottom: 20px;
}

.page > div.homePage, .page > div.lastPage {
  height: 34px;
  line-height: 34px;
  width: 60px;
  text-align: center;
  border: 1px solid #EAEAEA;
  box-sizing: border-box;
  cursor: pointer;
}

.page > div.homePage {
  border-radius: 2px 0 0 2px;
}

.page > div.lastPage {
  border-radius: 0 2px 2px 0;
}

.el-pagination {
  text-align: center;
  padding: 0;
}

.el-pagination > button {
  padding: 0 !important;
  height: 34px !important;
}

.el-pagination > button.btn-prev {
  border-right: none !important;
}

.el-pagination span {
  color: #656565;
  width: 50px;
  border: 1px solid #EAEAEA;
  height: 34px !important;
  line-height: 34px !important;
  box-sizing: border-box;
}

.el-icon.more.btn-quicknext.el-icon-more {
  border-right: none;
}

.el-icon.more.btn-quickprev.el-icon-more {
  border-left: none;
}


.el-dialog {
  background-color: #fff; /* 设置对话框的背景颜色 */
  border: 1px solid #ccc; /* 设置对话框的边框颜色 */
  border-radius: 4px; /* 设置对话框的边框圆角 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15); /* 设置对话框的阴影效果 */
  padding: 20px; /* 设置对话框的内边距 */
  box-sizing: border-box; /* 防止内边距和边框超出容器 */
}

.el-dialog__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #0099CC;
  border-bottom: 1px solid #ccc;
}

.el-dialog__header > * {
  margin-right: 10px;
  color: #ffffff;
}

.el-dialog__title {
  font-size: 18px;
  font-weight: bold;
}

.el-dialog__body {
  padding: 20px;
}

.el-dialog__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-top: 1px solid #ccc;
}

.el-dialog__footer > * {
  margin-left: 10px;
}

.headerBg {
  background: #0099CC!important;
  color: #ffffff!important;
}

.map_search_result {
  position: absolute;
  top: calc(100% - 40vh);
  right: 20px;
  z-index: 9999;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
}

.map_search_result ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.map_search_result li {
  padding: 10px;
  cursor: pointer;
}

.map_search_result li:hover {
  background-color: #f5f5f5;
}
</style>