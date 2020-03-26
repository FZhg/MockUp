package com.example.android.mockup

import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.android.mockup.navigation.MockUpFragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {
    private lateinit var tabNames: TypedArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        // Hook up the adapter to viewpager
        tabNames = resources.obtainTypedArray(R.array.tab_names)
        view_pager_2.adapter = MockUpFragmentStateAdapter(this)
        TabLayoutMediator(tab_layout, view_pager_2) { tab, position ->
                tab.text = tabNames.getString(position)
        }.attach()
    }

    override fun onStop() {
        super.onStop()
        tabNames.recycle()
    }
}

