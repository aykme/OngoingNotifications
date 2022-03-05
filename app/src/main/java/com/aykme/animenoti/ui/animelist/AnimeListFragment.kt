package com.aykme.animenoti.ui.animelist

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.R
import com.aykme.animenoti.databinding.FragmentAnimeListBinding
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.ui.animelist.paging.PagingAnimeListAdapter

class AnimeListFragment : Fragment() {
    private lateinit var binding: FragmentAnimeListBinding
    private val viewModel: AnimeListViewModel by viewModels {
        AnimeListViewModel.AnimeListViewModelFactory.getInstance(activity?.application as AnimeNotiApplication)
    }
    private var lastFollowedAnimeList: List<Anime>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.animeListRecyclerView
        val ongoingListAdapter = PagingAnimeListAdapter(requireContext(), viewModel)
        val announcedListAdapter = PagingAnimeListAdapter(requireContext(), viewModel)
        var searchListAdapter = PagingAnimeListAdapter(requireContext(), viewModel)
        val layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = ongoingListAdapter
        recyclerView.layoutManager = layoutManager
        val ongoingAnimeButton = binding.ongoingAnimeButton
        val announcedAnimeButton = binding.announcedAnimeButton
        val searchAnimeButton = binding.searchAnimeButton
        val verticalDivider = binding.verticalDivider
        val searchInputTextLayout = binding.searchInputTextLayout
        val searchTextInputEditText = binding.searchTextInputEditText
        val searchCancelButton = binding.searchCancelButton
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setProgressViewOffset(false, 45, 245)
        swipeRefresh.setColorSchemeResources(R.color.pink)
        val status = binding.status
        status.visibility = View.GONE
        viewModel.bindDefaultUpperMenuState(
            ongoingAnimeButton,
            announcedAnimeButton,
            searchAnimeButton,
            searchCancelButton,
            verticalDivider,
            searchInputTextLayout,
            ongoingListAdapter
        )
        ongoingAnimeButton.setOnClickListener {
            recyclerView.adapter = ongoingListAdapter
            viewModel.submitAnimeData(ongoingListAdapter, AnimeDataType.ONGOING)
            viewModel.dataType = AnimeDataType.ONGOING
            viewModel.onOngoingAnimeButtonClicked(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchCancelButton,
                verticalDivider,
                searchInputTextLayout
            )
        }
        announcedAnimeButton.setOnClickListener {
            recyclerView.adapter = announcedListAdapter
            viewModel.submitAnimeData(announcedListAdapter, AnimeDataType.ANONS)
            viewModel.dataType = AnimeDataType.ANONS
            viewModel.onAnnouncedAnimeButtonClicked(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchCancelButton,
                verticalDivider,
                searchInputTextLayout
            )
        }
        searchAnimeButton.setOnClickListener {
            recyclerView.adapter = searchListAdapter
            viewModel.submitAnimeData(searchListAdapter, AnimeDataType.SEARCH)
            viewModel.dataType = AnimeDataType.SEARCH
            viewModel.onSearchAnimeButtonClicked(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchCancelButton,
                verticalDivider,
                searchInputTextLayout
            )
        }
        searchTextInputEditText.setOnKeyListener(fun(
            view: View,
            keyCode: Int,
            keyEvent: KeyEvent
        ): Boolean {
            val result = viewModel.onEnterKeyClicked(
                requireContext(),
                view,
                keyCode,
                keyEvent,
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchCancelButton,
                verticalDivider,
                searchInputTextLayout
            )
            return if (result) {
                searchListAdapter = PagingAnimeListAdapter(requireContext(), viewModel)
                recyclerView.adapter = searchListAdapter
                viewModel.searchData = searchTextInputEditText.text.toString()
                viewModel.submitAnimeData(searchListAdapter, AnimeDataType.SEARCH)
                lastFollowedAnimeList?.let {
                    searchListAdapter.submitFollowedAnimeList(it)
                }
                searchTextInputEditText.text = SpannableStringBuilder("")
                true
            } else false
        })
        searchCancelButton.setOnClickListener {
            viewModel.cancelSearch(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchCancelButton,
                verticalDivider,
                searchInputTextLayout,
                requireContext(),
                view
            )
        }
        viewModel.apply {
            apiStatus.observe(viewLifecycleOwner) {
                bindApiStatus(status)
            }
            followedAnimeList.observe(viewLifecycleOwner) { followedAnimeList ->
                lastFollowedAnimeList = followedAnimeList
                ongoingListAdapter.submitFollowedAnimeList(followedAnimeList)
                announcedListAdapter.submitFollowedAnimeList(followedAnimeList)
                searchListAdapter.submitFollowedAnimeList(followedAnimeList)
            }
            swipeRefresh.setOnRefreshListener {
                viewModel.refreshRecyclerView(
                    ongoingListAdapter,
                    announcedListAdapter,
                    searchListAdapter
                )
                swipeRefresh.isRefreshing = false
            }
        }
    }
}