package com.aykme.animenoti.ui.animelist

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.databinding.FragmentAnimeListBinding
import com.aykme.animenoti.ui.animelist.paging.PagingAnimeListAdapter

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
        val searchListAdapter = PagingAnimeListAdapter(requireContext(), viewModel)
        val layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = ongoingListAdapter
        recyclerView.layoutManager = layoutManager
        val ongoingAnimeButton = binding.ongoingAnimeButton
        val announcedAnimeButton = binding.announcedAnimeButton
        val searchAnimeButton = binding.searchAnimeButton
        val searchInputTextButton = binding.searchInputTextButton
        val verticalDivider = binding.verticalDivider
        val searchInputTextLayout = binding.searchInputTextLayout
        val searchTextInputEditText = binding.searchTextInputEditText
        val status = binding.status
        status.visibility = View.GONE
        viewModel.bindDefaultUpperMenuState(
            ongoingAnimeButton,
            announcedAnimeButton,
            searchAnimeButton,
            searchInputTextButton,
            verticalDivider,
            searchInputTextLayout,
            ongoingListAdapter
        )
        ongoingAnimeButton.setOnClickListener {
            recyclerView.adapter = ongoingListAdapter
            viewModel.submitAnimeData(ongoingListAdapter, AnimeDataType.ONGOING)
            viewModel.onOngoingAnimeButtonClicked(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchInputTextButton,
                verticalDivider,
                searchInputTextLayout
            )
        }
        announcedAnimeButton.setOnClickListener {
            recyclerView.adapter = announcedListAdapter
            viewModel.submitAnimeData(announcedListAdapter, AnimeDataType.ANONS)
            viewModel.onAnnouncedAnimeButtonClicked(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchInputTextButton,
                verticalDivider,
                searchInputTextLayout
            )
        }
        searchAnimeButton.setOnClickListener {
            viewModel.submitAnimeData(searchListAdapter, AnimeDataType.SEARCH)
            recyclerView.adapter = searchListAdapter
            viewModel.onSearchAnimeButtonClicked(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchInputTextButton,
                verticalDivider,
                searchInputTextLayout
            )
        }
        searchInputTextButton.setOnClickListener {
            viewModel.searchData = searchTextInputEditText.text.toString()
            viewModel.submitAnimeData(searchListAdapter, AnimeDataType.SEARCH)
            recyclerView.adapter = searchListAdapter
            viewModel.onSearchInputTextButtonClicked(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchInputTextButton,
                verticalDivider,
                searchInputTextLayout
            )
            searchTextInputEditText.text = SpannableStringBuilder("")
        }
        viewModel.apply {
            apiStatus.observe(viewLifecycleOwner) {
                bindApiStatus(status)
            }
            followedAnimeList.observe(viewLifecycleOwner) { followedAnimeList ->
                ongoingListAdapter.submitFollowedAnimeList(followedAnimeList)
                announcedListAdapter.submitFollowedAnimeList(followedAnimeList)
                searchListAdapter.submitFollowedAnimeList(followedAnimeList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}