package com.netmontools.filesguide

import android.app.Application
import android.os.Environment
import com.netmontools.filesguide.ui.files.model.Folder
import com.netmontools.filesguide.utils.SimpleUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Objects


class App : Application() {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    //private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        instance = this

        suspend fun scanRootPath() = withContext(ioDispatcher) {
            viewModelScope {
                launch {
                    scan()
                }
            }
        }
    }

    fun scan() {
        folders.clear()
        try {
            var fd: Folder
            val dir: Folder
            val file = File(Environment.getExternalStorageDirectory().path)
            if (file.exists()) {
                rootPath = file.path
                dir = Folder()
                dir.name = file.name
                dir.path = file.path
                dir.folders = ArrayList()
                for (f in Objects.requireNonNull(file.listFiles())) {
                    if (f.exists()) {
                        fd = Folder()
                        if (f.isDirectory) {
                            fd.name = f.name
                            fd.path = f.path
                            fd.isFile = false
                            fd.isChecked = false
                            fd.isImage = false
                            fd.isVideo = false
                            fd.setItemSize(SimpleUtils.getDirectorySize(f));
                            fd.size = 0L
                            folders.add(fd)
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
                            dir.folders.add(fd)
                        }
                    }
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
    companion object {

        var instance: App? = null
        var folders = ArrayList<Folder>()
        var rootPath: String? = null
        var previousPath: String? = null
    }
}
