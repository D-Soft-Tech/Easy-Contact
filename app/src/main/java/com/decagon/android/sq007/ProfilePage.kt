package com.decagon.android.sq007

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.decagon.android.sq007.databinding.ProfilePageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class ProfilePage : AppCompatActivity() {
    // storage code for permission
    private val storage_permission_code = 1

    // ViewBinding variable
    private lateinit var binding: ProfilePageBinding

    // variable for saving the database instance
    private lateinit var db: FirebaseDatabase
    // variable for storing the reference to the database
    private lateinit var ref: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getting reference to the viewBinding of the page and storing it in the binding variable created above
        binding = ProfilePageBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)

        // Initializing the FirebaseDatabase credentials
        db = FirebaseDatabase.getInstance()
        ref = db.getReference("Contacts")

        // Function to get all the data saved in the intent parsed in from other activity
        val id = intent.getStringExtra("contactId")
        val username = intent.getStringExtra("contactName")
        val phoneNumber = intent.getStringExtra("contactPhone")

        // setting the data from the intent into textViews
        binding.profilePageUserName.text = username.toString()
        binding.profilePageUserImageIv.setImageResource(R.drawable.ic_person_pin)
        binding.profilePageUserPhoneNumberTv.text = phoneNumber.toString()

        // Make call when the call icon is clicked
        binding.profilePageCallUser.setOnClickListener() {
            if (binding.profilePageUserPhoneNumberTv.text.isNotEmpty()) {
                makeCall(binding.profilePageUserPhoneNumberTv.text.toString())
            } else {
                Toast.makeText(this, R.string.profile_page_error_making_call, Toast.LENGTH_LONG).show()
            }
        }

        // setting onclick listener on the menu icon
        binding.profilePageMenu.setOnClickListener() {
            showPopUp(username.toString(), phoneNumber.toString(), id.toString())
        }
    }

    // Function to make call
    @RequiresApi(Build.VERSION_CODES.M)
    private fun makeCall(number: String) {
        if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse(
                    "tel:" + Uri.encode(number)
                )
            )
            startActivity(callIntent)
        } else {
            requestPhoneCallPermission()
        }
    }

    // Requests for permission to make phone call
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPhoneCallPermission() {
        if (
            shouldShowRequestPermissionRationale(CALL_PHONE)
        ) {
            MaterialDialog(this)
                .show {
                    title(R.string.permission_title)
                    message(R.string.call_permission_purpose)
                    negativeButton { cancel() }
                    positiveButton {
                        requestPermissions(arrayOf(CALL_PHONE), storage_permission_code)
                    }
                }
        } else {
            requestPermissions(arrayOf(CALL_PHONE), storage_permission_code)
        }
    }

    // Show PopUp function
    private fun showPopUp(name: String, phone: String, id: String) {
        var popup = PopupMenu(this@ProfilePage, binding.profilePageMenu)
        popup.inflate(R.menu.details_page_menu_options)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                // Runs when the clicked item in the menu is share
                R.id.menu_share_phone_number ->
                    {
                        // calls the sharePhone function
                        sharePhone(name, phone)
                        return@setOnMenuItemClickListener true
                    }
                // Runs when the clicked item in the menu is delete
                R.id.menu_delete_details -> {
                    if (intent.getStringExtra("phoneContactApp").toString() != "Don't Edit / Delete") {
                        // Asking confirmation from user before deleting the phone number
                        MaterialDialog(this)
                            .show {
                                title(R.string.profile_page_delete_confirmation_dialog_title)
                                message(R.string.profile_page_delete_confirmation_dialog_body)
                                negativeButton { cancel() }
                                positiveButton {
                                    // calls the delete function if the ok button is pressed
                                    deleteFromFireBase(id, name)
                                }
                            }
                    } else {
                        Snackbar.make(this, binding.profilePageParentLayout, "Sorry You are not permitted to perform this operation on this contact", Snackbar.LENGTH_LONG).show()
                    }
                    return@setOnMenuItemClickListener true
                }
                // Runs when the clicked item in the menu is edit
                R.id.menu_edit_details -> {
                    if (intent.getStringExtra("phoneContactApp").toString() != "Don't Edit / Delete") {
                        // calling the edit function
                        edit(id, name, phone)
                    } else {
                        Snackbar.make(this, binding.profilePageParentLayout, "Sorry You are not permitted to perform this operation on this contact", Snackbar.LENGTH_LONG).show()
                    }
                    return@setOnMenuItemClickListener true
                }

                else -> false
            }
        }

        // Forcefully Showing the icon
        try {
            val fieldMPopup = popup::class.java.getDeclaredField("mPopup")

            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popup)
            mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(mPopup, true)
        } catch (e: Exception) {
            // I don't want to do anything in the catch block so i am leaving it empty
        } finally { // I still want to show the popup menu items even if the
            // icons can not be shown, at least i want to show the texts
            // hence the reason for this finally block
            popup.show()
        }
    }

    // share phone number function
    private fun sharePhone(name: String, phone: String) {
        val myIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "This is $name's Phone Number \n $phone")
        }
        val shareIntent = Intent.createChooser(myIntent, "Share $name's Phone number using: ")

        startActivity(shareIntent)
    }

    // Delete from database
    private fun deleteFromFireBase(id: String, name: String) {
        ref.child(id).removeValue()

        Toast.makeText(this, "$name's Phone number has been deleted", Toast.LENGTH_LONG).show()

        // Go to the contact List Page
        finish()
    }

    // Edit contact phone number
    private fun edit(id: String, name: String, phone: String) {
        // Take the user to the edit page
        val intent = Intent(this, SaveNewAndEditPage::class.java) // Intent to go to the saveAndEditPage
        // Putting the values to edit as extras
        intent.putExtra("nameFromProfilePage", "$name")
        intent.putExtra("phoneNumberFromProfilePage", "$phone")
        intent.putExtra("idFromProfilePage", "$id")

        // start the activity
        startActivity(intent)
        finish()
    }
}
