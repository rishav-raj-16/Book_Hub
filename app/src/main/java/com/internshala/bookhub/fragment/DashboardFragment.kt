package com.internshala.bookhub.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.bookhub.R
import com.internshala.bookhub.adaptor.DashboardRecyclerAdaptor
import com.internshala.bookhub.database.BookEntity
import com.internshala.bookhub.models.Book
import com.internshala.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.Collections


class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdaptor: DashboardRecyclerAdaptor
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var bookInfoList: ArrayList<Book> = ArrayList()

    var ratingComparator = Comparator<Book> { book1, book2 ->
        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)


//      Check the internet connection using The dialog box

        val queue = Volley.newRequestQueue(requireContext())

        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if (ConnectionManager().checkConnectivity(requireContext())) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener
                {
//                Here is the code for Response from the Server
//                    println("Response is $it")
                    try {
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJasonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJasonObject.getString("book_id"),
                                    bookJasonObject.getString("name"),
                                    bookJasonObject.getString("author"),
                                    bookJasonObject.getString("rating"),
                                    bookJasonObject.getString("price"),
                                    bookJasonObject.getString("image")
                                )

                                bookInfoList.add(bookObject)
                                recyclerAdaptor =
                                    DashboardRecyclerAdaptor(activity as Context, bookInfoList)

                                recyclerDashboard.adapter = recyclerAdaptor

                                recyclerDashboard.layoutManager = layoutManager


                            }
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            requireContext(),
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(requireContext(), "Volley error occurred!!!", Toast.LENGTH_LONG)
                        .show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Setting") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(requireActivity())
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.action_sort) {
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()
        }

        recyclerAdaptor.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}


//                               recyclerDashboard.addItemDecoration(
//                                    DividerItemDecoration(
//                                        recyclerDashboard.context,
//                                        (layoutManager as LinearLayoutManager).orientation
//                                    )
//                                )

//        Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
//        Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
//        Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
//        Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
//        Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
//        Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
//        Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
//        Book(
//            "The Adventures of Huckleberry Finn",
//            "Mark Twain",
//            "Rs. 699",
//            "4.5",
//            R.drawable.adventures_finn
//        ),
//        Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
//        Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
//    )


//lateinit var btCheckInternet: Button
//        btCheckInternet = view.findViewById(R.id.btCheckInternet
//btCheckInternet.setOnClickListener {
//    if (ConnectionManager().checkConnectivity(activity as Context)) {
//        val dialog = AlertDialog.Builder(activity as Context)
//        dialog.setTitle("Success")
//        dialog.setMessage("Internet Connection Found")
//        dialog.setPositiveButton("Ok") { text, listener ->
//
//        }
//
//        dialog.setNegativeButton("Cancel") { text, listener ->
//
//        }
//        dialog.create()
//        dialog.show()
//    } else {
//        val dialog = AlertDialog.Builder(activity as Context)
//        dialog.setTitle("Error")
//        dialog.setMessage("Internet Connection Not Found")
//        dialog.setPositiveButton("Ok") { text, listener ->
//
//        }
//
//        dialog.setNegativeButton("Cancel") { text, listener ->
//
//        }
//        dialog.create()
//        dialog.show()
//    }
//}
