import request from './request'

/** 获取当前作者的文件夹列表。 */
export const getMyFolders = () => request.get('/folders/mine')

/** 创建文件夹。 */
export const createFolder = (data) => request.post('/folders', data)

/** 更新文件夹。 */
export const updateFolder = (id, data) => request.put(`/folders/${id}`, data)

/** 删除文件夹。 */
export const deleteFolder = (id) => request.delete(`/folders/${id}`)
