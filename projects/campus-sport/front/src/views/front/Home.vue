<script setup>
import {reactive, ref} from "vue";
import request from "@/utils/request";
import router from "@/router"


const name = ref('')
const state = reactive({
    noticeList:[],
        rotationList:[],
})


state.userOptions = []
request.get('/user').then(res => state.userOptions = res.data)

const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const load = () => {

  //请求前面10条的公告
  request.get('/notice/findTop10').then(res => {
    state.noticeList = res.data
  })
}
load()  // 调用 load方法拿到后台数据

//轮播图
request.get('/front/rotation/list').then(res => {
  state.rotationList = res.data
  state.rotationList = state.rotationList.filter((item) => item.indexRadio === '是');
})

//加载首页数据列表
request.get('/front/news/page', {
  params: {
    pageNum: 1,
    pageSize: 8,
  }
}).then(res => {
  state.newsData = res.data.records
})
const truncatedContent = (content) => {
  const maxLength = 100;
  if (content.length > maxLength) {
    return content.substring(0, maxLength) + '...';
  }
  return content;
}

state.recommend = []
request.get('/front/recommend/area').then(res => {
  state.recommend = res.data
})
</script>

<template>
  <div>
    <!-- 轮播图 -->
    <div>
      <div style="width: 100%">
        <el-carousel :interval="5000" arrow="always" height="400px">
          <el-carousel-item v-for="item in state.rotationList" :key="item" v-show="item.indexRadio=='是'">
            <a :href="item.url" target="_blank"><img :src="item.img" alt="" style="width: 100%; height: 100%"></a>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>

      <div style="width:85%;margin: 0 auto;margin-bottom: 50px;">
        <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; margin-top: 20px;text-align: center;">
          <span style="font-weight: bold; font-size: 24px;color: #0099CC;">个性化场地推荐</span>
        </div>
          <div style="margin-top: 20px;">
            <el-row :gutter="10">
              <el-col :span="6" v-for="item in state.recommend" :key="item.id" style="margin-top: 20px;">
                  <div ><img @click="router.push('/front/area?id=' + item.id)" :src="item.img" alt="" style="width: 100%; height: 200px;cursor: pointer"></div>
                  <div><span style="font-weight: bold">{{ item.name}}</span></div>
                  <div style="margin: 5px 0; text-align: right">
                    <el-button type="primary" size="small" @click="router.push('/front/area?id=' + item.id)">查看详情</el-button>
                  </div>
              </el-col>
            </el-row>
          </div>
      </div>



      <div style="width:85%;margin: 0 auto;margin-bottom: 50px;">
          <div style="padding-bottom: 10px ;padding-top: 5px ;border-bottom: 3px solid #0099CC;border-top: 3px solid #0099CC; background-color: #e0e0e0; margin-top: 20px;text-align: center;">
            <span style="font-weight: bold; font-size: 24px;color:#0099CC;">系统公告</span>
          </div>
          <div style="margin-top: 20px;">
            <div class="item" v-for="item in state.newsData" :key="index" >
              <img :src="item.img" style="margin-left: 30px;"/>
              <div class="right-container">
                <div class="top">
                  <div style="cursor: pointer;width:80%; "><a href="javascript:void(0)" class="title" @click="router.push('/front/news-skills?id=' + item.id)">{{ item.name }}</a></div>
                  <!-- （可选）显示价格 -->
                  <div style="color: #fa5741;width:20%; " v-if="item.price">{{ item.price }} 元</div>
                </div>
                <div style="color: #6d6d73;font-weight: bold; font-size: 14px;" v-html="truncatedContent(item.content)"></div>
                <div class="time">发布时间：{{ item.createTime }}</div>
              </div>
            </div>
          </div>
      </div>

  </div>
</template>

<style scoped>
.refresh:hover {
  cursor: pointer;
}
:deep(.el-card__body) {
  padding: 10px !important;
}


/* 标题样式 */
.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;  /* 标题默认颜色 */
  text-decoration: none;  /* 移除下划线 */
  /* 悬停效果 */
  transition: color 0.3s ease;
}

.title:hover {
  color: orangered !important;  /* 鼠标悬停时的颜色 */
}


.item {
  padding: 20px 0;
  display: flex;
  border-radius: 10px;
}

.item img {
  width: 160px;
  height: 140px;
}

.right-container {
  position: relative;
  margin-left: 20px;
  width: 100%;

}

.right-container div {
  margin-bottom: 7px;
}

.top {
  display: flex;
  color: #101d37;
  font-size: 20px;
  font-weight: bold;
}

.time {
  position: absolute;
  left: 0;
  bottom: 0;
  margin-bottom: 0 !important;
  color: rgba(16, 29, 55, .3);
  font-size: 14px;
}
</style>
