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
  const token = userStore.getBearerToken
  const auths =  userStore.getAuths
  const user = userStore.getUser
  const pageNum = ref(1)
  const pageSize = ref(5)
  const total = ref(0)

    //判断用户是否登录
  if(user.id==null){
      router.push('/login')
  }

  const content = ref('')
  const viewShow = ref(false)
  const view = (value) => {
    viewShow.value = true
    content.value = value
  }

  const id = router.currentRoute.value.query.id
  let name = router.currentRoute.value.query.name
  const state = reactive({
    tableData:[],
    timeSlots:[],
    selectedTimeSlot:null
  })

  const load = () => {
    request.get('/front/prepared/list').then(res => {
      state.tableData = res.data
      state.tableData = state.tableData.filter((item) => item.userId === user.id);

      state.selectedTimeSlot = null
      state.timeSlots = []
      if (state.tableData.length > 0) {
        request.get('/front/area/timeslot?areaId='+state.tableData[0].goodid).then(res => {
          state.timeSlots = res.data || [];
        })
      }
    })
  }
  load()  // 调用 load方法拿到后台数据

  //轮播图
  request.get('/front/rotation/list').then(res => {
      state.rotationList = res.data
      state.rotationList = state.rotationList.filter((item) => item.indexRadio === '否');
  })

  //计算预约信息总价
  const getTotalPrice =() =>{
      const totalPrice = state.tableData.reduce((sum, item) => {
          return sum + item.price*1;
      }, 0);
      return totalPrice.toFixed(2);
  }

  //修改预约信息
  const changeNum = (row) =>{
      request.request({
          url: '/front/prepared/update',
          method: 'post',
          data: {
              id:row.id,
              name:row.name,
              userId:user.id,
              num:row.num,
          }
      }).then(res => {
          if (res.code === '200') {
              ElMessage.success('修改成功')
          } else {
              ElMessage.error(res.msg)
          }
      })
  }

  //删除预约信息
  const deleteCart =(id) =>{
      request.delete('/front/prepared/' + id).then(res => {
          if (res.code === '200') {
              ElMessage.success('操作成功')
              load()  // 刷新表格数据
          } else {
              ElMessage.error(res.msg)
          }
      })
  }

  //批量删除预约信息
  const deleteBatchCart =(ids) =>{
      ids.forEach((e)=>{
          request.delete('/front/prepared/' + e).then(res => {})
      })
  }


  //支付方式
  state.selectedPaytype = 1;
  state.payTypeList = []
  request.get('/front/paytype/list').then(res => {
      state.payTypeList = res.data
  })

  //去结算
  const dialogFormVisible = ref(false)
  state.qrcode = ''
  const toOrder = () =>{
      const flag = validateForm()
      if(!flag)return;

      dialogFormVisible.value = true
      const selectPaytype = state.payTypeList.filter((item) => item.id === state.selectedPaytype);
      if(selectPaytype && selectPaytype.length){
          state.qrcode = selectPaytype[0].img
      }
  }

  const validateForm = () =>{
      if(state.tableData.length==0){
          ElMessage.error('预约信息为空！请先添加')
          return false;
      }
      if(state.payTypeList.length==0){
          ElMessage.error('支付方式为空！请先添加')
          return false;
      }
      if(!state.selectedTimeSlot){
          ElMessage.error('请选择预约时间段')
          return false;
      }
      if(!state.form.name){
          ElMessage.error('请填写姓名')
          return false;
      }
      if(!state.form.phone){
          ElMessage.error('请填写电话')
          return false;
      }

      return true
  }

  state.form = {
      name:'',
      phone:'',
  }
  state.orders = {}
  state.pList = ``
  //保存订单
  const saveOrder = () =>{
      const flag = validateForm()
      if(!flag)return;


      state.orders.name = generateOrderNumber()
      const selectedPaytype = state.payTypeList.filter((item) => item.id === state.selectedPaytype);
      state.pList = ``
      state.pList += `支付方式：${selectedPaytype[0].name}<br/>`;
      state.pList += `用户订单信息：<br/>`;
      state.pList += `<ul>`
      state.pList += '<li>预约时间：'+ state.selectedTimeSlot.time+'</li>'
      state.pList += '<li>姓名：'+state.form.name+'</li>'
      state.pList += '<li>电话：'+state.form.phone+'</li>'
      state.pList += `</ul>`
      state.pList += `场地明细：<br/>`;
      state.pList += `<ul>`
      const goodids = [];
      state.tableData.forEach((e,i)=>{
          goodids.push(e.goodid)
          state.pList += `<li>球场：${e.name}，订金：${e.price}</li>`
          state.orders.rent=e.rent;
      })
      state.orders.goodids = goodids.join(',');
      state.pList += `</ul>`
      state.orders.content = state.pList
      state.orders.stateRadio = '已预约'
      state.orders.userId = user.id
      state.orders.price = getTotalPrice()
      state.orders.timeslotId = state.selectedTimeSlot.id

      request.request({
          url: '/front/books/update',
          method: 'post',
          data: state.orders
      }).then(res => {
          if (res.code === '200') {
              dialogFormVisible.value = false
              //清空预约信息车数据
              const ids = state.tableData.map(obj => obj.id);
              deleteBatchCart(ids)

              router.push('/front/books')
          } else {
              ElMessage.error(res.msg)
          }
      })

  }

  //生成订单号
  function generateOrderNumber() {
      const date = new Date();
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      const orderNumber = `${year}${month}${day}${hours}${minutes}${seconds}`;
      return orderNumber;
  }

  function selectTimeSlot(slot) {
    if (!slot.selected ) {
      resetSelectedTimeSlot();
      slot.selected = true;
      state.selectedTimeSlot = slot;
    }
  }

  function resetSelectedTimeSlot() {
    state.selectedTimeSlot = null;
    state.timeSlots.forEach(slot => {
      slot.selected = false;
    });
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


      <div style="width:85%;margin: 0 auto;margin-bottom: 50px;">
        <div style="padding-bottom: 15px ;margin-top: 20px;text-align: left;">
            <span style="font-size: 14px;margin-right: 20px;">当前位置：首页 > 预约信息</span>
        </div>

        <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; text-align: left;display: flex;">
            <span style="font-weight: bold; font-size: 24px;float: left;flex: 3;color: #0099CC;">预约信息</span>
        </div>

          <el-table :data="state.tableData" style="width: 100%;margin-top: 20px;" stripe border :header-cell-class-name="'headerBg'">
            <el-table-column prop="id" label="序号" width="80px;">
                <template #default="scope">
                    {{ scope.$index + 1 }}
                </template>
                </el-table-column>
                <el-table-column label="球场图片" width="120px;">
                    <template #default="scope" >
                        <img :src="scope.row.img" alt="球场图片" style="width: 100px; height: 80px;">
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="球场" width="320px;"></el-table-column>
                <el-table-column label="小计">
                    <template #default="scope">
                        {{ (scope.row.price).toFixed(2)}}
                    </template>
                </el-table-column>


                <el-table-column label="操作">
                    <template #default="scope">
                        <a href="javascript:void(0)" @click="deleteCart(scope.row.id)" class="delete-link">删除</a>
                    </template>
                </el-table-column>
        </el-table>

        <div class="total-container">
          <span class="total-label">总计：</span>
          <span class="total-price">{{ getTotalPrice() }}</span>
        </div>

        <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; text-align: left;display: flex;">
          <span style="font-weight: bold; font-size: 20px;float: left;flex: 3;color: #0099CC;">选择预约时间段</span>
        </div>
        <div class="time-slots">
          <div
              class="time-slot"
              v-for="(slot, index) in state.timeSlots"
              :key="index"
              :class="{ active: slot.selected, 'disabled': slot.stateRadio === '已预约' }"
              @click="slot.stateRadio !== '已预约' && selectTimeSlot(slot)"
          >
            <div class="time" :class="{ selected: slot.selected }">{{ slot.time }}</div>
          </div>
        </div>

        <div class="selected-time" v-if="state.selectedTimeSlot!=null">
          <div v-if="state.selectedTimeSlot">已选时间段：{{state.selectedTimeSlot.time}}</div>
          <el-button v-else content="请选择时间段"></el-button>
        </div>

              <!-- 填写订单信息 -->
              <div class="order-list">
                  <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; text-align: left;display: flex;">
                      <span style="font-weight: bold; font-size: 20px;float: left;flex: 3;color: #0099CC;">填写信息</span>
                  </div>

                  <div style="margin-top: 20px;margin-left: 50px;">
                      <span>姓名：</span>
                      <span><el-input v-model="state.form.name" placeholder="请填写姓名" style="width: 400px;"></el-input></span>
                  </div>
                  <div style="margin-top: 20px;margin-left: 50px;">
                      <span>电话：</span>
                      <span><el-input v-model="state.form.phone" placeholder="请填写电话" style="width: 400px;"></el-input></span>
                  </div>
              </div>

          <!-- 选择支付方式 -->
          <div class="pay-list">
              <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; text-align: left;display: flex;">
                  <span style="font-weight: bold; font-size: 20px;float: left;flex: 3;color: #0099CC;">支付方式</span>
              </div>
              <div style="margin-top: 15px;">
                  <el-radio-group v-model="state.selectedPaytype">
                      <el-radio v-for="paytype in state.payTypeList" :label="paytype.id" >
                          {{paytype.name}}
                      </el-radio>
                  </el-radio-group>
              </div>
          </div>

          <div class="detail-btn" style="display: flex; justify-content: center; align-items: center; height: 100%;">
              <div @click="toOrder"><i class="el-icon-date"></i>立即结算</div>
          </div>
    </div>

      <!-- 支付框 -->
    <el-dialog v-model="dialogFormVisible" title="订单支付" width="40%">
        <div style="text-align: center;">
            总金额：<span style="font-size: 25px;color: red;">{{ getTotalPrice() }}</span><span style="margin-left: 20px;">请扫码下方二维码支付</span>
        </div>
        <div style="margin-top: 10px;text-align: center;">
            <img :src="state.qrcode" alt="支付二维码" style="width: 200px;height: 220px;">
        </div>
        <div style="margin-top: 10px;text-align: center;">
            <el-button type="primary" @click="saveOrder">确认购买</el-button>
        </div>
    </el-dialog>

  </div>
</template>

<style>
    .total-container {
      margin-top: 20px;
      text-align: right;
      margin-right: 20px;
    }

    .total-label {
      font-weight: bold;
    }

    .total-price {
      color: red;
      font-size: 25px;
      font-weight: bold;

    }

    a.delete-link {
      color: #ff0000;
      text-decoration: none;
    }

    a.delete-link:hover {
      text-decoration: underline;
    }

    .time-slot {
      width: 100%;
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      cursor: pointer;
      padding: 15px;
      border-radius: 5px;
      background-color: #f5f5f5;
    }

    .time-slots {
      display: flex;
      flex-wrap: wrap;
    }

    .pay-list {
      width: 100%;
      margin: 0 auto;
      display: flow;
    }

    .detail-btn{
      width: 100%;
      margin: 0 auto;
      display: flow;
    }

    .detail-btn div {
      width: 160px;
      height: 50px;
      line-height: 14px;
      padding: 18px 0;
      font-size: 16px;
      box-sizing: border-box;
      background: #0099CC;
      color: #fff;
      text-align: center;
      margin-right: 15px;
      cursor: pointer;
      border-radius: 20px;
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


    .selected-time {
      margin-top: 20px;
      font-size: 16px;
      font-weight: bold;
      text-align: center;
      color: red;
      border-radius: 40px;
      border-color: #FF9900;
      border-style: double;
    }


    .time-slots {
      display: flex;
      flex-wrap: wrap;
    }

    .time-slot {
      width: 100%;
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      cursor: pointer;
      padding: 15px;
      border-radius: 5px;
      background-color: #f5f5f5;
    }

    .time {
      flex: 1;
      font-size: 16px;
      color: #333333;
    }

    .availability {
      font-size: 14px;
      color: #808080;
    }

    .selected-time {
      margin-top: 20px;
      font-size: 16px;
      font-weight: bold;
      text-align: center;
    }

    .selected {
      color: #ffffff;
      font-weight: bold;
    }

    .active {
      background-color: #007bff;
      color: #ffffff;
    }

    .disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    .disabled .time {
      color: #808080;
    }

    .disabled .availability {
      color: #808080;
    }
</style>
