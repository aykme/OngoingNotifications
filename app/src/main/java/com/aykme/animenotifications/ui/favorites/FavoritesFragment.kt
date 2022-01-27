package com.aykme.animenotifications.ui.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aykme.animenotifications.Application
import com.aykme.animenotifications.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory.getInstance(activity?.application as Application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.favoritesRecyclerView
        val layoutManager = GridLayoutManager(requireContext(), 1)
        val adapter = FavoritesListAdapter(requireContext(), viewModel)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        viewModel.followedAnimeList.observe(viewLifecycleOwner) { followedAnimeList ->
            viewModel.submitAnimeData(adapter, followedAnimeList)
            adapter.submitFollowedAnimeList(followedAnimeList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}