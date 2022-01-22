package com.aykme.animenotifications.ui.animelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aykme.animenotifications.Application
import com.aykme.animenotifications.data.repository.ShikimoriApiRepository
import com.aykme.animenotifications.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.animenotifications.databinding.FragmentAnimeListBinding
import com.aykme.animenotifications.domain.usecase.FetchOngoingAnimeListUseCase
import com.aykme.animenotifications.ui.animelist.paging.PagingAnimeListAdapter

const val ANIME_LIST_FRAGMENT_TAG = "AnimeListFragment"

class AnimeListFragment : Fragment() {
    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeListViewModel by viewModels {
        AnimeListViewModelFactory(
            ((activity?.application) as Application),
            FetchOngoingAnimeListUseCase(ShikimoriApiRepository(ShikimoriApi.instance))
        )
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

        viewModel.apiStatus.observe(viewLifecycleOwner) {
            viewModel.bindApiStatus(binding.status)
        }
        viewModel.ongoingAnimeData.observe(viewLifecycleOwner) { pagingData ->
            viewModel.bindOngoingAnimeData(adapter, pagingData)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}