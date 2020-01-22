package com.pradeep.factsdemoapp.adapter

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pradeep.factsdemoapp.R
import com.pradeep.factsdemoapp.model.Facts
import java.util.*


class FactsListAdapter(ctx: Context?, dataModelArrayList: ArrayList<Facts>) : RecyclerView.Adapter<FactsListAdapter.MyViewHolder?>() {

    private val inflater: LayoutInflater
    private val dataModelArrayList: ArrayList<Facts>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = inflater.inflate(R.layout.facts_cardview, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int
    ) {
        var path = dataModelArrayList[position].factsImgURL
        var title = dataModelArrayList[position].factsTitle
        var description = dataModelArrayList[position].factsDescription
        if(path != null && !path.isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(path).into(holder.factsImgURL);
        } else {
            holder.factsImgURL.setVisibility(View.GONE)
        }
        if(title != null && !title.isEmpty()) {
            holder.factsTitle.setText(title)
        } else {
            holder.factsTitle.setVisibility(View.GONE)
        }
        if(description != null && !description.isEmpty()) {
            holder.factsDescription.setText(description)
        } else {
            holder.factsDescription.setVisibility(View.GONE)
        }
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var factsImgURL: ImageView
        var factsTitle: TextView
        var factsDescription: TextView

        init {
            factsImgURL = itemView.findViewById<View>(R.id.factsImgURL) as ImageView
            factsTitle = itemView.findViewById<View>(R.id.factsTitle) as TextView
            factsTitle.setMovementMethod(ScrollingMovementMethod())
            factsDescription = itemView.findViewById<View>(R.id.factsDescription) as TextView
            factsDescription.setMovementMethod(ScrollingMovementMethod())
        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        this.dataModelArrayList = dataModelArrayList
    }

    override fun getItemCount(): Int {
        return dataModelArrayList.size
    }
}