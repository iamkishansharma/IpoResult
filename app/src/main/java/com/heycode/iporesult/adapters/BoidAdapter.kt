package com.heycode.iporesult.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heycode.iporesult.R
import com.heycode.iporesult.databinding.BoidItemBinding
import de.hdodenhof.circleimageview.CircleImageView

class BoidAdapter(
    private val names: Array<String>,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<BoidAdapter.ViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val binding = BoidItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item1 = names[position]

        // sets the image to the imageview from our itemHolder class
        holder.apply {
            image.setImageResource(R.drawable.logo)
            title.text = item1
            subtitle.text = item1
        }
//        Glide.with(context).load(LOGO_IMG).fitCenter()
//            .placeholder(R.drawable.logo)
//            .into(holder.image)

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return names.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(binding: BoidItemBinding) : RecyclerView.ViewHolder(binding.root),View.OnClickListener {
        val image: CircleImageView = binding.ivBoidPic
        val title: TextView = binding.tvBoidTitle
        val subtitle: TextView =  binding.tvBoidSubtitle

        init {
            binding.clRecentItem.setOnClickListener (this)
            binding.clRecentItem.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(position)
                }
                return@setOnLongClickListener true
            }
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }
}