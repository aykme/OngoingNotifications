package com.aykme.animenoti.ui.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.R
import com.aykme.animenoti.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory.getInstance(activity?.application as AnimeNotiApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.favoritesRecyclerView
        val layoutManager = GridLayoutManager(requireContext(), 1)
        val adapter = FavoritesListAdapter(requireContext(), viewModel)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val placeholder = binding.favoritesPlaceholder
        placeholder.visibility = View.GONE
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setProgressViewOffset(false, 45, 245)
        swipeRefresh.setColorSchemeResources(R.color.pink)

        viewModel.apply {
            followedAnimeList.observe(viewLifecycleOwner) { followedAnimeList ->
                bindPlaceholder(placeholder, followedAnimeList.isNullOrEmpty())
                submitAnimeData(adapter, followedAnimeList)
            }
            swipeRefresh.setOnRefreshListener {
                refreshDatabaseItems()
                swipeRefresh.isRefreshing = false
            }

        }
    }
}