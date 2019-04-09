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
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import java.net.*
import java.io.*
import java.util.regex.*

class ImageLabelAdapter(private var firebaseVisionList: List<Any>) : RecyclerView.Adapter<ImageLabelAdapter.ItemHolder>() {
    lateinit var context: Context
    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun extractInfo(inputLabel: String) : String{

            try
            {
                    var label = inputLabel
                    var length = label.length
                    var testLine = ""
                    label = label.replace((" ").toRegex(), "%20")
                      var inputLine = ""
                      val url = "https://en.wikipedia.org/w/api.php?action=opensearch&format=json&search=" + label + "&namespace=0%7C4&limit=1&profile=classic&redirects=resolve"
                      val wiki = URL(url)
                      val `in` = BufferedReader(InputStreamReader(wiki.openStream()))
                      inputLine = `in`.readLine()
                      testLine = inputLine.substring(length + 6, inputLine.indexOf("\"", length + 6))
                      val start = 10 + length + testLine.length
                      inputLine = inputLine.substring(start, inputLine.indexOf("]", start))
                      inputLine = inputLine.replace((Matcher.quoteReplacement("\\\"")).toRegex(), Matcher.quoteReplacement("\""))
                      return inputLine

            }

            catch (e:Exception) {
                   Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
            }
            return ""

        }

        fun bindCloud(currentItem: FirebaseVisionCloudLabel) {
            val isExpanded = HashMap<String,Boolean>(firebaseVisionList.size)
            itemView.setOnClickListener(View.OnClickListener {

                if(isExpanded[currentItem.label] == false) {
                    if(itemView.wikiInfo.text.equals("")) {
                        doAsyncResult {
                            itemView.wikiInfo.text = extractInfo(currentItem.label)
                        }
                        Thread.sleep(1500)
                    }
                    itemView.wikiInfo.visibility = View.VISIBLE
                    itemView.wikiButton.visibility = View.VISIBLE
                        isExpanded[currentItem.label] = true
                        itemView.wikiButton.setOnClickListener {
                            val modifiedLabel = currentItem.label.replace(" ", "_")
                            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://en.wikipedia.org/wiki/" + modifiedLabel))
                            startActivity(context, i, null)
                    }
                }

                else{
                    itemView.wikiInfo.visibility = View.GONE

                    itemView.wikiButton.visibility = View.GONE
                    isExpanded[currentItem.label] = false
                }

            })
            isExpanded[currentItem.label] = false
            itemView.itemName.text = currentItem.label
            itemView.itemAccuracy.text = "Probability : ${(currentItem.confidence * 100).toInt()}%"
        }

        fun bindDevice(currentItem: FirebaseVisionLabel) {
            // on-device
            itemView.itemName.text = currentItem.label
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
