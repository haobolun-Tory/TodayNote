<template>
  <div class="editor-page container">
    <div class="card editor-container">
      <div class="form-group">
        <input v-model="title" type="text" placeholder="输入文章标题..." class="title-input" />
      </div>
      <div class="editor-main">
        <textarea v-model="content" placeholder="开始写作..." class="content-input"></textarea>
      </div>
      <div class="editor-footer">
        <button class="btn btn-secondary" @click="$router.push('/')">取消</button>
        <button class="btn btn-primary" @click="publish">发布文章</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessMessage, showWarningMessage } from '@/utils/message'

const title = ref('')
const content = ref('')
const router = useRouter()

/**
 * 旧版演示编辑页的发布动作。
 */
const publish = async () => {
  if (!title.value || !content.value) {
    showWarningMessage('标题和内容不能为空')
    return
  }
  // 模拟发布请求
  console.log('Publishing:', { title: title.value, content: content.value })
  showSuccessMessage('发布成功')
  router.push('/')
}
</script>

<style scoped>
.editor-page {
  height: calc(100vh - 100px);
}

.editor-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.title-input {
  width: 100%;
  font-size: 1.5rem;
  border: none;
  outline: none;
  font-weight: bold;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.editor-main {
  flex: 1;
  margin-top: 1rem;
}

.content-input {
  width: 100%;
  height: 100%;
  resize: none;
  border: none;
  outline: none;
  font-size: 1rem;
  line-height: 1.6;
}

.editor-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}

.btn-secondary {
  background-color: white;
  border: 1px solid #d1d5db;
  color: var(--text-secondary);
}
</style>
