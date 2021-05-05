package com.decagon.android.sq007

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.decagon.android.sq007.databinding.ActivityPhoneContactAppBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PhoneContactApp :
    AppCompatActivity(),
    ContactListAdapter.OnItemClickListener,
    ItemClickListener {
    // List of contacts
    private var contactItems = mutableListOf<UserModel>()

    // Creating a variable for storing the viewBinding
    private lateinit var binding: ActivityPhoneContactAppBinding

    // creating variables for storing views
    private lateinit var floatingBackButton: FloatingActionButton
    private lateinit var searchBar: android.widget.SearchView
    private lateinit var contactRecyclerView: RecyclerView

    // recyclerview for contact
    private lateinit var contactListRecyclerView: RecyclerView

    // Columns to check for in the phone contacts list
    private val columns = listOf<String>(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone._ID
    ).toTypedArray()

    // Permission request code
    private var storage_permission_code = 1

    // Array of Permissions to be requested
    private var permissionsToAsk = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // storing a reference to the viewBinding in the variable created above
        binding = ActivityPhoneContactAppBinding.inflate(LayoutInflater.from(this))
        // Attaching the viewBinding to root view
        val view = binding.root
        // Inflating the activity with viewBinding
        setContentView(view)

        // getting reference to views and saving them in the global variables created above
        floatingBackButton = binding.contactListFloatingActionButton
        searchBar = binding.contactListTilNumSv
        contactRecyclerView = binding.contactListItemFromPhoneRv

        // setting onClickListener to the back button
        floatingBackButton.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // moves back to the main activity
            finish() // finishes the current activity
        }

        permissionChecker()

        // RecyclerView adapter for the contacts i load from my phone
        contactListRecyclerView = findViewById<RecyclerView>(R.id.contact_list_item_from_phone_rv)
        contactListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // setting an onQueryTextListener to the searchBar
        // Then using the text inside the searchBar as the selection argument in the readContact function
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // writing a selection and selectionArgument for the query in the readContact function
                val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
                val selectionArg = Array(1) { "%$newText%" }

                // calling the readContact method and parsing in the required parameter
//                readContact(selection, selectionArg)

                var new = mutableListOf<UserModel>()

                // Saving the result set in a variable
                val contactsResult = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    columns,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
                    Array(1) { "%$newText%" },
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )

                if (contactsResult?.moveToFirst()!!) {
                    new.add(
                        UserModel(
                            "",
                            contactsResult.getString(0).toString(),
                            contactsResult.getString(1)
                        )
                    )

                    // Adding the result set to the contact lists
                    while (contactsResult.moveToNext()) {
                        new.add(
                            UserModel(
                                "",
                                contactsResult.getString(0).toString(),
                                contactsResult.getString(1)
                            )
                        )
                    }
                    contactListRecyclerView.adapter = ContactListAdapter(new, this@PhoneContactApp)
                    contactsResult.close()
                } else Toast.makeText(this@PhoneContactApp, "No contact to display", Toast.LENGTH_LONG).show()

                return false
            }
        })
    }

    // This function checks, if the permission has been given or not, if not it asks for the permission
    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionChecker() {
        if (
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            (
                checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                )
        ) {
            // we read the contact once we have assertained that the permissions has been granted
            readContact(null, null, contactItems)
        } else {
            // If the permission was not granted, we request for the permission
            requestStoragePermission()
        }
    }

    // Function to request for permission if it hasn't been given before
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestStoragePermission() {
        if (
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) &&
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)
        ) {
            MaterialDialog(this)
                .show {
                    title(R.string.permission_title)
                    message(R.string.image_permission_purpose)
                    negativeButton { cancel() }
                    positiveButton {
                        requestPermissions(permissionsToAsk, storage_permission_code)
                    }
                }
        } else {
            requestPermissions(permissionsToAsk, storage_permission_code)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == storage_permission_code) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show()

                // we read the contact once we have assertained that the permissions has been granted
                readContact(null, null, contactItems)
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }
    }

    // This function reads the contacts list of the phone
    private fun readContact(selection: String?, array: Array<String>?, data: MutableList<UserModel>) {

        // Saving the result set in a variable
        val contactsResult = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            columns,
            selection,
            array,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        if (contactsResult?.moveToFirst()!!) {
            data.add(
                UserModel(
                    "",
                    contactsResult.getString(0).toString(),
                    contactsResult.getString(1)
                )
            )

            // Adding the result set to the contact lists
            while (contactsResult.moveToNext()) {
                data.add(
                    UserModel(
                        "",
                        contactsResult.getString(0).toString(),
                        contactsResult.getString(1)
                    )
                )
            }
            contactListRecyclerView.adapter = ContactListAdapter(contactItems, this@PhoneContactApp)
            contactsResult.close()
        } else Toast.makeText(this, "No contact to display", Toast.LENGTH_LONG).show()
    }

    override fun onItemClick(position: Int) {
        // The Clicked item in the recyclerView is
        val clickedItem = contactItems[position]

        // Saving this in a bundle
        val intent = Intent(this, ProfilePage::class.java)
        intent.putExtra("contactName", clickedItem.newName)
        intent.putExtra("contactPhone", clickedItem.newPhone)
        intent.putExtra("phoneContactApp", "Don't Edit / Delete")

        startActivity(intent)
    }

    override fun onRecyclerItemClicked(id: String, name: String, phone: String) {
        val intent = Intent(this, ProfilePage::class.java)
        intent.putExtra("contactNameFromContact", name)
        intent.putExtra("contactPhoneFromContact", phone)
        intent.putExtra("phoneContactApp", "Don't Edit / Delete")
        startActivity(intent)
    }
}
