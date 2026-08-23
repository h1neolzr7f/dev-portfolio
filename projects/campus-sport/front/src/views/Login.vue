<script setup>
import {onMounted, reactive, ref} from "vue"
import {User, Lock} from '@element-plus/icons-vue'
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import {useUserStore} from "@/stores/user";
import router, {setRoutes} from "@/router";
import SIdentify from '../components/Sidentify.vue';
import config from "../../config";

// 图形验证码
let identifyCodes = "1234567890"
let identifyCode = ref('')
const failCount = ref(0)
const randomNum = (min, max) => {
  return Math.floor(Math.random() * (max - min) + min)
}
const makeCode = (o, l) => {
  for (let i = 0; i < l; i++) {
    identifyCode.value += o[randomNum(0, o.length)];
  }
}
const refreshCode = () => {
  identifyCode.value = "";
  makeCode(identifyCodes, 4);
}
// 生成验证码
onMounted(() => {
  identifyCode.value = "";
  makeCode(identifyCodes, 4);
})

const loginData = reactive({})
const rules = reactive({
  username: [
    {required: true, message: '请输入账号', trigger: 'blur'},
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 3, max: 20, message: '密码长度在3-20位之间', trigger: 'blur'},
  ],
})
const ruleFormRef = ref()
const login = () => {
  ruleFormRef.value.validate(valid => {
    if (valid) {
      // 失败3次触发验证码
      if (failCount.value >= 3 && loginData.code !== identifyCode.value) {
        ElMessage.warning('验证码错误')
        return
      }
      // 发送表单数据给后台
      request.post('/login', loginData).then(res => {
        if (res.code === '200') {
          const backendRoles = ['ADMIN', 'venue_manager', 'teacher']
          if (backendRoles.includes(res.data.user.role)) {
            router.push('/') // 后台的首页
          }
          		  else if(res.data.user.role === 'members'){
            router.push('/front') 
          }
          ElMessage.success('登录成功')
          const userStore = useUserStore()
          userStore.setManagerInfo(res.data)
        } else {
          ElMessage.error(res.msg)
          failCount.value ++  // 失败次数加1
        }
      })
    }
  })
}

</script>

<template>
  <div class="wrapper">
    <div style="margin: 150px auto; background-color: #0099CC; width: 500px; height: 400px; padding: 20px;">
      <div style="margin: 20px 0; text-align: center; font-size: 24px;color: #ffffff;"><b>{{config.projectName}}</b></div>
      <el-form
              ref="ruleFormRef"
              :model="loginData"
              :rules="rules"
              size="large"
              status-icon
      >
        <el-form-item prop="username">
          <span style="color: #ffffff;">请输入账户：</span>
          <el-input size="medium" prefix-icon="User" v-model="loginData.username" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <span style="color: #ffffff;">请输入密码：</span>
          <el-input size="medium" prefix-icon="Lock" show-password v-model="loginData.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <div style="display: flex; margin: 15px 0" v-if="failCount >= 3">
          <div style="flex: 1">
            <el-input v-model="loginData.code" placeholder="验证码"></el-input>
          </div>
          <div>
            <div @click="refreshCode" style="margin-left: 5px">
              <SIdentify :identifyCode="identifyCode" />
            </div>
          </div>
        </div>
        <el-form-item style="margin: 10px 0; text-align: right">
          <el-button type="primary" autocomplete="off" @click="login">登录</el-button>
          <el-button autocomplete="off" @click="router.push('/register-members')">用户注册</el-button>
        </el-form-item>
      </el-form>
        <div style="text-align: center;">
          <a style="text-decoration: none; color: #ffffff;margin-left: 20px;" href="/front/home">访问前台系统</a>
        </div>
    </div>
  </div>

</template>
<style>
  .wrapper {
    background-image: url("../assets/bg.jpg");
    background-repeat: no-repeat;
    background-size: cover;
    height: 100vh;
    overflow: hidden;
  }

  .el-form-item.is-error .el-input__inner{
    border-color: #ffffff;
  }
  .el-form-item.is-error .el-input__validateIcon{
    color: #ffffff;
  }
  .el-form-item__error{
    color: #ffffff;
  }
</style>

