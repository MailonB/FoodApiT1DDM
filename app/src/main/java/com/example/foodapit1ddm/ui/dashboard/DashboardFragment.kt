package com.example.foodapit1ddm.ui.dashboard

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foodapit1ddm.databinding.FragmentDashboardBinding
import com.example.foodapit1ddm.model.RecipeResponse // Importe sua classe de modelo
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val recipeList = mutableListOf<String>()
    private lateinit var recipeArrayAdapter: ArrayAdapter<String>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firestore = FirebaseFirestore.getInstance("receitas")

        setupArrayAdapter()
        fetchRecipes()

        return root
    }
    private fun setupArrayAdapter() {
        context?.let {
            recipeArrayAdapter = ArrayAdapter(it, R.layout.simple_list_item_1, recipeList)
        }
    }
    private fun fetchRecipes() {
        firestore.collection("recipes")
            .get()
            .addOnSuccessListener { querySnapshot ->
                recipeList.clear()

                for (document in querySnapshot.documents) {
                    val recipe = document.toObject(RecipeResponse::class.java)
                    recipe?.let {
                        it.recipe_name?.let { name -> recipeList.add(name) }
                    }
                }
                recipeArrayAdapter.notifyDataSetChanged()
                Toast.makeText(context, "Receitas carregadas com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("DashboardFragment", "Erro ao buscar receitas: ${e.message}", e)
                Toast.makeText(context, "Erro ao carregar receitas: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}