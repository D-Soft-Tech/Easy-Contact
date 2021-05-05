package com.decagon.android.sq007

data class UserModel(
    var newId: String,
    var newName: String,
    var newPhone: String
) {
    constructor() : this("", "", "") {}
}
