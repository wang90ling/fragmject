package com.example.fragment.project.data.bean.request

data class AbilityLabelSaveRequest(
    val categoryId: String,
    val labelGroups: List<LabelGroup>
){
    data class LabelGroup(
        val categoryLabelId: String,
        val labelItemIds: List<String>
    )
}
