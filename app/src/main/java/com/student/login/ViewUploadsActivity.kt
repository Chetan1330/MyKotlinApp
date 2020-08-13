package com.student.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.viewactivity.*


class ViewUploadsActivity : AppCompatActivity() {
    lateinit var ref : DatabaseReference
    lateinit var fileList:MutableList<Datalist>
    lateinit var listview:ListView
    lateinit var refprivate : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewactivity)

        fileList = mutableListOf()
        listview = findViewById(R.id.listView)
        ref = FirebaseDatabase.getInstance().getReference("stempedia-abd43")
        refprivate = FirebaseDatabase.getInstance().getReference("stempedia-private")

        publicfile.setOnClickListener(View.OnClickListener {

            ref.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.exists()){
                        fileList.clear()
                        for (e in p0.children){
                            val employee = e.getValue(Datalist::class.java)
                            fileList.add(employee!!)
                        }
                        val adapter = filesAdapter(this@ViewUploadsActivity,R.layout.students,fileList)
                        listview.adapter = adapter
                    }
                }

            })
        })

        privatefile.setOnClickListener(View.OnClickListener {

            refprivate.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.exists()){
                        fileList.clear()
                        for (e in p0.children){
                            val employee = e.getValue(Datalist::class.java)
                            fileList.add(employee!!)
                        }
                        val adapter = filesAdapter(this@ViewUploadsActivity,R.layout.students,fileList)
                        listview.adapter = adapter
                    }
                }

            })
        })

    }
}