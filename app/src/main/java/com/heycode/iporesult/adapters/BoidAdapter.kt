package com.heycode.iporesult.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heycode.iporesult.R
import de.hdodenhof.circleimageview.CircleImageView

class BoidAdapter(
    private val names: Array<String>,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<BoidAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.boid_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item1 = names[position]

        // sets the image to the imageview from our itemHolder class
        holder.image.setImageResource(R.drawable.logo)
//        Glide
//            .with(context)
//            .load(LOGO_IMG)
//            .fitCenter()
//            .placeholder(R.drawable.logo)
//            .into(holder.image)

        // sets the text to the textview from our itemHolder class
        holder.title.text = item1
        holder.subtitle.text = item1
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return names.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val image: CircleImageView = ItemView.findViewById(R.id.ivBoidPic)
        val title: TextView = ItemView.findViewById(R.id.tvBoidTitle)
        val subtitle: TextView = ItemView.findViewById(R.id.tvBoidSubtitle)

        init {
            ItemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
            ItemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(position)
                }
                return@setOnLongClickListener true
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }
}