package com.aykme.animenoti.ui.base

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.R
import com.aykme.animenoti.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainActivityViewModel by viewModels {
        MainViewModelFactory.getInstance(application as AnimeNotiApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomMenu: BottomNavigationView = binding.bottomMenu

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            navController.graph
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomMenu.setupWithNavController(navController)
        supportActionBar?.hide()

        val badge = binding.bottomMenu.getOrCreateBadge(R.id.favorites_anime)
        viewModel.followedAnimeList.observe(this) { followedAnimeList ->
            viewModel.bindBadge(badge, followedAnimeList)
        }
    }
}