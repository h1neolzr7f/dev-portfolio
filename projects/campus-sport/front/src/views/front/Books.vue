<script setup>
  import router from "@/router";
  import request from "@/utils/request";
  import {ElMessage} from "element-plus";
  import {onMounted, reactive, ref, nextTick} from "vue";
  import {useUserStore} from "@/stores/user";
  import '@wangeditor/editor/dist/css/style.css' // 引入 css
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

  const userStore = useUserStore()
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

  const state = reactive({
    tableData:[],
    form:{},
    payListdialog:false,
    payDialogFormVisible:false,
  })

  const load = () => {
    request.get('/front/books/list').then(res => {
      state.tableData = res.data
      state.tableData = state.tableData.filter((item) => item.userId === user.id);
    })


  }
  load()  // 调用 load方法拿到后台数据

  //轮播图
  request.get('/front/rotation/list').then(res => {
      state.rotationList = res.data
      state.rotationList = state.rotationList.filter((item) => item.indexRadio === '否');
  })

  //删除订单
  const deleteOrders =(id) =>{
      request.delete('/front/books/' + id).then(res => {
          if (res.code === '200') {
              ElMessage.success('操作成功')
              load()  // 刷新表格数据
          } else {
              ElMessage.error(res.msg)
          }
      })
  }

  //取消订单
  const cancelOrders =(id) =>{
      request.put('/front/books/cancel/' + id).then(res => {
          if (res.code === '200') {
              ElMessage.success('操作成功')
              load()  // 刷新表格数据
          } else {
              ElMessage.error(res.msg)
          }
      })
  }


  //弹出评论
  state.goodids = ''
  const dialogFormVisible = ref(false)
  const handlerCommentAdd = (id,goodids,bizUserId) =>{
      dialogFormVisible.value = true
      state.goodids = goodids
      state.form.ordersId = id
      state.form.bizUserId = bizUserId
  }
  //保存评价
  state.orders = {}
  const saveComment = () => {
      let array = state.goodids.split(',')
      array.forEach((e,i)=> {
          state.form.areaId = e  // 当前模块的id
          state.form.userId = user.id //用户id
          // 发送数据
          request.post('/front/comments/update', state.form).then(res => {
              if (res.code === '200') {
                  dialogFormVisible.value = false
              } else {
                  ElMessage.error(res.msg)
                  return
              }
          })
      });
      //修改订单状态为'已评价'
      state.orders.id = state.form.ordersId
      state.orders.stateRadio = '已评价'
      request.post('/front/books/update',state.orders).then(res => {
          if (res.code === '200') {
              load()  // 刷新表格数据
          } else {
              ElMessage.error(res.msg)
          }
      })
      ElMessage.success("评论成功")
  }

  const updateSingup = (rowId) =>{
      state.orders.id = rowId
      state.orders.stateRadio = '签到'
      request.put('/front/books/update',state.orders).then(res => {
        if (res.code === '200') {
          load()  // 刷新表格数据
        } else {
          ElMessage.error(res.msg)
        }
      })
      ElMessage.success("签到成功")
  }

  let bookId = '';
  let book=null;

  //显式支付方式
  const showPayList = (rowId) => {
    bookId = rowId;
    book = state.tableData.filter(item=>item.id==bookId)
    book  = book[0];
    state.form.cost = getTotal();
    state.payListdialog=true;
  }

  //支付方式
  state.selectedPaytype = 1;
  state.payTypeList = []
  request.get('/front/paytype/list').then(res => {
    state.payTypeList = res.data
  })



  const updateLeave = () =>{

    state.orders.id = bookId
    state.orders.stateRadio = '结束'
    state.orders.total = state.form.cost;
    request.put('/front/books/update',state.orders).then(res => {
      if (res.code === '200') {
        state.payDialogFormVisible=false;
        load()  // 刷新表格数据
      } else {
        ElMessage.error(res.msg)
      }
    })
    ElMessage.success("结算成功")
  }

  //支付的二维码
  state.qrcode = ''
  const toOrder = (rowId) =>{
    state.payListdialog=false;
    state.payDialogFormVisible = true
    const selectPaytype = state.payTypeList.filter((item) => item.id === state.selectedPaytype);
    if(selectPaytype && selectPaytype.length){
      state.qrcode = selectPaytype[0].img
    }
  }

  const getTotal = () => {
    if (!book || !book.intime) {
      return 0;
    }
    const startTime = new Date(book.intime).getTime();
    if (Number.isNaN(startTime)) {
      return 0;
    }
    const currentTime = new Date().getTime();
    const timeDifference = currentTime - startTime
    let hours = Math.floor(timeDifference / (1000 * 60 * 60));

// 检查是否要加上额外的小时
    const minutes = Math.floor((timeDifference % (1000 * 60 * 60)) / (1000 * 60));
    if (minutes > 15) {
      hours += 1;
    }
    const total = hours * book.rent;
    return total;
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
            <span style="font-size: 14px;margin-right: 20px;">当前位置：首页 > 我的订单</span>
        </div>

          <div style="padding-bottom: 15px ;border-bottom: 3px solid #0099CC; text-align: left;display: flex;">
              <span style="font-weight: bold; font-size: 24px;float: left;flex: 3;color: #0099CC;">我的订单</span>
          </div>

          <el-table :data="state.tableData" style="width: 100%;margin-top: 20px;" stripe border :header-cell-class-name="'headerBg'">
                <el-table-column prop="id" label="序号">
                  <template #default="scope">
                    {{ scope.$index + 1 }}
                  </template>
                </el-table-column>
                <el-table-column prop="name" label="订单号"></el-table-column>
                <el-table-column label="订单明细"><template #default="scope"><el-button @click="view(scope.row.content)">查看</el-button></template></el-table-column>
                <el-table-column prop="price" label="定金"></el-table-column>
                <el-table-column prop="total" label="总金额"></el-table-column>
              <el-table-column prop="createTime" label="下单时间"></el-table-column>
              <el-table-column prop="intime" label="签到时间"></el-table-column>
              <el-table-column prop="outtime" label="离场时间"></el-table-column>
                <el-table-column prop="stateRadio" label="订单状态"></el-table-column>
              <el-table-column label="操作" width="150px;">
                  <template #default="scope">
                      <a href="javascript:void(0)" @click="deleteOrders(scope.row.id)" class="delete-link" style="margin-right: 15px;">删除</a>
                      <a href="javascript:void(0)" @click="cancelOrders(scope.row.id)" class="delete-link" style="margin-right: 15px;" v-if="scope.row.stateRadio=='已预约'">取消</a>
                      <a href="javascript:void(0)" @click="updateSingup(scope.row.id)" class="delete-link" v-if="scope.row.stateRadio=='已预约'">签到</a>
                      <a href="javascript:void(0)" @click="showPayList(scope.row.id)" class="delete-link" v-if="scope.row.stateRadio=='签到' && scope.row.stateRadio!='结束'">结束</a>
                      <a href="javascript:void(0)" @click="handlerCommentAdd(scope.row.id,scope.row.goodids,scope.row.bizUserId)" class="delete-link" style="margin-left: 10px;" v-if="scope.row.stateRadio=='结束'">评价</a>
                  </template>
              </el-table-column>
        </el-table>

    </div>

      <el-dialog v-model="viewShow" title="预览" width="40%">
          <div  id="editor-content-view" class="editor-content-view" v-html="content" style="padding: 0 20px"></div>
          <template #footer>
      <span class="dialog-footer">
        <el-button @click="viewShow = false">关闭</el-button>
      </span>
          </template>
      </el-dialog>

          <!-- 评论窗口 -->
          <el-dialog v-model="dialogFormVisible" title="评论" width="30%">
              <el-form :model="state.form" label-width="50px" style="padding: 0 20px" status-icon>
                  <el-form-item label="评分">
                      <el-rate v-model="state.form.score">
                      </el-rate>
                  </el-form-item>
                  <el-form-item label="内容">
                      <el-input type="textarea" :rows="5" v-model="state.form.content" autocomplete="off"></el-input>
                  </el-form-item>
              </el-form>
              <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveComment">
          确定
        </el-button>
      </span>
              </template>
          </el-dialog>

    <!-- 选择支付方式 -->
    <el-dialog v-model="state.payListdialog" title="订单支付" width="40%">
      <div class="pay-list">
        <div style="padding-bottom: 15px ;border-bottom: 3px solid #99CC99; text-align: left;display: flex;">
          <span style="font-weight: bold; font-size: 20px;float: left;flex: 3;color: #99CC99;">支付方式</span>
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
        <div @click="toOrder">立即结算</div>
      </div>
    </el-dialog>

    <!-- 支付框 -->
    <el-dialog v-model="state.payDialogFormVisible" title="订单支付" width="40%">
      <div style="text-align: center;">
        总金额：<span style="font-size: 25px;color: red;">{{ state.form.cost }}</span><span style="margin-left: 20px;">请扫码下方二维码支付</span>
      </div>
      <div style="margin-top: 10px;text-align: center;">
        <img :src="state.qrcode" alt="支付二维码" style="width: 200px;height: 220px;">
      </div>
      <div style="margin-top: 10px;text-align: center;">
        <el-button type="primary" @click="updateLeave">确认支付</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style>

.total-container {
  margin-top: 20px;
  text-align: right;
}

.total-label {
  font-weight: bold;
}

.total-price {
  color: red;
  font-weight: bold;
}

a.delete-link {
    color: #ff0000;
    text-decoration: none;
}

a.delete-link:hover {
    text-decoration: underline;
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
</style>
