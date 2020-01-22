package com.pradeep.factsdemoapp.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pradeep.factsdemoapp.R
import com.pradeep.factsdemoapp.adapter.FactsListAdapter
import com.pradeep.factsdemoapp.api.Api
import com.pradeep.factsdemoapp.model.Facts
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import android.widget.ProgressBar
import android.widget.TextView
import com.pradeep.factsdemoapp.presenter.FactsListPresenter
import com.pradeep.factsdemoapp.utils.CommonOperations
import kotlinx.android.synthetic.main.activity_facts.*

class FactsListActivity : AppCompatActivity(), FactsListPresenter {

    private var retrofitAdapter: FactsListAdapter? = null
    private var recyclerView: RecyclerView? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facts)

        recyclerView = findViewById(R.id.recycler)
        val postProgressBar: ProgressBar = this.progressBar
        val postProgressBarText: TextView = this.progressBarText
        getSupportActionBar()?.setTitle(R.string.title);
        // Check if network is available or not
        if (CommonOperations.isNetworkAvailable(this)) {
            fetchFacts()
        } else {
            Toast.makeText(
                this,
                R.string.no_internet_connection,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.facts -> if (CommonOperations.isNetworkAvailable(this)) {
                getSupportActionBar()?.setTitle(R.string.title);
                recyclerView?.removeAllViewsInLayout();
                fetchFacts()
            } else {
                Toast.makeText(
                    this,
                    R.string.no_internet_connection,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun fetchFacts() {
        // display the indefinite progressbar
        this@FactsListActivity.runOnUiThread(java.lang.Runnable {
            progressBar.visibility = View.VISIBLE
            progressBarText.visibility = View.VISIBLE
        })
        val BASE_URL = "https://dl.dropboxusercontent.com"
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .method(original.method(), original.body())
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()
        val api: Api = retrofit.create(Api::class.java)
        // Call API to get list of facts along with title, description and image if available
        val call: Call<String?>? = api.getFacts()
        call?.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        // when the task is completed, make progressBar gone
                        this@FactsListActivity.runOnUiThread(java.lang.Runnable {
                            progressBar.visibility = View.GONE
                            progressBarText.visibility = View.GONE
                        })
                        val jsonresponse = response.body().toString()
                        writeRecycler(jsonresponse)
                    } else {
                        Toast.makeText(this@FactsListActivity, R.string.no_results_found, Toast.LENGTH_LONG).show();
                        this@FactsListActivity.runOnUiThread(java.lang.Runnable {
                            progressBar.visibility = View.GONE
                            progressBarText.visibility = View.GONE
                        })
                    }
                } else {
                    Toast.makeText(this@FactsListActivity, R.string.no_results_found, Toast.LENGTH_LONG).show();
                    this@FactsListActivity.runOnUiThread(java.lang.Runnable {
                        progressBar.visibility = View.GONE
                        progressBarText.visibility = View.GONE
                    })
                }
            }
            override fun onFailure(
                call: Call<String?>,
                t: Throwable
            ) {
            }
        })
    }

    private fun writeRecycler(response: String) {
        try {
            val obj = JSONObject(response)

                val modelRecyclerArrayList: ArrayList<Facts> = ArrayList<Facts>()
                val title = obj.getString("title")
                getSupportActionBar()?.setTitle(title);
                val dataArray = obj.getJSONArray("rows")
                for (i in 0 until dataArray.length()) {
                    val modelRecycler = Facts()
                    val dataobj = dataArray.getJSONObject(i)

                    val imageHref = dataobj.getString("imageHref")
                    val title = dataobj.getString("title")
                    val description = dataobj.getString("description")

                    if(imageHref != null && !imageHref.isEmpty() && !imageHref.equals("null")) {
                        modelRecycler?.factsImgURL = imageHref
                    }
                    if(title != null && !title.isEmpty() && !title.equals("null")) {
                        modelRecycler?.factsTitle = title
                    }
                    if(description != null && !description.isEmpty() && !description.equals("null")) {
                        modelRecycler?.factsDescription = description
                    }
                    modelRecyclerArrayList.add(modelRecycler)
                }
                retrofitAdapter = FactsListAdapter(this, modelRecyclerArrayList)
                recyclerView?.setAdapter(retrofitAdapter)
                recyclerView?.setLayoutManager(
                    LinearLayoutManager(
                        getApplicationContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                )

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}