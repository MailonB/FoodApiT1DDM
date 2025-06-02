package com.example.foodapit1ddm.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.foodapit1ddm.ui.auth.LoginActivity
import com.example.foodapit1ddm.R
import com.example.foodapit1ddm.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clique dos botões principais
        binding.btnSearch.setOnClickListener {
            // Navegação usando Navigation Component
            findNavController().navigate(R.id.action_home_to_recipes)
        }

        binding.btnFavorites.setOnClickListener {
            // Exemplo: navegar para favoritos
        }

        binding.btnCalories.setOnClickListener {
            // Exemplo: navegar para cálculo de calorias
        }

        // Adiciona o menu (ícone de login)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_login -> {

                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
