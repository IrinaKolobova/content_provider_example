package com.ivk.contentproviderexam

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.content_main.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    private var readGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        Log.d(TAG, "onCreate: checkSelfPermission returned $hasReadContactPermission")

        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreate: permission granted")
            readGranted = true  
        } else {
            Log.d(TAG, "onCreate: permission denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Log.d(TAG, "fab onClick: starts")
            val projection = arrayOf((ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
            val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val contacts = ArrayList<String>()          //create a list to hold contacts
            cursor?.use {
                while(it.moveToNext()){
                    contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                }
            }

            val adapter = ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
            contact_names.adapter = adapter

            Log.d(TAG, "fab onClick: ends")
        }
        Log.d(TAG, "onCreate: ends")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: starts")
        when(requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                readGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    // do the contact-related task we need to do
                    Log.d(TAG, "onRequestPermissionsResult: permission granted")
                    true
                } else {
                    // permission denied
                    // disable the functionality that depends on this permission
                    Log.d(TAG, "onRequestPermissionsResult: permission refused")
                    false
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: ends")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}