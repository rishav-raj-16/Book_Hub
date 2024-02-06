package com.internshala.bookhub.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.bookhub.R
import com.internshala.bookhub.adaptor.FavouriteRecyclerAdapter
import com.internshala.bookhub.database.BookDatabase
import com.internshala.bookhub.database.BookEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouritesFragment : Fragment() {

    private lateinit var recyclerFavourites: RecyclerView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    private var dbBookList = listOf<BookEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFavourites = view.findViewById(R.id.recyclerFavourites)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = GridLayoutManager(activity, 2)

        // Use lifecycleScope.launch instead of GlobalScope.launch
        lifecycleScope.launch {
            try {
                // Switch to IO dispatcher for database operation
                dbBookList = withContext(Dispatchers.IO) {
                    RetrieveFavourites(activity as Context).execute()
                }

                // Update UI elements on the main thread
                progressLayout.visibility = View.GONE
                recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbBookList)
                recyclerFavourites.adapter = recyclerAdapter
                recyclerFavourites.layoutManager = layoutManager
            } catch (e: Exception) {
                // Handle exceptions
                e.printStackTrace()
            }
        }

        return view
    }

    class RetrieveFavourites(val context: Context) {
        suspend fun execute(): List<BookEntity> = withContext(Dispatchers.IO) {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()
            return@withContext db.bookDao().getAllBooks()
        }
    }
}
