package com.ozankocak.personelbilgi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozankocak.personelbilgi.databinding.ActivityMainBinding
import com.ozankocak.personelbilgi.databinding.ActivityProfilBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var personelListe : ArrayList<Personel>
    private lateinit var personelAdapter: PersonelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        personelListe = ArrayList<Personel>()

        personelAdapter = PersonelAdapter(personelListe)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = personelAdapter

        getData()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun getData () {

        try {
                val db = this.openOrCreateDatabase("PersonelBilgileri", MODE_PRIVATE, null)

            val cursor = db.rawQuery("SELECT * FROM personel",null)

            val adSoyadIndex = cursor.getColumnIndex("adsoyad")
            val idIndex = cursor.getColumnIndex("id")

            while(cursor.moveToNext()) {
                val adsoyad = cursor.getString(adSoyadIndex)
                val id = cursor.getInt(idIndex)
                val personel = Personel(adsoyad,id)
                personelListe.add(personel)

            }

            personelAdapter.notifyDataSetChanged()
            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.personelEkle) {
            val intent = Intent(this@MainActivity,Profil::class.java)
            intent.putExtra("veri", "yeni")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}