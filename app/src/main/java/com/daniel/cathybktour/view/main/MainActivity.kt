package com.daniel.cathybktour.view.main

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.daniel.cathybktour.R
import com.daniel.cathybktour.databinding.ActivityMainBinding
import com.daniel.cathybktour.databinding.TabItemBinding
import com.daniel.cathybktour.utils.Utils
import com.daniel.cathybktour.view.main.viewModel.MainActivityViewModel
import com.google.android.material.tabs.TabLayout
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavLogger
import com.ncapdevi.fragnav.FragNavSwitchController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import com.ncapdevi.fragnav.tabhistory.UniqueTabHistoryStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FragNavController.TransactionListener, FragNavController.RootFragmentListener {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    //主控制
    private val mNavController = FragNavController(
        supportFragmentManager,
        R.id.fl_content
    )

    override val numberOfRootFragments: Int = 2
    private lateinit var tabs: Array<String>

    private val mTabIconsNormal = intArrayOf(

        R.drawable.tab_home_n,
        R.drawable.tab_explore_n

    )

    private val mTabIconsSelected = intArrayOf(

        R.drawable.tab_home_s,
        R.drawable.tab_explore_s

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {

            setContentView(this.root)
            mainViewModel = viewModel

        }

        initTab()
        initFragment(savedInstanceState)
        initObserver()

    }

    private fun initObserver() {

        viewModel.selectedTabIndex.observe(this) { tabIndex ->

            mNavController.switchTab(tabIndex)

        }

        viewModel.currentLanguage.observe(this) { language ->

            val config = Configuration()
            config.setLocale(viewModel.getLocale(language))
            resources.updateConfiguration(config, resources.displayMetrics)

            initTab()

        }

    }

    override fun getRootFragment(index: Int): Fragment {
        return when (index) {

            FragNavController.TAB1 -> HomeFragment.newInstance()
            FragNavController.TAB2 -> ExploreFragment.newInstance()
            else -> throw IllegalStateException("Need to send an index that we know")

        }
    }

    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {

        actionBar?.setDisplayHomeAsUpEnabled(mNavController.isRootFragment.not())

    }

    override fun onTabTransaction(fragment: Fragment?, index: Int) {

        // If we have a backstack, show the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(mNavController.isRootFragment.not())
        actionBar?.setDisplayHomeAsUpEnabled(mNavController.isRootFragment.not())

    }

    private fun initTab() {

        if (binding.bottomTabLayout.tabCount > 0) {

            binding.bottomTabLayout.removeAllTabs()

        }

        tabs = resources.getStringArray(R.array.tab_main)
        for (i in tabs.indices) {
            val tab = binding.bottomTabLayout.newTab()

            // 使用 ViewBinding 來初始化 tab_item
            val tabItemBinding = TabItemBinding.inflate(layoutInflater)
            tab.customView = tabItemBinding.root

            val icon = tabItemBinding.tabIcon

            if (i == 0) {
                icon.setImageResource(mTabIconsSelected[i])
                val laParams = icon.layoutParams
                laParams?.let {
                    it.height = Utils.dp2pixel(this@MainActivity, 30)
                    it.width = Utils.dp2pixel(this@MainActivity, 30)
                    icon.layoutParams = it
                }
            } else {
                val laParams = icon.layoutParams
                laParams?.let {
                    it.height = Utils.dp2pixel(this@MainActivity, 30)
                    it.width = Utils.dp2pixel(this@MainActivity, 30)
                    icon.layoutParams = it
                }
                icon.setImageResource(mTabIconsNormal[i])
            }

            icon.setImageDrawable(
                Utils.setDrawableSelector(
                    this,
                    mTabIconsNormal[i],
                    mTabIconsSelected[i]
                )
            )

            val title = tabItemBinding.tabTitle
            title.text = tabs[i]
            val csl = ContextCompat.getColor(this@MainActivity, R.color.gray_80FFFFFF)
            title.setTextColor(csl)

            binding.bottomTabLayout.addTab(tab)
        }

        // 在所有 tabs 初始化完之後，將第 0 號位置的 tab 文字顏色設為白色
        val initialTab = binding.bottomTabLayout.getTabAt(0)
        val initialTitle = initialTab?.customView?.findViewById<TextView>(R.id.tab_title)
        initialTitle?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))

    }

    private fun initFragment(savedInstanceState: Bundle?) {

        mNavController.apply {
            transactionListener = this@MainActivity
            rootFragmentListener = this@MainActivity
            createEager = true

            //log
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {
                    Log.e("MainActivity", message, throwable)
                }
            }

            //fragment切換時的狀態
            fragmentHideStrategy = FragNavController.HIDE

            //navigation策略
            navigationStrategy = UniqueTabHistoryStrategy(object : FragNavSwitchController {
                override fun switchTab(index: Int, transactionOptions: FragNavTransactionOptions?) {

                    binding.bottomTabLayout.getTabAt(index)?.select()

                }
            })

        }

        binding.bottomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                val img = tab?.customView?.findViewById<ImageView>(R.id.tab_icon)
                val title = tab?.customView?.findViewById<TextView>(R.id.tab_title)
                val laParams = img?.layoutParams

                if (laParams != null) {
                    laParams.height = Utils.dp2pixel(this@MainActivity, 35)
                    laParams.width = Utils.dp2pixel(this@MainActivity, 35)
                }

                title?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))

                img?.layoutParams = laParams

                img?.setImageResource(mTabIconsSelected[tab.position])

                viewModel.switchTab(tab?.position ?: 0)

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

                val img = tab?.customView?.findViewById<ImageView>(R.id.tab_icon)
                val title = tab?.customView?.findViewById<TextView>(R.id.tab_title)

                var laParams = img?.layoutParams
                if (laParams != null) {
                    laParams.height = Utils.dp2pixel(this@MainActivity, 29)
                    laParams.width = Utils.dp2pixel(this@MainActivity, 29)
                }
                img?.layoutParams = laParams
                img?.setImageResource(mTabIconsNormal[tab.position])

                title?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.gray_80FFFFFF))

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

                mNavController.clearStack()

            }

        })

        //設定初始化tab位置
        mNavController.initialize(0, savedInstanceState)

    }

}