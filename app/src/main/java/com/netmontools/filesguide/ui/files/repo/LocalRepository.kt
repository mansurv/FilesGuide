package com.netmontools.lookatnet.ui.local.repository

import android.app.Application
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netmontools.filesguide.App
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
import java.util.Objects


class LocalRepository(application: Application?) {
    private val TAG = "LocalRepository"

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    var allPoints: LiveData<List<Folder>>? = null
    private var liveData: MutableLiveData<List<Folder>>? = MutableLiveData<List<Folder>>()

    var folders = ArrayList<Folder>()

    //private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        var any = try {
            liveData!!.setValue(App.folders)
            var size = App.folders.size
            size++
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
        }
        allPoints = liveData
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

    fun updateItem(point: Folder?) {
        try {
            folders.clear()
            var fd: Folder
            val dir: Folder
            var file: File? = null
            file = File(point!!.path)
            App.previousPath = file.path
            if (file.exists()) {
                if (file.isDirectory) {
                    dir = Folder()
                    dir.name = file.name
                    dir.path = file.path
                    dir.folders = ArrayList()
                    file.walk().forEach {
                        if (it.exists()) {
                            fd = Folder()
                            fd.name = it.name
                            fd.path = it.path
                            if (it.isDirectory) {
                                fd.isFile = false
                                fd.size = 0//SimpleUtils.getDirectorySize(it)
                                fd.image = App.folder_image!!
                                fd.isImage = false
                                fd.isVideo = false
                            } else {
                                fd.isFile = true
                                fd.size = it.length()
                                App.imageSelector(it)
                                fd.image = App.file_image!!
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
                            dir.addFolderItem(fd)
                        }
                    }
                }
            }
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            npe.message
        }
    }

    suspend fun populate() {
        App.folders.clear()
        try {
            var fd: Folder
            val dir: Folder
            val file = File(Environment.getExternalStorageDirectory().path)
            if (file.exists()) {
                App.rootPath = file.path
                dir = Folder()
                dir.name = file.name
                dir.path = file.path
                dir.folders = ArrayList()
                for (f in Objects.requireNonNull(file.listFiles())) {
                    if (f.exists()) {
                        fd = Folder()
                        if (f.isDirectory) {
                            App.imageSelector(f)
                            fd.image = App.folder_image!!
                            fd.name = f.name
                            fd.path = f.path
                            fd.isFile = false
                            fd.isChecked = false
                            fd.isImage = false
                            fd.isVideo = false
                            //fd.setSize(SimpleUtils.getDirectorySize(f));
                            fd.size = 0L
                            App.folders.add(fd)
                            dir.folders.add(fd)
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
                            App.imageSelector(f)
                            fd.image = App.file_image!!

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
                            App.folders.add(fd)
                            dir.folders.add(fd)
                        }
                    }
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
}