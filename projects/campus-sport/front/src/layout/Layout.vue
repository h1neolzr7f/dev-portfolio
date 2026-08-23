<script setup>
import { RouterView } from 'vue-router'
import router from "@/router";
import {useUserStore} from "@/stores/user";
import request from "@/utils/request";
import {ElMessage} from "element-plus";
const userStore = useUserStore()
let user = userStore.getUser
const activePath = router.currentRoute.value.path

const logout = () => {
  request.get('/logout/' + user.uid).then(res => {
    if (res.code === '200') {
      userStore.logout()
    } else {
      ElMessage.error(res.msg)
    }
  })
}
const menus = userStore.getMenus
const getMenuIndex = (path) => path && path.startsWith('/') ? path : '/' + path

const getAvatarHandler = (avatar) => {
  user.avatar = avatar
}
</script>

<template>
  <div class="mainBg">
    <div style="height: 60px; line-height: 60px; border-bottom: 1px solid #ccc; background-color: #ffffff;">
      <div style="display: flex">
        <div style="flex:1.2; color: #0099CC; font-weight: bold;  text-align: right; font-size: 20px">
          高校体育馆管理系统
        </div>

        <div style="flex: 1;text-align: right; ">
          <div>
          </div>
          <div style="width: 500px; text-align: right; padding-right: 20px">
            <span style="margin-right: 5px; color: #0099CC">你好，{{ user.name }}</span>
            <a href="javascript:void(0)" @click="router.push('/front/home')" class="infoCls">前台系统</a>
            <a href="javascript:void(0)" @click="router.push('/person')" class="infoCls">个人信息</a>
            <a href="javascript:void(0)" @click="router.push('/password')" class="infoCls">修改密码</a>
            <a href="javascript:void(0)" @click="logout" class="infoCls">注销</a>
          </div>
        </div>
      </div>
    </div>

    <div style="display: flex;">
      <div style="width: 230px; min-height: calc(100vh - 60px); border-right: 1px solid #ccc;background-color: #ffffff;">
        <el-menu
            :default-active="activePath"
            :default-openeds="menus.map(v => v.id + '')"
            class="el-menu-demo"
            style="border: none"
            router
        >
        <div v-for="item in menus" :key="item.id">
          <div v-if="item.type === 2">
            <el-menu-item :index="getMenuIndex(item.path)" v-if="!item.hide">
              <el-icon v-if="item.icon">
                <component :is="item.icon"></component>
              </el-icon>
              <span>{{ item.name }}</span>
            </el-menu-item>
          </div>
          <div v-else>
            <el-sub-menu :index="item.id + ''" v-if="!item.hide">
              <template #title>
                <el-icon v-if="item.icon">
                  <component :is="item.icon"></component>
                </el-icon>
                <span>{{ item.name }}</span>
              </template>
              <div  v-for="subItem in item.children" :key="subItem.id">
                <el-menu-item :index="getMenuIndex(subItem.path)" v-if="!subItem.hide">
                  <template #title>
                    <el-icon v-if="subItem.icon">
                      <component :is="subItem.icon"></component>
                    </el-icon>
                    <span>{{ subItem.name }}</span>
                  </template>
                </el-menu-item>
              </div>
            </el-sub-menu>
          </div>
        </div>
        </el-menu>
      </div>

      <div style="flex: 1; padding: 10px">
        <router-view v-slot="{ Component }">
          <component @getAvatar="getAvatarHandler" ref="childRef" :is="Component" />
        </router-view>
      </div>
    </div>

  </div>
</template>
<style>
  .infoCls{
    margin-left: 10px;
    text-decoration: none;
    color: #0099CC;
  }
  .infoCls a {
    text-decoration: none;
    color: #ffffff;
    transition: color 0.3s;
  }

  .infoCls:hover {
    color: #f00;
  }

  .infoCls:visited {
    color: #999;
  }

  .infoCls::after {
    content: '';
    width: 0;
    height: 1px;
    background-color: #f00;
    transition: width 0.3s;
  }

  .infoCls:hover::after {
    width: 100%;
  }

  .mainBg{
    background-color: rgba(255, 255, 255, 0.5);
    background-image: url("../assets/bg.jpg");
    background-repeat: no-repeat;
    background-size: cover;
    height: 100vh;
  }
</style>
