<template>
  <div style="width: 70%;height: 100%;margin: auto;margin-top: 20px;margin-bottom: 20px;">
    <el-container>
      <el-container>
        <el-header style="border-radius: 10px 10px 0 0">
          <div style="top: 20px;color: white;font-size: 16px ;outline-width: 5px">
            {{fuser.name}}
          </div>

        </el-header>
        <div ref="divRef" class="el-main1" style="overflow-y: scroll;">
          <!-- 循环-->
          <div  v-for="item in messages" :key="item.id">
            <!--左边-->
            <div style="display: flex; margin: 20px 0;" v-if="user.id !== item.toId">
              <el-popover
                  placement="top-start"
                  :width="100"
                  trigger="hover"
              >
                <template #reference>
                  <!--                  左边头像图片:src-->
                  <img :src="fuser.avatar" alt="" style="width: 30px; height: 30px; border-radius: 50%; margin-left: 10px">
                </template>
                <div style="line-height: 20px;">
                  <div style="font-size: 16px">{{ fuser.name }}</div>

                </div>
              </el-popover>
              <!--          <div style="width: 50px; line-height: 30px; margin-left: 5px; color: #888; overflow: hidden; font-size: 14px">{{ item.username }}</div>-->
              <div style="line-height: 30px; background-color: white; padding: 0 10px;margin-left: 10px;
                 width:fit-content; border-radius: 10px">{{ item.content }}</div>
            </div>
            <!--右边-->
            <div style="display: flex; justify-content: flex-end; margin: 20px 0 20px 0;" v-else>
              <div style="line-height: 30px; background-color: #80B9F2; padding: 0 10px;
                margin-right: 10px;
                width:fit-content; border-radius: 10px;">{{ item.content }}</div>

              <el-popover
                  placement="top-start"
                  :width="100"
                  trigger="hover"
              >
                <template #reference>
                  <img :src="tuser.avatar" alt="" style="width: 30px; height: 30px; border-radius: 50%; margin-right: 10px">
                </template>
                <div style="line-height: 20px">
                  <div style="font-size: 16px">{{tuser.name}}</div>
                  <!--                  <div style="font-size: 12px;">{右b}</div>-->
                </div>
              </el-popover>
            </div>

          </div>
          <!--          👆循环-->
        </div>

        <el-footer style="border-radius:  0 0 10px 10px;border-top: 1px solid darkgray">
          <div style="display: flex;margin-left: 20px;top: 10px;width: 95%;height: 80px;">
            <V3Emoji default-select="recent" :recent="true" :options-name="optionsName" :keep="true"  :textArea="true" size="mid" v-model="text" />
            <div style="text-align: right;margin-left: 20px;top: 45px;border-radius: 10px">
              <el-button @click="send" type="primary">
                发送
              </el-button>
            </div>
          </div>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>


<script setup>
import {nextTick, onMounted, reactive, ref , onDeactivated, onUnmounted} from "vue";
import V3Emoji from 'vue3-emoji'
import 'vue3-emoji/dist/style.css'
import {useUserStore} from "@/stores/user";
import request from "@/utils/request";
import { ElRow, ElInput, ElButton } from 'element-plus'
import router from "@/router";

// const tid = router.currentRoute.value.query.tid


const fid = router.currentRoute.value.query.fid
const userStore = useUserStore()
const user = userStore.getUser
//id在这里设置
const tid = user.id
// const fid = 2

//判断用户是否登录
if(user.id==null){
  router.push('/login')
}

const messages = ref([])
const fuser = ref([])
const tuser = ref([])



const text = ref('')  // 聊天输入的内容
const divRef = ref()   // 聊天框的引用

// 页面滚动到最新位置的函数
const scrollBottom = () => {
  nextTick(() => {   // 等到页面元素出来之后再去滚动
    divRef.value.scrollTop = divRef.value.scrollHeight
  })
}

const timer = ref()
// 页面加载完成触发此函数
onMounted(() => {
  setInterval(loadList, 3000); // 每隔 3 秒调用 loadList 方法
  loadList();
})

onDeactivated(()=>{
  clearInterval(timer.value)
})

onUnmounted(()=>{
  clearInterval(timer.value)
})

const loadList = () => {
  request.get(`/pm/${fid}/${tid}`).then(res => {

    messages.value = res.data.pmLists
    fuser.value = res.data.fuser    //别人信息
    tuser.value = res.data.tuser    //我信息

    console.log(tuser)
    scrollBottom()
  })
}

// 发送消息触发滚动条滚动
const send = () => {
  messages.fromId = fid       //别人id
  messages.toId = user.id     //我id
  messages.content = text.value  //向后端传结构体

  request.post('/pm', messages).then(res=>{
    console.log(res)
  })

  text.value = ''  // 清空文本框
}

const optionsName = {
  'Smileys & Emotion': '笑脸&表情',
  'Food & Drink': '食物&饮料',
  'Animals & Nature': '动物&自然',
  'Travel & Places': '旅行&地点',
  'People & Body': '人物&身体',
  Objects: '物品',
  Symbols: '符号',
  Flags: '旗帜',
  Activities: '活动'
}






</script>

<style scoped>

.el-container {
  height: 100%;
}
/*考研聊天室*/
.el-header{
  background-color: #0099CC;
  color: #333;
  text-align: center;
  height: 60px;
  border-bottom: 1px solid darkgrey;
}
/*输入框*/
.el-footer {
  background-color: #f4f5f7;
  color: #333;
  text-align: center;
  height: 120px;
}

.el-aside {
  background-color: #d3dce6;
  color: #333;
  text-align: center;
  height: 600px;
}
/*主聊天框*/
.el-main1 {
  background-color: #f4f5f7;
  color: #333;
  text-align: center;
  height: 420px;

}

.el-menu {
  background-color: #d3dce6;
}

.myinfo{

  text-align: left;
  vertical-align: middle;
  margin-top: 10px;
  margin-left: 10px;
}
.myinfo span{
  text-align: left;
  vertical-align: middle;
}

</style>