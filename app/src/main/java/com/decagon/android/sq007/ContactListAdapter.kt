package com.decagon.android.sq007

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactListAdapter(
    private val contacts: MutableList<UserModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        return ContactListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.contact_lists,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactListViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<ImageView>(R.id.user_image_iv).setImageResource(R.drawable.ic_person_pin)
            findViewById<TextView>(R.id.contact_display_name_tv).text = contacts[position].newName
            findViewById<TextView>(R.id.contact_phone_number).text = contacts[position].newPhone
        }
    }

    inner class ContactListViewHolder(itemToView: View) : RecyclerView.ViewHolder(itemToView), View.OnClickListener {

        init {
            itemToView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            listener.onItemClick(position)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
