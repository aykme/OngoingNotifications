package com.aykme.ongoingnotifications.ui.animelist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aykme.ongoingnotifications.data.repository.ShikimoriApiRepository
import com.aykme.ongoingnotifications.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.ongoingnotifications.databinding.FragmentAnimeListBinding
import com.aykme.ongoingnotifications.domain.usecase.FetchOngoingAnimeListUseCase

const val AnimeListFragmentTag = "AnimeListFragment"

class AnimeListFragment : Fragment() {
    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeListViewModel by viewModels {
        AnimeListViewModelFactory(
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
        viewModel.ongoingAnimeList.observe(viewLifecycleOwner) {
            Log.d(AnimeListFragmentTag, "Размер полученного листа: ${it.size}")
            val anime = it[0]
            Log.d(AnimeListFragmentTag, "Аниме детали: $anime")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}