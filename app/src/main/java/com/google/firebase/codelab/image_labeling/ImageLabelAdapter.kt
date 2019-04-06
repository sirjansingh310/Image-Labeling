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
            val isExpanded = HashMap<String,Boolean>(firebaseVisionList.size)
            itemView.setOnClickListener(View.OnClickListener {
                if(isExpanded[currentItem.label] == false) {
                    itemView.wikiInfo.text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
                    itemView.wikiInfo.visibility = View.VISIBLE
                    itemView.wikiButton.visibility = View.VISIBLE
                    isExpanded[currentItem.label] = true
                    itemView.wikiButton.setOnClickListener {
                        val modifiedLabel = currentItem.label.replace(" ","_")
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://en.wikipedia.org/wiki/"+modifiedLabel))
                        startActivity(context,i,null)
                    }
                }
                else{
                    itemView.wikiInfo.visibility = View.GONE
                    itemView.wikiButton.visibility = View.GONE
                    isExpanded[currentItem.label] = false
                }

            })
            isExpanded[currentItem.label] = false
            itemView.itemName.text = currentItem.label + "detected on cloud"
            itemView.itemAccuracy.text = "Probability : ${(currentItem.confidence * 100).toInt()}%"
        }

        fun bindDevice(currentItem: FirebaseVisionLabel) {
            // on-device
            itemView.itemName.text = currentItem.label + "detected on cloud"
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
