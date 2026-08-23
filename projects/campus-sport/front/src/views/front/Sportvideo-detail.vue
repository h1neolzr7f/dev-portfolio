<script setup>
  import router from "@/router";
  import request from "@/utils/request";
  import {ElMessage} from "element-plus";
  import {onMounted, reactive, ref} from "vue";
  import {useUserStore} from "@/stores/user";
  import '@wangeditor/editor/dist/css/style.css' // 引入 css
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
  import config from "../../../config";

  const userStore = useUserStore()
  const user = userStore.getUser


  const id = router.currentRoute.value.query.id // 参数id
  const state = reactive({
    data: {},
  })

  const load = () => {
    request.get('/front/sportvideo/' + id).then(res => {
      state.data = res.data
      loadVedio(state.data.vedio)
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


  state.activeTab = 'content'




  //视频设置
  state.activeVedioTab = 'vedio'
  const getUrl = (name) => {
    return new URL(`../../../../files/${name}`, import.meta.url).href
  }
  state.vedioUrl = ''
  state.vedioOptions = ''
  const loadVedio = (value) => {
    if(value==null || value==''){
      ElMessage.error('没有发现视频，请上传视频')
      return
    }
    state.vedioUrl = value.substring(value.lastIndexOf("/") + 1);
    state.vedioOptions = {
      width: '800px', //播放器高度
      height: '450px', //播放器高度
      color: "#409eff", //主题色
      muted: false, //静音
      webFullScreen: false,
      speedRate: ["0.75", "1.0", "1.25", "1.5", "2.0"], //播放倍速
      autoPlay: false, //自动播放
      loop: false, //循环播放
      mirror: false, //镜像画面
      ligthOff: false,  //关灯模式
      volume: 0.3, //默认音量大小
      control: true, //是否显示控制器
      title: '', //视频名称
      src: getUrl(state.vedioUrl), //视频源
      poster: '', //封面

    }
  }

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




        <div class="mc detail-container">
          <div style="padding-bottom: 15px ;text-align: left;">
            <span style="font-size: 14px;margin-right: 20px;">当前位置：首页 > {{ state.data.name }}</span>
          </div>

          <div class="detail-content">
            <div class="detail-left">
              <div class="big-name"> {{ state.data.name }}</div>
              <div class="detail-info-list">

                <div>
                  <span>发布时间：</span>{{ state.data.createTime }}
                </div>

              </div>
            </div>

            <div class="detail-right">
              <div style="display: flex; justify-content: center; align-items: center; height: 100%;">
                <vue3VideoPlay class="vue-video" v-bind="state.vedioOptions" />
              </div>
            </div>
          </div>

          <el-tabs v-model="state.activeTab" style="margin-top: 20px;min-height: 500px;">
            <el-tab-pane label="简介详情" name="content">
              <span class="markdown-body" v-html="state.data.content"></span>
            </el-tab-pane>
          </el-tabs>

        </div>

      </div>
    </template>

    <style scoped>
      .detail-container {
        padding: 20px 0;
        width:85%;margin: 0 auto;margin-bottom: 50px;
      }

      .big-name {
        display: flex;
        align-items: center;
        margin-bottom: 5px;
        font-size: 28px;
        font-weight: 700;
        line-height: 40px;
        color: #101d37;
      }


      .detail-content {
        display: flex;
      }

      .detail-left {
        width: 35%;
        margin-right: 2%;

      }

      .detail-right {
        width: 65%;
        padding: 17px 30px 0;
        box-sizing: border-box;
        height: 420px;
      }

      .detail-tag div {
        background-color: rgba(132, 154, 174, .1);
        border-radius: 2px;
        display: inline-block;
        padding: 2px 10px;
        height: 23px;
        line-height: 23px;
        text-align: center;
        font-size: 12px;
        color: #849aae;
        margin-right: 5px;
        margin-bottom: 5px;
      }

      .detail-info-list {
        padding: 10px 0;
        border-bottom: 1px solid #e4e6f0;
      }

      .detail-info-list div {
        margin-bottom: 5px;
        font-size: 14px;
      }

      .detail-info-list div span {
        color: #9399a5;

      }

      .el-carousel__item h3 {
        color: #475669;
        font-size: 14px;
        opacity: 0.75;
        line-height: 150px;
        margin: 0;
      }

      .detail-btn {
        display: flex;

      }

      .detail-btn div {
        width: 160px;
        height: 50px;
        line-height: 14px;
        padding: 18px 0;
        font-size: 16px;
        box-sizing: border-box;
        background: #000000;
        color: #fff;
        text-align: center;
        margin-right: 15px;
        cursor: pointer;
        border-radius: 20px;
      }


      /* el-tabs */
      ::v-deep .el-tabs__nav {
        margin: 0 20px;
        /* 使用rpx没有效果 */
      }
      ::v-deep .el-tabs--top .el-tabs__item.is-top:nth-child(2) {
        padding-left: 20px;
      }
      ::v-deep .el-tabs--top .el-tabs__item.is-top:last-child {
        padding-right: 20px;
      }
      /*悬浮样式*/
      ::v-deep .el-tabs__item:hover {
        color: #e1251b;
      }
      /*选中样式*/
      ::v-deep .el-tabs__item.is-active {
        color: #fff;
        font-weight: bold;
        background-color: #000000;
      }
      /*隐藏tab下面的一横*/
      ::v-deep .el-tabs__active-bar {
        display: none;
      }

    </style>
