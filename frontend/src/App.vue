<template>
  <el-config-provider v-bind="elementOptions">
    <router-view />
  </el-config-provider>
</template>

<script setup>
import { getElementOptions } from '@/plugins/element'

// 统一读取 Element Plus 的全局配置，避免页面层重复注入。
const elementOptions = getElementOptions()
</script>
