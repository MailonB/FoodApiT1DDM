//package com.example.foodapit1ddm.ui.favorites
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.example.foodapit1ddm.R
//import com.example.foodapit1ddm.databinding.FragmentFavoritesBinding
//
//class FavoritesFragment : Fragment() {
//
//    private var _binding: FragmentFavoritesBinding? = null
//    private val binding get() = _binding!!
//
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//
//
//        binding.lvFavorites.setOnItemClickListener { _, _, position, _ ->
//            val bundle = Bundle().apply {
//                putParcelable("recipe", recipe)
//            }
//
//            findNavController().navigate(
//                R.id.action_favoritesFragment_to_recipeDetailFragment,
//                bundle
//            )
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}