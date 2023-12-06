package com.netmontools.filesguide.ui.files.view




import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import com.netmontools.filesguide.R
import com.netmontools.filesguide.databinding.FragmentHomeBinding
import com.netmontools.filesguide.ui.files.model.Folder
import com.netmontools.filesguide.utils.SimpleUtils
import java.io.File
import java.util.Objects


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
    private val menuHost: MenuHost get() = requireActivity()

    @SuppressLint("UseSparseArrays")
    var selectedArray = SparseArray<Boolean>()
    var isSelected = false
    var isListMode: Boolean = false
    var isBigMode: Boolean = false

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
            android.R.color.holo_blue_bright, android.R.color.holo_green_light,
            android.R.color.holo_orange_light, android.R.color.holo_red_light
        )
        binding.localRefreshLayout.isEnabled = false

        layoutManager = AutoFitGridLayoutManager(requireActivity(), 400)
        binding.localRecyclerView.layoutManager = layoutManager
        isListMode = false
        binding.localRecyclerView.itemAnimator = DefaultItemAnimator()
        binding.localRecyclerView.setHasFixedSize(true)

        adapter = LocalAdapter()
        binding.localRecyclerView.adapter = adapter

        localViewModel =
            ViewModelProvider.AndroidViewModelFactory(App.getInstance()).create(HomeViewModel::class.java)
        localViewModel.allPoints.observe(viewLifecycleOwner, Observer<List<Folder>>
                {points -> adapter.setPoints(points)
                binding.localRefreshLayout.isRefreshing = false })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuHost.addMenuProvider((object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {

                if (!isListMode) {
                    menu.findItem(com.netmontools.filesguide.R.id.listMode).setIcon(com.netmontools.filesguide.R.drawable.baseline_view_list_yellow_24)
                } else {
                    menu.findItem(com.netmontools.filesguide.R.id.listMode).setIcon(com.netmontools.filesguide.R.drawable.baseline_view_column_yellow_24)
                }
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menuInflater.inflate(com.netmontools.filesguide.R.menu.fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    com.netmontools.filesguide.R.id.property -> {
                        if (menuItem.isChecked) {

                            menuItem.setChecked(false)
                        } else {
                            menuItem.setChecked(true)

                        }

                        true
                    }
                    
                    com.netmontools.filesguide.R.id.listMode -> {
                        if (isListMode == false) {
                            isListMode = true
                            //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                            binding.localRecyclerView.setLayoutManager(layoutManager)
                            menuItem.setIcon(com.netmontools.filesguide.R.drawable.baseline_view_list_yellow_24)
                        } else {
                            isListMode = false
                            binding.localRecyclerView.setLayoutManager(GridLayoutManager(getActivity(), 2));
                            menuItem.setIcon(com.netmontools.filesguide.R.drawable.baseline_view_column_yellow_24)
                        }
                        true
                    }
                    else -> return true
                }
            }

            override fun onMenuClosed(menu: Menu) {}// Меню закрыто

        }), viewLifecycleOwner)

        adapter.setOnItemClickListener { point ->
            isSelected = false;
            if(!point.isFile) {
                binding.localRefreshLayout.setRefreshing(true)
                localViewModel.update(point)
                mainViewModel.updateActionBarTitle(point.getNameItem())
            } else {
                try {
                    if(point.getPathItem() != null) {
                        val file = File(point.getPathItem())
                        if (file.exists() && (file.isFile())) {
                            val ext = SimpleUtils.getExtension(file.getName())
                            if (ext.equals("fb2")) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setType("text/plain")
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                var chosenIntent =
                                    Intent.createChooser(intent, "Choose file...")
                                startActivity(chosenIntent)
                            } else {
                                SimpleUtils.openFile(App.instance, file)
                            }
                        }
                    }
                } catch (npe: NullPointerException) {
                    npe.printStackTrace()
                }
            }

        }

        adapter.setOnItemLongClickListener { point: Folder ->

            point.isChecked = !point.isChecked
            //localViewModel.update(adapter.getPointAt(position));
            adapter.notifyItemChanged(position);
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState);
        var mode = 0
        if(isListMode) {
            mode = 1
        }

        outState.putInt("mode", mode)
    }

    override fun onPause() {
        super.onPause();
        sp.edit().putString("actionbar_title", appBar.getTitle().toString()).apply();
        sp.edit().putBoolean("layout_mode", isListMode).apply()
    }

    override fun onResume() {
        super.onResume();
        var actionBarTitle = sp.getString("actionbar_title", "");
        if(actionBarTitle.equals("0")) {
            mainViewModel.updateActionBarTitle(App.rootPath);
        } else mainViewModel.updateActionBarTitle(actionBarTitle!!);

        if (isListMode == false) {
            binding.localRecyclerView.setLayoutManager(GridLayoutManager(getActivity(), 2));
            //binding.localRecyclerView.setLayoutManager(layoutManager);
            //menuItem.setIcon(R.drawable.baseline_view_list_yellow_24);
        } else {
            binding.localRecyclerView.setLayoutManager(LinearLayoutManager(getActivity()));
            //menuI.setIcon(R.drawable.baseline_view_column_yellow_24);
        }
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

    @Override
    override fun onAttach(context: Context) {
        super.onAttach(requireContext())
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                try {
                    var file: File? = null
                    if (App.previousPath != null) {
                        file =  File(App.previousPath)
                    }
                    if (!file!!.getPath().equals(App.rootPath)) {
                        if (file.exists()) {
                            file = File(Objects.requireNonNull(file.getParent()))
                            val fd: Folder = Folder()
                            fd.isFile = file.isFile()
                            fd.setNameItem(file.getName())
                            fd.setPathItem(file.getPath())
                            if (fd.isFile) {
                                fd.setItemSize(file.length())
                                fd.setImageItem(App.file_image)
                            } else {
                                fd.setItemSize(0L)
                                fd.setImageItem(App.folder_image)
                            }
                            localViewModel.update(fd)
                            binding.localRefreshLayout.setRefreshing(true)
                            mainViewModel.updateActionBarTitle(file.getName())
                        }
                    } else {
                        this.remove()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }

                } catch (npe: NullPointerException) {
                    npe.printStackTrace();
                }
            }
        };
        requireActivity().onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}