package com.example.foodapit1ddm.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapit1ddm.R
import com.example.foodapit1ddm.model.Food

class FoodAdapter(
    private var foodList: List<Food> = emptyList(),
    private val onItemClick: (Food) -> Unit = {}
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodName: TextView = itemView.findViewById(R.id.tv_food_name)
        private val brandName: TextView = itemView.findViewById(R.id.tv_brand_name)
        private val foodType: TextView = itemView.findViewById(R.id.tv_food_type)
        private val foodDescription: TextView = itemView.findViewById(R.id.tv_food_description)

        fun bind(food: Food) {
            foodName.text = food.food_name
            brandName.text = food.brand_name ?: "Marca não informada"
            foodType.text = food.food_type
            foodDescription.text = food.food_description

            itemView.setOnClickListener {
                onItemClick(food)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount(): Int = foodList.size

    fun updateFoodList(newFoodList: List<Food>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }

    fun addFood(food: Food) {
        val mutableList = foodList.toMutableList()
        mutableList.add(food)
        foodList = mutableList
        notifyItemInserted(foodList.size - 1)
    }

    fun removeFood(position: Int) {
        if (position < foodList.size) {
            val mutableList = foodList.toMutableList()
            mutableList.removeAt(position)
            foodList = mutableList
            notifyItemRemoved(position)
        }
    }
}