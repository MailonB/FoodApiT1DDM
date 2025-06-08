package com.example.foodapit1ddm.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.foodapit1ddm.R
import com.bumptech.glide.Glide

class RecipeAdapter(
    context: Context,
    private val recipes: List<RecipeResponse>
) : ArrayAdapter<RecipeResponse>(context, 0, recipes) {

    private class ViewHolder(
        val recipeNameTextView: TextView,
        val recipeDescriptionTextView: TextView,
        val recipeImageView: ImageView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_receita, parent, false)
            viewHolder = ViewHolder(
                view.findViewById(R.id.tvRecipeName),
                view.findViewById(R.id.tvRecipeDescription),
                view.findViewById(R.id.ivRecipeImage)
            )
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val recipeItem = getItem(position)

        if (recipeItem != null) {
            viewHolder.recipeNameTextView.text = recipeItem.recipe_name ?: "Nome não disponível"
            viewHolder.recipeDescriptionTextView.text = recipeItem.recipe_description ?: "Descrição não disponível"

            if (!recipeItem.recipe_image.isNullOrEmpty()) {
                Glide.with(context)
                    .load(recipeItem.recipe_image)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(viewHolder.recipeImageView)
                viewHolder.recipeImageView.visibility = View.VISIBLE
            } else {
                viewHolder.recipeImageView.visibility = View.GONE
                //colocar uma imagem padrão aqui
            }

        }

        return view
    }
}