package com.netmontools.filesguide

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.netmontools.filesguide.databinding.ActivityMainBinding
import com.netmontools.filesguide.ui.files.model.Folder
import com.netmontools.filesguide.ui.files.view.LocalAdapter
import com.netmontools.filesguide.ui.files.view.LocalViewModel
import com.netmontools.filesguide.utils.MimeTypes
import com.netmontools.filesguide.utils.PermissionUtils
import com.netmontools.filesguide.utils.SimpleUtils
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel
    private lateinit var localRefreshLayout: SwipeRefreshLayout
    private lateinit var localRecyclerView: RecyclerView
    lateinit var layoutManager: AutoFitGridLayoutManager
    var isSelected: Boolean = false
    var isListMode: Boolean = false
    var isBigMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.localRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright, android.R.color.holo_green_light,
            android.R.color.holo_orange_light, android.R.color.holo_red_light
        )
        binding.localRefreshLayout.isEnabled = false
        layoutManager = AutoFitGridLayoutManager(this, 400)
        binding.localRecyclerView.layoutManager = layoutManager
        isListMode = false
        binding.localRecyclerView.itemAnimator = DefaultItemAnimator()
        binding.localRecyclerView.setHasFixedSize(true)

        adapter = LocalAdapter()
        binding.localRecyclerView.adapter = adapter

        localViewModel = ViewModelProvider.AndroidViewModelFactory(App.instance!!).create(LocalViewModel::class.java)
        localViewModel.allPoints.observe(this, Observer<List<Folder>>
        {points -> adapter.setPoints(points)
            binding.localRefreshLayout.isRefreshing = false })


        adapter.setOnItemClickListener { point ->
            isSelected = false;
            if(!point.isFile) {
                binding.localRefreshLayout.setRefreshing(true)
                localViewModel.update(point)
                //mainViewModel.updateActionBarTitle(point.getNameItem())
            } else {
                val file = File(point.getPathItem())
                if (file.exists() && (file.isFile())) {
                    val ext = SimpleUtils.getExtension(file.name)
                    val type = MimeTypes.getMimeType(file)
                    if (ext.equals("jpg") || (ext.equals("jpeg") || (ext.equals("bmp")))) {
                        val intent: Intent = Intent(this@MainActivity, ImageActivity::class.java)
                        intent.setDataAndType(file.path.toString().toUri(), type )
                        intent.putExtra("path", file.path)
                        startActivity(intent)
                    } else if (ext.equals("fb2")) {

                        val viewIntent = Intent(Intent.ACTION_VIEW)
                        viewIntent.setDataAndType(Uri.parse(file.path.toString()), "*/*")
                        val chooserIntent = Intent.createChooser(viewIntent, "Open with...")
                        startActivity(chooserIntent)

                    } else {
                        //open the file
                        try {
                            val intent = Intent()
                            val type = MimeTypes.getMimeType(file)
                            intent.setAction(Intent.ACTION_VIEW)
                            //intent.setDataAndType(Uri.parse(file.getAbsolutePath()), type)
                            intent.setDataAndType(file.path.toString().toUri(), type )
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            this.startActivity(intent)
                        } catch (e: IllegalArgumentException) {
                            Toast.makeText(
                                this,
                                "Cannot open the file" + e.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }//try
                    }//else
                }//if
            }//else
        }//adapter

        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        if (savedInstanceState == null) {
            if (PermissionUtils.hasPermissions(this@MainActivity)) return
            PermissionUtils.requestPermissions(this@MainActivity, MainActivity.PERMISSION_STORAGE)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MainActivity.PERMISSION_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (PermissionUtils.hasPermissions(this)) {
                    Toast.makeText(
                        this,
                        "Permission granted",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Permission not granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MainActivity.PERMISSION_STORAGE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permission granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Permission not granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    companion object {
        private const val PERMISSION_STORAGE = 101
        private const val TAG = "MainActivity"
        lateinit var localViewModel: LocalViewModel
        private lateinit var adapter: LocalAdapter
        private var position = 0
    }
}