package com.example.foodapit1ddm.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foodapit1ddm.R
import com.example.foodapit1ddm.databinding.FragmentFavoritesBinding
import com.example.foodapit1ddm.model.Recipe

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    // Lista de receitas favoritas
    private val favoriteRecipes = listOf(
        Recipe("Batata Frita", "Por 100g - Cal: 86kcal | Gord: 0,05g | Carbs: 20,12g | Prot: 1,57g"),
        Recipe("Purê de Batata", "Por 100g - Cal: 90kcal | Gord: 0,1g | Carbs: 18g | Prot: 2g")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = object : ArrayAdapter<Recipe>(
            requireContext(),
            R.layout.item_recipe,
            R.id.tvRecipeName,
            favoriteRecipes
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView ?: layoutInflater.inflate(R.layout.item_recipe, parent, false)
                val recipe = favoriteRecipes[position]
                row.findViewById<TextView>(R.id.tvRecipeName).text = recipe.name
                row.findViewById<TextView>(R.id.tvNutritionalInfo).text = recipe.nutrition
                return row
            }
        }

        binding.lvFavorites.adapter = adapter

        binding.lvFavorites.setOnItemClickListener { _, _, position, _ ->
            val recipe = favoriteRecipes[position]
            val bundle = Bundle().apply {
                putParcelable("recipe", recipe)
            }

            findNavController().navigate(
                R.id.action_favoritesFragment_to_recipeDetailFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}