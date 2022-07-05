package com.ozankocak.personelbilgi

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.ozankocak.personelbilgi.databinding.ActivityProfilBinding
import java.io.ByteArrayOutputStream
import java.util.jar.Manifest

class Profil : AppCompatActivity() {

    private lateinit var binding : ActivityProfilBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var bitmap : Bitmap? = null
    private lateinit var db : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = this.openOrCreateDatabase("PersonelBilgileri", MODE_PRIVATE,null)
        launchers()

        val intent = intent
        val veri = intent.getStringExtra("veri")

        if(veri.equals("yeni")) {
            binding.imageView.setImageResource(R.drawable.image)
            binding.editTextisim.setText("")
            binding.editTextDepartman.setText("")
            binding.editTextMaas.setText("")
            binding.button.visibility = View.VISIBLE

        } else {
            binding.button.visibility = View.INVISIBLE
            val idDetay = intent.getIntExtra("id",0)

            val cursor = db.rawQuery("SELECT * FROM personel WHERE id = ?", arrayOf(idDetay.toString()))

            val adSoyadIndex = cursor.getColumnIndex("adsoyad")
            val departmanIndex = cursor.getColumnIndex("departman")
            val maasIndex = cursor.getColumnIndex("maas")
            val imageIndex = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {

                binding.editTextisim.setText(cursor.getString(adSoyadIndex))
                binding.editTextDepartman.setText(cursor.getString(departmanIndex))
                binding.editTextMaas.setText(cursor.getString(maasIndex))

                val byteArray = cursor.getBlob(imageIndex)
                val bitmapFactory = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmapFactory)

            }

            cursor.close()

        }

    }

    fun kaydet(view : View){
        var adSoyad = binding.editTextisim.text.toString()
        var departman = binding.editTextDepartman.text.toString()
        var maas = binding.editTextMaas.text.toString()

        if(bitmap != null) {
            var kucukBitmap = bitmapKucult(bitmap!!, maxSize = 200)

            var outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
            val byteArray = outputStream.toByteArray()

            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS personel (id INTEGER PRIMARY KEY, adsoyad VARCHAR, departman VARCHAR, maas VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO personel (adsoyad, departman, maas, image) VALUES (?,?,?,?)"
                val statement = db.compileStatement(sqlString)
                statement.bindString(1,adSoyad)
                statement.bindString(2,departman)
                statement.bindString(3,maas)
                statement.bindBlob(4,byteArray)
                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent = Intent(this@Profil,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    fun bitmapKucult(image : Bitmap, maxSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        var oran : Double = width.toDouble() / height.toDouble()

        if(oran > 1) {
            width = maxSize
            val newHeight = width / oran
            height = newHeight.toInt()

        } else {
            height = maxSize
            val newWidth = height * oran
            width = newWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    fun image(view : View) {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentGallery)
        }
        }

    fun launchers() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                val intentResult = result.data
                if(intentResult != null) {
                    val image = intentResult.data
                    if (image != null){
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source =  ImageDecoder.createSource(this@Profil.contentResolver, image)
                                bitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(bitmap)

                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,image)
                                binding.imageView.setImageBitmap(bitmap)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if(result) {
                val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentGallery)

            } else {
                Toast.makeText(this@Profil,"Galeri erişim izni gerekli!", Toast.LENGTH_LONG).show()
            }
    }

}}