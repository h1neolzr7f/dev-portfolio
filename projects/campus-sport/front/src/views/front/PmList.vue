<script setup>
  import router from "@/router";
  import request from "@/utils/request";
  import {onMounted, reactive, ref} from "vue";
  import {useUserStore} from "@/stores/user";
  import '@wangeditor/editor/dist/css/style.css' // 引入 css
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

  const userStore = useUserStore()
  const user = userStore.getUser
  const pageNum = ref(1)
  const pageSize = ref(5)
  const total = ref(0)

  
  const content = ref('')
  const viewShow = ref(false)
  const view = (value) => {
    viewShow.value = true
    content.value = value
  }

  //判断用户是否登录
  if(user.id==null){
    router.push('/login')
  }

  const id = router.currentRoute.value.query.id
  let name = router.currentRoute.value.query.name
  const state = reactive({
    tableData:[],
  })

  state.searchKey = ''
  const load = () => {
    if(state.searchKey!=null && state.searchKey!=''){
      name = state.searchKey
    }
    request.get('/pm/findList/'+user.id).then(res => {
      state.tableData = res.data
    })
  }
  load()  // 调用 load方法拿到后台数据

  //轮播图
  request.get('/front/banner/list').then(res => {
    state.rotationList = res.data
    state.rotationList = state.rotationList.filter((item) => item.indexRadio === '否');
  })

  state.userOptions = []
  request.get('/front/user/list').then(res => state.userOptions = res.data)


  //搜索
  state.searchKey = ''
  const search = () =>{
    load()
  }
</script>

<template>
  <div>

      <!-- 轮播图 -->
      <div>
        <el-card>
        <div style="width: 100%">
          <el-carousel :interval="5000" arrow="always" height="200px">
            <el-carousel-item v-for="item in state.rotationList" :key="item">
              <a :href="item.url" target="_blank"><img :src="item.img" alt="" style="width: 100%; height: 100%"></a>
            </el-carousel-item>
          </el-carousel>
        </div>
        </el-card>
      </div>


    <div style="margin-top: 20px;">
      <el-card>
        <div style="padding-bottom: 15px ;border-bottom: 1px solid #ddd; margin-top: 20px;text-align: center;">
          <span style="font-weight: bold; font-size: 24px;">私聊列表</span>
        </div>


        <el-table :data="state.tableData" style="width: 100%;margin-top: 20px;">
                <el-table-column prop="id" label="编号"></el-table-column>
      <el-table-column prop="name" label="用户名"></el-table-column>
            <el-table-column label="操作"><template #default="scope"><el-button type="primary" size="small" @click="router.push('/front/pm?fid=' + scope.row.id)">私聊</el-button></template></el-table-column>
        </el-table>

      </el-card>

    </div>
  </div>
</template>

<style scoped>
  .navbar {
    background-color: #eeeeee;
    padding: 10px;
    justify-content: center;
  }

  .navbar a {
    font-size: 16px;
    color: #333;
    text-decoration: none;
    margin: 0 15px;
    padding: 5px;
    transition: color 0.3s;
  }

  .navbar a:hover {
    color: #ff6700;
  }
</style>