package com.decagon.android.sq007

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.android.sq007.databinding.ActivityMainBinding
import com.google.firebase.database.*

class MainActivity :
    AppCompatActivity(),
    ContactListAdapter.OnItemClickListener,
    ItemClickListener {
    // List of contactsReturned from database
    var contactReturned = mutableListOf<UserModel>()

    // creating variable for storing instance of the firebase database
    private lateinit var db: FirebaseDatabase

    // Creating reference (i.e table name)
    private lateinit var databaseReference: DatabaseReference

    // ViewBinding
    lateinit var binding: ActivityMainBinding

    private lateinit var contactFromFirebaseListRecyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // viewBinding Variable
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        val view = binding.root

        setContentView(view)

        // storing an instance of the FirebaseDatabase into the variable created above
        db = FirebaseDatabase.getInstance()

        // database reference id
        databaseReference = db.getReference("Contacts")

        // Goes to a new activity where to save new contact
        binding.floatingActionButton.setOnClickListener() {
            val intent = Intent(this, SaveNewAndEditPage::class.java)
            startActivity(intent)
        }

        binding.tilNumSv.setOnClickListener() {
            val intent = Intent(this, PhoneContactApp::class.java)
            startActivity(intent)
        }

        // getting reference to views
//        var name = binding.etUserName
//        var phone = binding.etUserPassword
//        var save = binding.btnAddNew
//
//        //Sending data to database
//        save.setOnClickListener() {
//            if (name.text.isNotEmpty() && phone.text.isNotEmpty()) {
//
//                //Unique identifier for each row
//                var id = databaseReference.push().key
//
//                var data = QuickData(id, name.text.toString(), phone.text.toString())
//
//                try {
//                    databaseReference.child(id.toString()).setValue(data)
//                    Toast.makeText(this, R.string.contact_saved_toast, Toast.LENGTH_SHORT).show()
//                } catch (e: Error) {
//                    Toast.makeText(this, R.string.contact_not_saved_toast, Toast.LENGTH_SHORT).show()
//                }
//
//            } else {
//                if (name.text.isEmpty()) name.setError("Please Enter a name")
//                if (phone.text.isEmpty()) phone.setError("Please enter a phone number")
//            }
//        }

        // RecyclerView adapter for the contacts i load from my phone
//        var contactListRecyclerView = findViewById<RecyclerView>(R.id.contact_list_rv)
//        contactListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        contactListRecyclerView.adapter = ContactListAdapter(contactItems, this)

        // RecyclerView adapter for contacts from firebase
        contactFromFirebaseListRecyclerView = findViewById(R.id.item_from_database_rv)
        contactFromFirebaseListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // calling the readFromFirebase()
        readFromFirebase()
    }

    override fun onItemClick(position: Int) {
        // The Clicked item in the recyclerView is
        val clickedItem = contactReturned[position]

        // Saving this in a bundle
        val intent = Intent(this, ProfilePage::class.java)
        intent.putExtra("contactName", clickedItem.newName)
        intent.putExtra("contactPhone", clickedItem.newPhone)

        startActivity(intent)
    }

    // Function to read from the database
    private fun readFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            // we are only interested in the onDataChange method, therefore, we do not perform any operation in this method
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // clear the list first before adding to it
                    contactReturned.clear()

                    for (h in snapshot.children) {
                        var eachData = h.getValue(UserModel::class.java)

                        if (eachData != null) {
                            contactReturned.add(eachData)
                        } else {
                            Toast.makeText(this@MainActivity, "Failed to call from database", Toast.LENGTH_LONG).show()
                        }
                    }
                    contactFromFirebaseListRecyclerView.adapter = ReadFromAdapter(contactReturned, this@MainActivity)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to call from database", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

//    override fun onRecyclerItemClicked(name : String) {
//
//    }

    override fun onRecyclerItemClicked(id: String, name: String, phone: String) {
        val intent = Intent(this, ProfilePage::class.java)
        intent.putExtra("contactName", name)
        intent.putExtra("contactPhone", phone)
        intent.putExtra("contactId", id)
        startActivity(intent)
    }
}
