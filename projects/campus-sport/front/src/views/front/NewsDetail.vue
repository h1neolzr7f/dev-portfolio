<script setup>
  import router from "@/router";
  import request from "@/utils/request";
  import {onMounted, reactive, ref} from "vue";
  import {ElMessage} from "element-plus";
  import {useUserStore} from "@/stores/user";
  import '@wangeditor/editor/dist/css/style.css' // 引入 css
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

  const userStore = useUserStore()
  const user = userStore.getUser

  
  const id = router.currentRoute.value.query.id // 参数id
  const state = reactive({
    data: {},
  })

  const load = () => {
    request.get('/front/news/' + id).then(res => {
      state.data = res.data
    })

  }
  onMounted(() => {
    load()
  })

  //轮播图
  request.get('/front/rotation/list').then(res => {
    state.rotationList = res.data
    state.rotationList = state.rotationList.filter((item) => item.indexRadio === '否');
  })

  

</script>

<template>
  <div>

      <!-- 轮播图 -->
      <div>
        <div style="width: 100%">
          <el-carousel :interval="5000" arrow="always" height="200px">
            <el-carousel-item v-for="item in state.rotationList" :key="item">
              <a :href="item.url" target="_blank"><img :src="item.img" alt="" style="width: 100%; height: 100%"></a>
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>


    <div style="width:85%;margin: 0 auto;margin-bottom: 50px;">
        <div style="padding-bottom: 15px ;margin-top: 20px;text-align: left;">
          <span style="font-size: 14px;margin-right: 20px;">当前位置：首页 > {{ state.data.name }}</span>
        </div>

        <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; margin-top: 20px;text-align: center;">
          <span style="font-weight: bold; font-size: 24px;">{{ state.data.name }}</span>
        </div>

        <div style="padding-bottom: 15px ;border-bottom: 1px solid #ddd; margin-top: 20px;text-align: center;">
          <span style="font-size: 14px;margin-right: 20px;">创建时间：{{ state.data.createTime }}</span>
        </div>


        <div class="editor-content-view" v-html="state.data.content" style="min-height: 200px;"></div>


    </div>

  </div>
</template>