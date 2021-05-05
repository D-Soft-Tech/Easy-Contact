package com.decagon.android.sq007

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReadFromAdapter(
    private val contactsReturnedFromFirebase: MutableList<UserModel>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<ReadFromAdapter.ReturnedFromListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnedFromListViewHolder {
        return ReturnedFromListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.contact_lists,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return contactsReturnedFromFirebase.size
    }

    override fun onBindViewHolder(holder: ReturnedFromListViewHolder, position: Int) {
        val id = contactsReturnedFromFirebase[position].newId
        val name = contactsReturnedFromFirebase[position].newName
        val phone = contactsReturnedFromFirebase[position].newPhone

        holder.itemView.apply {
            findViewById<ImageView>(R.id.user_image_iv).setImageResource(R.drawable.ic_person_pin)
            findViewById<TextView>(R.id.contact_display_name_tv).text = contactsReturnedFromFirebase[position].newName
            findViewById<TextView>(R.id.contact_phone_number).text = contactsReturnedFromFirebase[position].newPhone
        }
        holder.itemView.setOnClickListener {
            listener.onRecyclerItemClicked(id, name, phone)
        }
    }
    class ReturnedFromListViewHolder(itemToView: View) : RecyclerView.ViewHolder(itemToView)
}
