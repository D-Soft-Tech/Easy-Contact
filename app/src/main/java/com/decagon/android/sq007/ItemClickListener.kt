package com.decagon.android.sq007

interface ItemClickListener {
    fun onRecyclerItemClicked(
        id: String,
        name: String,
        phone: String
    )
}
