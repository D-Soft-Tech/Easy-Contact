package com.decagon.android.sq007

data class QuickData(
    var newId: String?,
    var newName: String,
    var newPhone: String
) {
    constructor() : this("", "", "") {}
}
