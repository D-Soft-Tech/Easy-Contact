package com.decagon.android.sq007

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.decagon.android.sq007.databinding.ActivitySaveNewAndEditPageBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SaveNewAndEditPage : AppCompatActivity() {
    // Creating view binding variable
    private lateinit var binding: ActivitySaveNewAndEditPageBinding

    // Creating variables for storing reference to views
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var saveButton: Button

    // Creating variable for storing instance of the firebase database
    private lateinit var db: FirebaseDatabase
    // Creating variable for storing reference to the firebase database
    private lateinit var ref: DatabaseReference

    private lateinit var profilePageId: String

    // creating variable to store the data to be save
    private lateinit var data: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getting reference to the Page's viewBinding
        binding = ActivitySaveNewAndEditPageBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)

        // Initializing the database credentials created above
        db = FirebaseDatabase.getInstance()
        ref = db.getReference("Contacts")

        // storing references to view in variables
        name = binding.etUserName
        phone = binding.etUserPhone
        saveButton = binding.btnAddNew

        // checking if value was sent in from the Profile page
        if (intent.getStringExtra("idFromProfilePage") != null) { // Runs if an id was sent in from profile page
            // Get the id sent in from profile Page
            profilePageId = intent.getStringExtra("idFromProfilePage").toString()
            val profilePageName = intent.getStringExtra("nameFromProfilePage").toString()
            val profilePageNumber = intent.getStringExtra("phoneNumberFromProfilePage").toString()

            name.text = profilePageName
            phone.text = profilePageNumber
        }

        // Setting onClickListener for the save button
        saveButton.setOnClickListener() {
            // validating the inputFields
            when {
                !Validator.userNameValidation(name.text.toString()) -> { // If the name is not valid, an error message is set to the textField
                    name.error = "Invalid name"
                }
                !Validator.phoneNumberValidation(phone.text.toString()) -> { // sets and error message to the textField if the phoneNumber doesn't pass the validation
                    phone.error = "Invalid Phone number"
                }
                else -> { // Proceeds to saving the inputted details if validation is passed
                    // Checking if any intent is sent in with extra or not
                    when {
                        intent.getStringExtra("idFromProfilePage") != null -> { // Runs if an id was sent in from profile page

                            var pData = UserModel()
                            Log.d("dataToEdit", "onCreate: $profilePageId")

                            saveAndEditContact(profilePageId)
                        }
                        else -> { // If the cases above doesn't run, then we are saving our contacts afresh and not just editting
                            // Hence we call the method saveContactToTheDataBase and pass a new id to it
                            val newId = ref.push().key!! // This create a new id for saving new contact to the database

                            saveAndEditContact(newId)
                        }
                    }
                }
            }
        }
    }

    // Edits contacts sent in from profile page and updates to FirebaseDatabase
    // Note, the name is saveAndEditContact because this function is used to both save new contact and
    // also edit contact, it is just the id that is parsed to it that is different in the two scenario
    private fun saveAndEditContact(id: String) {
        // gets the newly inputted data, create and instance of the UserModel with it and then save it to the database
        data = UserModel(id, name.text.toString(), phone.text.toString())

        // Wrapping it in a try / Catch for safety purpose
        try {
            ref.child(id).setValue(data)
            Toast.makeText(this, R.string.contact_saved_toast, Toast.LENGTH_SHORT).show()
        } catch (e: Error) {
            Toast.makeText(this, R.string.contact_not_saved_toast, Toast.LENGTH_SHORT).show()
        }
        val intent = Intent(this, ProfilePage::class.java)
        intent.putExtra("contactId", data.newId)
        intent.putExtra("contactName", data.newName)
        intent.putExtra("contactPhone", data.newPhone)

        startActivity(intent)
        finish() // exiting out of the current activity
    }
}
