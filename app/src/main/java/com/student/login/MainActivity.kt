package com.student.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    var codeSent: String? = null
    lateinit var refphone : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        refphone = FirebaseDatabase.getInstance().getReference().child("phone")
        //refphone.setValue("77760")

        btnlogin.visibility = View.INVISIBLE



        buttonGetVerificationCode.setOnClickListener{

            if (checkSelfPermission(
                            REQUESTED_PERMISSIONS.get(0),
                            PERMISSION_REQ_ID
                    ) &&
                    checkSelfPermission(
                            REQUESTED_PERMISSIONS.get(1),
                            PERMISSION_REQ_ID
                    )
                    &&
                    checkSelfPermission(
                            REQUESTED_PERMISSIONS.get(2),
                            PERMISSION_REQ_ID
                    )
            ){
                refphone.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        val novalues = p0!!.value.toString()
                        if (editTextPhone.text.toString().isEmpty()) {
                            editTextPhone.error = "Phone number is required"
                            editTextPhone.requestFocus()
                            return
                        }
                        if (editTextPhone.text.toString().length < 10) {
                            editTextPhone.error = "Please enter a valid phone"
                            editTextPhone.requestFocus()
                            return
                        }
                        if (novalues.contains(editTextPhone.text.toString())){
                            btnlogin.visibility = View.VISIBLE
                            editTextPhone.visibility = View.INVISIBLE
                            buttonGetVerificationCode.visibility = View.INVISIBLE
                            editTextCode.visibility = View.INVISIBLE
                            buttonSignIn.visibility = View.INVISIBLE
                            Showuser()
                        }else{
                            sendVerificationCode()
                        }
                    }
                })
            }
        }

        buttonSignIn.setOnClickListener{

            refphone.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {
                }
                override fun onDataChange(p0: DataSnapshot?) {
                    val novalues = p0!!.value.toString()
                    if (editTextPhone.text.toString().isEmpty()) {
                        editTextPhone.error = "Phone number is required"
                        editTextPhone.requestFocus()
                        return
                    }
                    if (editTextPhone.text.toString().length < 10) {
                        editTextPhone.error = "Please enter a valid phone"
                        editTextPhone.requestFocus()
                        return
                    }
                    if (novalues.contains(editTextPhone.text.toString())){
                        btnlogin.visibility = View.VISIBLE
                        editTextPhone.visibility = View.INVISIBLE
                        buttonGetVerificationCode.visibility = View.INVISIBLE
                        editTextCode.visibility = View.INVISIBLE
                        buttonSignIn.visibility = View.INVISIBLE
                        Showuser()
                    }else{
                        verifySignInCode()
                    }
                }
            })

        }

        btnlogin.setOnClickListener{
            startActivity(Intent( this, Profile::class.java))
        }
    }


    private fun Showuser() {
        Toast.makeText(this, "${editTextPhone.text.toString()} User Exist Please Log In", Toast.LENGTH_LONG).show()
    }

    private fun verifySignInCode() {
        val code = editTextCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(codeSent!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    //here you can open new activity
                    Toast.makeText(
                        applicationContext,
                        "Login Successfull", Toast.LENGTH_LONG
                    ).show()
                    refphone.child(editTextPhone.text.toString()).setValue(editTextPhone.text.toString())
                    startActivity(Intent( this, Profile::class.java))
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            applicationContext,
                            "Incorrect Verification Code ", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i(
                MainActivity.LOG_TAG,
                "checkSelfPermission $permission $requestCode"
        )
        if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                )
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    MainActivity.REQUESTED_PERMISSIONS,
                    requestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>, grantResults: IntArray
    ) {
        Log.i(
                MainActivity.LOG_TAG,
                "onRequestPermissionsResult " + grantResults[0] + " " + requestCode
        )
        when (requestCode) {
            MainActivity.PERMISSION_REQ_ID -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(
                            MainActivity.LOG_TAG,
                            "Need permissions " + Manifest.permission.INTERNET + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE  + Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
        }
    }


    private fun sendVerificationCode() {
        val phone = editTextPhone.text.toString()
        if (phone.isEmpty()) {
            editTextPhone.error = "Phone number is required"
            editTextPhone.requestFocus()
            return
        }
        if (phone.length < 10) {
            editTextPhone.error = "Please enter a valid phone"
            editTextPhone.requestFocus()
            return
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallbacks
        ) // OnVerificationStateChangedCallbacks
    }

    var mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {}
            override fun onVerificationFailed(e: FirebaseException) {}
            override fun onCodeSent(
                s: String,
                forceResendingToken: ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                codeSent = s
            }
        }

    companion object {

        private val PERMISSION_REQ_ID = 22
        private val REQUESTED_PERMISSIONS = arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
        private val LOG_TAG: String = MainActivity::class.java.getSimpleName()


    }

}