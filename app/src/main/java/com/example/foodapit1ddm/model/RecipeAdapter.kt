package com.example.foodapit1ddm.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.foodapit1ddm.R // Importe seu R para acessar os IDs do layout
import com.example.foodapit1ddm.Api.Food // Importe sua classe de modelo Food

class RecipeAdapter(
    context: Context,
    private val foods: List<Food> // A lista de objetos Food que o adapter irá exibir
) : ArrayAdapter<Food>(context, 0, foods) { // Passa 0 como resource ID pois vamos inflar nosso próprio layout

    // ViewHolder pattern para otimizar o desempenho da ListView,
    // evitando chamadas repetidas a findViewById()
    private class ViewHolder(
        val foodNameTextView: TextView,
        val brandNameTextView: TextView,
        val foodDescriptionTextView: TextView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        // Se convertView é null, significa que estamos criando um novo item de lista pela primeira vez.
        // Precisamos inflar o layout e criar o ViewHolder.
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false)
            viewHolder = ViewHolder(
                view.findViewById(R.id.tvFoodName),
                view.findViewById(R.id.tvBrandName),
                view.findViewById(R.id.tvFoodDescription)
            )
            // Armazenamos o viewHolder no tag da view para reutilização.
            view.tag = viewHolder
        } else {
            // Se convertView não é null, significa que estamos reciclando uma view existente.
            // Podemos simplesmente recuperar o ViewHolder que armazenamos anteriormente.
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // Pega o objeto Food da lista na posição atual
        val foodItem = getItem(position)

        // Preenche as views dentro do item da lista com os dados do objeto Food
        if (foodItem != null) {
            viewHolder.foodNameTextView.text = foodItem.food_name ?: "Nome não disponível"

            // Mostra o nome da marca apenas se ele existir e não estiver vazio
            if (!foodItem.brand_name.isNullOrEmpty()) {
                viewHolder.brandNameTextView.text = "Marca: ${foodItem.brand_name}"
                viewHolder.brandNameTextView.visibility = View.VISIBLE
            } else {
                viewHolder.brandNameTextView.visibility = View.GONE // Esconde se não houver marca
            }

            viewHolder.foodDescriptionTextView.text =
                foodItem.food_description ?: "Descrição não disponível"
        }

        return view // Retorna a view configurada para ser exibida na ListView
    }
}



