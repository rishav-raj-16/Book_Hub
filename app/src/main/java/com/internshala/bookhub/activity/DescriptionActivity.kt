package com.internshala.bookhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.bookhub.R
import com.internshala.bookhub.database.BookDatabase
import com.internshala.bookhub.database.BookEntity
import com.internshala.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btAddFav: Button
    lateinit var toolbar: Toolbar
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var bookId: String? = "100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btAddFav = findViewById(R.id.btAddFav)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Description"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            Toast.makeText(
                this@DescriptionActivity,
                "Some unaepected error occurred",
                Toast.LENGTH_LONG
            ).show()
        }

        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unaepected error occurred",
                Toast.LENGTH_LONG
            ).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                    Response.Listener {
                        try {
                            val success = it.getBoolean("success")
                            if (success) {
                                progressLayout.visibility = View.GONE
                                val bookJasonObjet = it.getJSONObject("book_data")

                                val bookImgUrl = bookJasonObjet.getString("image")
                                Picasso.get().load(bookJasonObjet.getString("image"))
                                    .into(imgBookImage)
                                txtBookName.text = bookJasonObjet.getString("name")
                                txtBookAuthor.text = bookJasonObjet.getString("author")
                                txtBookPrice.text = bookJasonObjet.getString("price")
                                txtBookRating.text = bookJasonObjet.getString("rating")
                                txtBookDesc.text = bookJasonObjet.getString("description")

                                val bookEntity = BookEntity(
                                    bookId?.toInt() as Int,
                                    txtBookName.text.toString(),
                                    txtBookAuthor.text.toString(),
                                    txtBookPrice.text.toString(),
                                    txtBookRating.text.toString(),
                                    txtBookDesc.text.toString(),
                                    bookImgUrl
                                )

                                GlobalScope.launch {
                                    val isFav = DBCoroutineHelper(applicationContext, bookEntity, 1).execute()

                                    withContext(Dispatchers.Main) {
                                        if (isFav) {
                                            btAddFav.text = "Remove from Favourites"
                                            val favColor = ContextCompat.getColor(applicationContext, R.color.colorFavourite)
                                            btAddFav.setBackgroundColor(favColor)
                                        } else {
                                            btAddFav.text = "Add to Favourites"
                                            val favColor = ContextCompat.getColor(applicationContext, R.color.toolbar)
                                            btAddFav.setBackgroundColor(favColor)
                                        }
                                    }
                                }



                                btAddFav.setOnClickListener {
                                    GlobalScope.launch {
                                        val isBookInFavorites = DBCoroutineHelper(applicationContext, bookEntity, 1).execute()

                                        if (!isBookInFavorites) {
                                            // Book is not in favorites, add it
                                            val insertResult = DBCoroutineHelper(applicationContext, bookEntity, 2).execute()

                                            withContext(Dispatchers.Main) {
                                                if (insertResult) {
                                                    Toast.makeText(
                                                        this@DescriptionActivity,
                                                        "Book is added to favourites",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    btAddFav.text = "Remove from Favourites"
                                                    val favColor = ContextCompat.getColor(applicationContext, R.color.colorFavourite)
                                                    btAddFav.setBackgroundColor(favColor)

                                                } else {
                                                    Toast.makeText(
                                                        this@DescriptionActivity,
                                                        "Something went wrong",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                        } else {
                                            // Book is in favorites, remove it
                                            val deleteResult = DBCoroutineHelper(applicationContext, bookEntity, 3).execute()

                                            withContext(Dispatchers.Main) {
                                                if (deleteResult) {
                                                    Toast.makeText(
                                                        this@DescriptionActivity,
                                                        "Book is removed from favourites",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    btAddFav.text = "Add to Favourites"
                                                    val favColor = ContextCompat.getColor(applicationContext, R.color.toolbar)
                                                    btAddFav.setBackgroundColor(favColor)

                                                } else {
                                                    Toast.makeText(
                                                        this@DescriptionActivity,
                                                        "Something went wrong",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                }


                            } else {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "Some unexpected error occurred",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some unexpected error occurred",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Volley Error $it",
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "df69e6c858fa4f"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Setting") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBCoroutineHelper(
        context: Context,
        private val bookEntity: BookEntity,
        private val mode: Int
    ) {

        private val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()

        suspend fun execute(): Boolean = withContext(Dispatchers.IO) {
            when (mode) {
                1 -> {
                    // Check DB if the book is in favourites or not
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id)
                    closeDatabase()
                    return@withContext book != null
                }

                2 -> {
                    // Save the book in DB as favourites
                    db.bookDao().insertBook(bookEntity)
                    closeDatabase()
                    return@withContext true
                }

                3 -> {
                    // Remove the book from the favourites
                    db.bookDao().deleteBook(bookEntity)
                    closeDatabase()
                    return@withContext true
                }
            }

            return@withContext false
        }

        suspend fun closeDatabase() {
            withContext(Dispatchers.IO) {
                db.close()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }
}