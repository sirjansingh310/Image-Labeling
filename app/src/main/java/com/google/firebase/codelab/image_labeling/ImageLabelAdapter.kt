package com.google.firebase.codelab.image_labeling

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import kotlinx.android.synthetic.main.item_row.view.*
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class ImageLabelAdapter(private var firebaseVisionList: List<Any>) : RecyclerView.Adapter<ImageLabelAdapter.ItemHolder>() {
    lateinit var context: Context

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindCloud(currentItem: FirebaseVisionCloudLabel) {
            itemView.setOnClickListener(View.OnClickListener {
                //itemView.itemName.text = "clicked"
                val modifiedLabel = currentItem.label.replace(" ","_")
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://en.wikipedia.org/wiki/"+modifiedLabel))
                startActivity(context,i,null)

            })
            itemView.itemName.text = currentItem.label + "detected on cloud"
            itemView.itemAccuracy.text = "Probability : ${(currentItem.confidence * 100).toInt()}%"
        }

        fun bindDevice(currentItem: FirebaseVisionLabel) {
            itemView.setOnClickListener(View.OnClickListener {
             //   itemView.itemName.text = "clicked"
                val modifiedLabel = currentItem.label.replace(" ","_")
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://zeen.wikipedia.org/wiki/"+modifiedLabel))
                startActivity(context,i,null)

            })
            itemView.itemName.text = currentItem.label + "detected on device!"
            itemView.itemAccuracy.text = "Probability : ${(currentItem.confidence * 100).toInt()}%"
        }

    }

    fun setList(visionList: List<Any>) {
        firebaseVisionList = visionList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem = firebaseVisionList[position]
        if (currentItem is FirebaseVisionCloudLabel)
            holder.bindCloud(currentItem)
        else
            holder.bindDevice(currentItem as FirebaseVisionLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        context = parent.context
        return ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_row, parent, false))
    }

    override fun getItemCount() = firebaseVisionList.size
}