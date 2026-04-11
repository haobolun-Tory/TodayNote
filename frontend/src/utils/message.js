import { ElMessage, ElMessageBox } from 'element-plus'

/** 展示成功消息。 */
export const showSuccessMessage = (message) =>
  ElMessage({
    type: 'success',
    message,
    plain: true,
  })

/** 展示错误消息。 */
export const showErrorMessage = (message) =>
  ElMessage({
    type: 'error',
    message,
    plain: true,
  })

/** 展示警告消息。 */
export const showWarningMessage = (message) =>
  ElMessage({
    type: 'warning',
    message,
    plain: true,
  })

/** 展示普通提示消息。 */
export const showInfoMessage = (message) =>
  ElMessage({
    type: 'info',
    message,
    plain: true,
  })

/** 展示确认弹窗，用于删除、取消发布等危险操作。 */
export const showConfirmDialog = (message, title = '提示') =>
  ElMessageBox.confirm(message, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
