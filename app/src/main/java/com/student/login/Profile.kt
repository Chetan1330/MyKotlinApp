package com.student.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.profile.*

class Profile : AppCompatActivity() {

    val File : Int = 0
    lateinit var uri : Uri
    lateinit var mStorage : StorageReference
    lateinit var ref : DatabaseReference
    lateinit var refprivate : DatabaseReference
    //lateinit var refphone : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val selectBtn = findViewById<View>(R.id.selectBtn) as Button

        ref = FirebaseDatabase.getInstance().getReference("stempedia-abd43")
        refprivate = FirebaseDatabase.getInstance().getReference("stempedia-private")
        //refphone = FirebaseDatabase.getInstance().getReference("Phone")
        mStorage = FirebaseStorage.getInstance().getReference("Uploads")
        progressBar.visibility =View.INVISIBLE
        textViewProgress.visibility =View.INVISIBLE
        uriTxt.visibility =View.INVISIBLE
        dwnTxt.visibility =View.INVISIBLE

        selectBtn.setOnClickListener(View.OnClickListener {
            view: View? -> val intent = Intent()
            intent.setType ("*/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select File"), File)
        })

        showfile.setOnClickListener(View.OnClickListener {
            startActivity(Intent( this, ViewUploadsActivity::class.java))
        })

        upload.setOnClickListener(View.OnClickListener {
            //onupload()
            onupload1()
        })

    }

    private fun onupload1() {
        val selectid: Int = radiogroup.checkedRadioButtonId
        val selectedradioBtn = findViewById<View>(selectid) as RadioButton

        if (editTextname.text.toString().isEmpty()) {
            editTextname.error = "Enter File Name "
            editTextname.requestFocus()
            return
        }
        else if (selectedradioBtn.text.toString().equals("Public")) {
            upload ()
            Toast.makeText(this, "Selected Public", Toast.LENGTH_SHORT).show()
        }
        else if (selectedradioBtn.text.toString().equals("Private")){
            upload1()
            Toast.makeText(this, "Selected Private", Toast.LENGTH_SHORT).show()
        }
        else{
            selectedradioBtn.error = "Please Select Public/Private"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val uriTxt = findViewById<View>(R.id.uriTxt) as TextView
        if (resultCode == RESULT_OK) {
            if (requestCode == File) {
                uri = data!!.data
                uriTxt.text = uri.toString()
                //upload ()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload() {
        var mReference = mStorage.child(uri.lastPathSegment)
        try {
            mReference.putFile(uri).addOnSuccessListener {
                taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.downloadUrl
                val dwnTxt = findViewById<View>(R.id.dwnTxt) as TextView
                dwnTxt.text = url.toString()
                Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
                val Name =editTextname.getText().toString().trim()
                val FileUrl = url.toString()
                val upload = Datalist(Name, FileUrl)
                ref.child(ref!!.push().key).setValue(upload)
                progressBar.visibility =View.INVISIBLE
                textViewProgress.visibility =View.INVISIBLE
            }.addOnProgressListener{
                taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.downloadUrl
                val progress: Double = (100 * taskSnapshot.bytesTransferred / taskSnapshot.getTotalByteCount()).toDouble()
                progressBar.visibility =View.VISIBLE
                textViewProgress.visibility =View.VISIBLE
                progressBar.setProgress(progress.toInt())
                textViewProgress.text ="$progress %"
            }
        }catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

    }


    private fun upload1() {
        var mReference = mStorage.child(uri.lastPathSegment)
        try {
            mReference.putFile(uri).addOnSuccessListener {
                taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.downloadUrl
                val dwnTxt = findViewById<View>(R.id.dwnTxt) as TextView
                dwnTxt.text = url.toString()
                Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
                val Name =editTextname.getText().toString().trim()
                val FileUrl = url.toString()
                val upload = Datalist(Name,FileUrl)
                refprivate.child(refprivate!!.push().key).setValue(upload)
                progressBar.visibility =View.INVISIBLE
                textViewProgress.visibility =View.INVISIBLE
            }.addOnProgressListener{
                taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.downloadUrl
                val progress: Double = (100 * taskSnapshot.bytesTransferred / taskSnapshot.getTotalByteCount()).toDouble()
                progressBar.visibility =View.VISIBLE
                textViewProgress.visibility =View.VISIBLE
                progressBar.setProgress(progress.toInt())
                textViewProgress.text ="$progress %"
            }
        }catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

    }

}




