package com.netmontools.filesguide.ui.files.view

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.netmontools.filesguide.App
import com.netmontools.filesguide.MainViewModel
import com.netmontools.filesguide.databinding.FragmentHomeBinding
import com.netmontools.filesguide.ui.files.model.Folder


class HomeFragment : Fragment() {

    private lateinit var localViewModel: HomeViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var localRefreshLayout: SwipeRefreshLayout
    private lateinit var localRecyclerView: RecyclerView
    private lateinit var sp: SharedPreferences
    lateinit var appCompatActivity: AppCompatActivity
    lateinit var appBar: ActionBar
    lateinit var layoutManager: AutoFitGridLayoutManager
    private lateinit var adapter: LocalAdapter
    private val position = 0

    @SuppressLint("UseSparseArrays")
    var selectedArray = SparseArray<Boolean>()
    var isSelected = false
    var isListMode: Boolean = false

    fun LocalFragment() {}

    fun newInstance(index: Int) {
        return LocalFragment()
    }


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = PreferenceManager.getDefaultSharedPreferences(App.instance)

        mainViewModel =
            ViewModelProvider(requireContext() as FragmentActivity).get(MainViewModel::class.java
            )
        val actionBarTitle = App.rootPath
        sp.edit().putString("root_path", actionBarTitle).apply()
        //mainViewModel.updateActionBarTitle(actionBarTitle);

        //mainViewModel.updateActionBarTitle(actionBarTitle);
        appCompatActivity = (activity as AppCompatActivity?)!!
        appBar = appCompatActivity.supportActionBar!!
        appBar.setTitle(actionBarTitle)

        if (savedInstanceState != null) {
            val mode = savedInstanceState.getInt("mode")
            isListMode = if (mode == 0) {
                false
            } else {
                true
            }
        }
    }
        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.localRefreshLayout.setColorSchemeResources(
            R.color.holo_blue_bright, R.color.holo_green_light,
            R.color.holo_orange_light, R.color.holo_red_light
        )
        binding.localRefreshLayout.isEnabled = false


        binding.localRecyclerView
        layoutManager = AutoFitGridLayoutManager(requireActivity(), 400)
        binding.localRecyclerView.layoutManager = layoutManager
        isListMode = false
        binding.localRecyclerView.itemAnimator = DefaultItemAnimator()
        binding.localRecyclerView.setHasFixedSize(true)

        adapter = LocalAdapter()
        binding.localRecyclerView.adapter = adapter

        localViewModel =
            ViewModelProvider.AndroidViewModelFactory(App.getInstance()).create(HomeViewModel::class.java)
        localViewModel.allPoints.observe(viewLifecycleOwner, Observer<List<Folder>> {points -> adapter.setPoints(points)
                binding.localRefreshLayout.isRefreshing = false })

        return root
    }

    class AutoFitGridLayoutManager(context: Context, columnWidth: Int) : GridLayoutManager(context, 1) {

        private var columnWidth: Int = 0
        private var columnWidthChanged = true

        init {
            setColumnWidth(columnWidth)
        }

        fun setColumnWidth(newColumnWidth: Int) {
            if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
                columnWidth = newColumnWidth
                columnWidthChanged = true
            }
        }

        override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
            if (columnWidthChanged && columnWidth > 0) {
                val totalSpace: Int
                if (orientation == LinearLayoutManager.VERTICAL) {
                    totalSpace = width - paddingRight - paddingLeft
                } else {
                    totalSpace = height - paddingTop - paddingBottom
                }
                val spanCount = Math.max(1, totalSpace / columnWidth)
                setSpanCount(spanCount)
                columnWidthChanged = false
            }
            super.onLayoutChildren(recycler, state)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}