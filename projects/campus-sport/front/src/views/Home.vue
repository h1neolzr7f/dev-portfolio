<script setup>
import {reactive} from "vue";
import request from "@/utils/request";
import dayjs from "dayjs";

const state = reactive({
  notice: []
})

const load = () => {
  request.get("/notice").then(res => {
    state.notice = res.data
  })
}

const dateFliter = (val, format = "YYYY-MM-DD hh:mm:ss") => {
  if (!isNaN(val)) {
    val = parseInt(val);
  }
  return dayjs(val).format(format);
};
load()
</script>

<template>
  <div style="background-color: #ffffff;padding: 10px;border-radius: 10px;margin-top: 20px;">
    <el-row :gutter="10">

      <el-col :span="24">

        <div>
          <el-card style="width: 100%;  margin: 10px 0">
            <template #header>
              <div>
                <span style="font-size: 20px;font-weight:bold;color: #0099CC;">网站公告</span>
              </div>
            </template>
            <el-collapse accordion>
              <el-collapse-item v-for="(item,index) in state.notice" :key="item.id" :name="'' + index">
                <template #title>
                  <span style="font-size: 16px;">标题：{{ item.name }}</span>
                  <span style="margin-left: 10px">发布时间：{{ item.createTime }}</span>
                </template>
                <div v-html="item.content"></div>
              </el-collapse-item>
            </el-collapse>
          </el-card>
        </div>

      </el-col>
    </el-row>

    <el-divider />


  </div>
</template>
