package com.decagon.android.sq007

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decagon.android.sq007.databinding.ActivityMainBinding
import com.google.firebase.database.*


const val CHANNEL_ID = "123"

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
//                    Toast.makeText(this@MainActivity, "Hey new Data is availaible", Toast.LENGTH_LONG).show()
                    createNotification()
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

    //Notification Creator
    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            //Notification Buiilder
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setAutoCancel(true)

            notificationManager.notify(0, builder.build())
        }
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
