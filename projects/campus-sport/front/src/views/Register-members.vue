<script setup>
import { reactive, ref} from "vue"
import { User, Lock, Message } from '@element-plus/icons-vue'
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import router from "@/router";
import config from "../../config";

  const loginData = reactive({})
  const rules = reactive({
    username: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
    ],
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 3, max: 20, message: '密码长度在3-20位之间', trigger: 'blur' },
    ],
    confirmPassword: [
      { required: true, message: '请确认密码', trigger: 'blur' },
      { min: 3, max: 20, message: '密码长度在3-20位之间', trigger: 'blur' },
    ],
  })
  const ruleFormRef = ref()
  const register = () => {
    ruleFormRef.value.validate(valid => {
      if (valid) {
        if (loginData.password !== loginData.confirmPassword) {
          ElMessage.warning('两次密码不一致')
        }
		loginData.role = 'members'
        // 发送表单数据给后台
        request.post('/register', loginData).then(res => {
          if (res.code === '200') {
            ElMessage.success('注册成功')
            router.push('/login')
          } else {
            ElMessage.error(res.msg)
          }
        })
      }
    })
  }

</script>

<template>
  <div class="wrapper">
    <div style="margin: 100px auto; background-color: #0099CC; width: 500px; height: 600px; padding: 20px;">
      <div style="margin: 20px 0; text-align: center; font-size: 24px;color: #ffffff;"><b>{{config.projectName}} | 用户注册</b></div>
      <el-form
              ref="ruleFormRef"
              :model="loginData"
              :rules="rules"
              size="large"
              status-icon
      >
        <el-form-item prop="username">
          <span style="color: #ffffff;">请输入姓名：</span>
          <el-input v-model="loginData.username" placeholder="请输入姓名"  />
        </el-form-item>
        <el-form-item prop="email">
          <span style="color: #ffffff;">请输入邮箱：</span>
          <el-input v-model="loginData.email" placeholder="请输入邮箱"  />
        </el-form-item>
        <el-form-item prop="password">
          <span style="color: #ffffff;">请输入密码：</span>
          <el-input v-model="loginData.password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <span style="color: #ffffff;">请确认密码：</span>
          <el-input v-model="loginData.confirmPassword" show-password placeholder="请确认密码"/>
        </el-form-item>
        <el-form-item style="margin: 10px 0; text-align: right">
          <el-button type="primary" autocomplete="off" @click="register">注册</el-button>
          <el-button autocomplete="off" @click="router.push('/login')">登录</el-button>
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
