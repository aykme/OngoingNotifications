package com.aykme.animenoti.ui.animelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.R
import com.aykme.animenoti.databinding.FragmentAnimeListBinding
import com.aykme.animenoti.ui.animelist.paging.PagingAnimeListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class AnimeListFragment : Fragment() {
    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeListViewModel by viewModels {
        AnimeListViewModelFactory.getInstance(activity?.application as AnimeNotiApplication)
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
        val ongoingListAdapter = PagingAnimeListAdapter(requireContext(), viewModel)
        val announcedListAdapter = PagingAnimeListAdapter(requireContext(), viewModel)
        val layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = ongoingListAdapter
        recyclerView.layoutManager = layoutManager

        val upperMenu: BottomNavigationView = binding.upperMenu
        val menuOngoingAnime = upperMenu.menu.findItem(R.id.ongoing_anime)
        val menuAnnouncedAnime = upperMenu.menu.findItem(R.id.announced_anime)
        val releasedAnimeButton = binding.releasedAnimeButton
        val status = binding.status
        status.visibility = View.GONE

        menuOngoingAnime.setOnMenuItemClickListener {
            recyclerView.adapter = ongoingListAdapter
            viewModel.animeDataType.value = AnimeDataType.ONGOING
            it.isChecked = true
            return@setOnMenuItemClickListener true
        }
        menuAnnouncedAnime.setOnMenuItemClickListener {
            recyclerView.adapter = announcedListAdapter
            viewModel.animeDataType.value = AnimeDataType.ANONS
            it.isChecked = true
            return@setOnMenuItemClickListener true
        }
        viewModel.apply {
            animeDataType.observe(viewLifecycleOwner) { animeStatus ->
                val currentAdapter = recyclerView.adapter as PagingAnimeListAdapter
                submitAnimeData(currentAdapter, animeStatus)
            }
            apiStatus.observe(viewLifecycleOwner) {
                bindApiStatus(status)
            }
            followedAnimeList.observe(viewLifecycleOwner) { followedAnimeList ->
                ongoingListAdapter.submitFollowedAnimeList(followedAnimeList)
                announcedListAdapter.submitFollowedAnimeList(followedAnimeList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}