import request from './request'

/** 获取公开文章列表。 */
export const getPublicArticles = (params) => request.get('/articles', { params })

/** 获取当前作者自己的文章列表。 */
export const getMyArticles = (params) => request.get('/articles/mine', { params })

/** 获取文章详情。 */
export const getArticleById = (id) => request.get(`/articles/${id}`)

/** 创建文章草稿。 */
export const createArticle = (data) => request.post('/articles', data)

/** 更新文章。 */
export const updateArticle = (id, data) => request.put(`/articles/${id}`, data)

/** 发布文章。 */
export const publishArticle = (id) => request.post(`/articles/${id}/publish`)

/** 取消发布文章。 */
export const unpublishArticle = (id) => request.post(`/articles/${id}/unpublish`)

/** 删除文章。 */
export const deleteArticle = (id) => request.delete(`/articles/${id}`)

/** 获取评论列表。 */
export const getComments = (id) => request.get(`/articles/${id}/comments`)

/** 提交评论。 */
export const createComment = (id, data) => request.post(`/articles/${id}/comments`, data)

/** 点赞文章。 */
export const likeArticle = (id) => request.post(`/articles/${id}/like`)

/** 取消点赞。 */
export const unlikeArticle = (id) => request.delete(`/articles/${id}/like`)
