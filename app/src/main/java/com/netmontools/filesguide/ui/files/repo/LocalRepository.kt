package com.netmontools.filesguide.ui.files.repo

import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netmontools.filesguide.App
import com.netmontools.filesguide.R
import com.netmontools.filesguide.ui.files.model.Folder
import com.netmontools.filesguide.utils.SimpleUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Objects


class LocalRepository() {
    private val TAG = "LocalRepository"

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    var allPoints: LiveData<List<Folder>>? = null
    private var liveData: MutableLiveData<List<Folder>>? = MutableLiveData<List<Folder>>()

    var folder_image = ContextCompat.getDrawable(App.instance!!, R.drawable.baseline_folder_yellow_24);
    var file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_file);
    var rootPath: String? = null
    var previousPath: String? = null

    var folders = ArrayList<Folder>()
    var foldersApp = ArrayList<Folder>()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {

        try {
            for(folder in App.folders) {
                if(folder.isFile) {
                    folder.image = file_image!!
                } else {
                    folder.image = folder_image!!
                }
            }

            liveData!!.setValue(App.folders)
            foldersApp = App.folders

        } catch (npe: NullPointerException) {
            npe.printStackTrace()
        }
        allPoints = liveData

    }

    suspend fun scan(item: Folder?) = withContext(ioDispatcher) {
        coroutineScope {
            launch { scanItem(item) }
        }
    }

    suspend fun update(item: Folder?) = withContext(ioDispatcher) {
        coroutineScope {
            launch {updateItem(item)}
        }
        PostUpdate()
    }
    fun getAll(): LiveData<List<Folder>>? {
        return allPoints
    }

    suspend fun PostUpdate()  = withContext(mainDispatcher) {
        liveData!!.value = folders
        allPoints = liveData
    }

    fun imageSelector(file: File) {
        val ext = SimpleUtils.getExtension(file.name)
        when (ext) {
            "ai" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_ai)
            "avi" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_avi)
            "bmp" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_bmp)
            "cdr" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_cdr)
            "css" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_css)
            "doc" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_doc)
            "eps" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_eps)
            "flv" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_flv)
            "gif" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_gif)
            "htm" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_html)
            "html" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_html)
            "iso" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_iso)
            "js" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_js)
            "jpg" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_jpg)
            "mov" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_mov)
            "mp3" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_mp3)
            "mpg" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_mpg)
            "pdf" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_pdf)
            "php" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_php)
            "png" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_png)
            "ppt" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_ppt)
            "ps" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_ps)
            "psd" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_psd)
            "raw" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_raw)
            "svg" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_svg)
            "tiff" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_tif)
            "tif" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_tif)
            "txt" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_txt)
            "xls" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_xls)
            "xml" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_xml)
            "zip" -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_zip)
            else -> file_image = ContextCompat.getDrawable(App.instance!!, R.drawable.ic_file)
        }
    }

    fun scanItem(point: Folder?) {
        try {
            var rootPath = point!!.getPathItem()
            val folderPath = rootPath + "/Fb2Lib"
            val file = File(folderPath)
            if (!file.exists())
                file.mkdir()
            val scanPath = Paths.get(rootPath)
            val result = arrayListOf<String>()
            val paths = Files.walk(scanPath)
                .filter { item -> Files.isRegularFile(item) }
                .filter { item -> item.toString().endsWith(".fb2") }
                .forEach { item -> result.add(item.toString()) }
            for (index in result.indices) {
                val sourcePath = Paths.get(result.get(index))
                val targetPath = Paths.get(folderPath + "/" + sourcePath.fileName)
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }

        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            npe.message
        }
    }

    fun updateItem(point: Folder?) {
        try {
            folders.clear()
            var fd: Folder
            val dir: Folder
            var file: File? = null
            file = File(point!!.path)
            App.previousPath = file.path
            if (file.exists()) {
                App.currentPath = App.previousPath
                if (file.isDirectory) {
                    dir = Folder()
                    dir.name = file.name
                    dir.path = file.path
                    for (it in (file.listFiles())!!) {
                        if (it.exists()) {
                            fd = Folder()
                            fd.name = it.name
                            fd.path = it.path
                            if (it.isDirectory) {
                                fd.isFile = false
                                fd.size = 0//SimpleUtils.getDirectorySize(it)
                                fd.image = folder_image!!
                                fd.isImage = false
                                fd.isVideo = false
                            } else {
                                fd.isFile = true
                                fd.size = it.length()
                                imageSelector(it)
                                fd.image = file_image!!
                                val ext = SimpleUtils.getExtension(it.name)
                                if (ext.equals("jpg", ignoreCase = true) ||
                                    ext.equals("jpeg", ignoreCase = true) ||
                                    ext.equals("png", ignoreCase = true) ||
                                    ext.equals("webp", ignoreCase = true) ||
                                    ext.equals("bmp", ignoreCase = true)
                                ) {
                                    fd.isImage = true
                                    fd.isVideo = false
                                } else if (ext.equals("mp4", ignoreCase = true) ||
                                    ext.equals("avi", ignoreCase = true) ||
                                    ext.equals("mkv", ignoreCase = true)
                                ) {
                                    fd.isImage = false
                                    fd.isVideo = true
                                } else {
                                    fd.isImage = false
                                    fd.isVideo = false
                                }
                            }
                            folders.add(fd)
                        }
                    }
                }
            }
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            npe.message
        }
    }

    fun populate() {
        folders.clear()
        try {
            var fd: Folder
            val dir: Folder
            val file = File(Environment.getExternalStorageDirectory().path)
            if (file.exists()) {
                rootPath = file.path
                App.currentPath = App.rootPath
                dir = Folder()
                dir.name = file.name
                dir.path = file.path
                for (f in Objects.requireNonNull(file.listFiles())) {
                    if (f.exists()) {

                        fd = Folder()
                        if (f.isDirectory) {
                            imageSelector(f)
                            fd.image = folder_image!!
                            fd.name = f.name
                            fd.path = f.path
                            fd.isFile = false
                            fd.isChecked = false
                            fd.isImage = false
                            fd.isVideo = false
                            fd.setItemSize(SimpleUtils.getDirectorySize(f));
                            fd.size = 0L
                            folders.add(fd)
                        }
                    }
                }
                for (f in Objects.requireNonNull(file.listFiles())) {
                    if (f.exists()) {
                        fd = Folder()
                        if (f.isFile) {
                            fd.name = f.name
                            fd.path = f.path
                            fd.isFile = true
                            fd.isChecked = false
                            fd.size = f.length()
                            imageSelector(f)
                            fd.image =file_image!!

                            val ext: String = SimpleUtils.getExtension(f.getName());
                            if (ext.contentEquals("jpg") ||
                                ext.contentEquals("png") ||
                                ext.contentEquals("webp") ||
                                ext.contentEquals("bmp")
                            ) {
                                fd.isImage = true;
                                fd.isVideo = false;
                            } else if (ext.contentEquals("mp4") ||
                                ext.contentEquals("avi") ||
                                ext.contentEquals("mkv")
                            ) {
                                fd.isImage = false;
                                fd.isVideo = true;
                            } else {
                                fd.isImage = false;
                                fd.isVideo = false;
                            }
                            folders.add(fd)
                        }
                    }
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
    companion object {

       var folders = ArrayList<Folder>()
       var rootPath: String? = null
       var previousPath: String? = null
   }
}