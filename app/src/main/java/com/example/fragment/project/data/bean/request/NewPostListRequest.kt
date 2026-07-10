package com.example.fragment.project.data.bean.request
//{
//    "pageNo": 0,
//    "pageSize": 0,
//    "beginTime": "2025-09-06T09:22:54.945Z",
//    "endTime": "2025-09-06T09:22:54.945Z",
//    "postId": 0
//}
data class NewPostListRequest(
    val pageNo: Int,
    val pageSize: Int,
//    val beginTime: String,
    val endTime: String?,
    val postId: String?
)
