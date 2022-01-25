package com.aykme.animenotifications.ui.animelist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aykme.animenotifications.Application
import com.aykme.animenotifications.R
import com.aykme.animenotifications.databinding.FragmentAnimeListBinding
import com.aykme.animenotifications.ui.animelist.paging.PagingAnimeListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

const val ANIME_LIST_FRAGMENT_TAG = "AnimeListFragment"

class AnimeListFragment : Fragment() {
    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeListViewModel by viewModels {
        AnimeListViewModelFactory.getInstance(activity?.application as Application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.animeListRecyclerView
        val adapter = PagingAnimeListAdapter(requireContext(), viewModel)
        val layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        val upperMenu: BottomNavigationView = binding.upperMenu
        upperMenu.menu.findItem(R.id.ongoing_anime).setOnMenuItemClickListener {
            Log.d(ANIME_LIST_FRAGMENT_TAG, "Нажата кнопка: Онгоинги")
            it.isChecked = true
            return@setOnMenuItemClickListener true
        }
        upperMenu.menu.findItem(R.id.announced_anime)
        upperMenu.menu.findItem(R.id.announced_anime).setOnMenuItemClickListener {
            Log.d(ANIME_LIST_FRAGMENT_TAG, "Нажата кнопка: Анонсы")
            it.isChecked = true
            return@setOnMenuItemClickListener true
        }

        viewModel.apiStatus.observe(viewLifecycleOwner) {
            viewModel.bindApiStatus(binding.status)
        }
        viewModel.ongoingAnimeData.observe(viewLifecycleOwner) { pagingData ->
            viewModel.bindOngoingAnimeData(adapter, pagingData)
        }
        viewModel.followedAnimeList.observe(viewLifecycleOwner) { followedAnimeList ->
            adapter.submitFollowedAnimeList(followedAnimeList)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}