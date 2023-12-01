package com.netmontools.filesguide

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
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
class App : Application() {
    //private var database: AppDatabase? = null

    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main

    //private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()

        suspend fun populateDb() = withContext(dispatcherIO) {
            coroutineScope {
                launch { populate() }
            }
            withContext(dispatcherMain) {
                val size = folders.size
                Log.d("val110450", "size = $size")

            }
        }
            //database = Room.databaseBuilder(this, AppDatabase::class.java, "database")
            //    .addMigrations(AppDatabase.MIGRATION_1_2)
            //    .build()
            val host_image =
                ContextCompat.getDrawable(this, R.drawable.ic_desktop_windows_black_24dp)
            val folder_image = ContextCompat.getDrawable(this, R.drawable.baseline_folder_yellow_24)
            val file_image = ContextCompat.getDrawable(this, R.drawable.ic_file)
        }

        //fun getDatabase(): AppDatabase? {
        //    return database
        //}

        fun populate() {
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
                                imageSelector(f)
                                fd.image = folder_image!!
                                fd.name = f.name
                                fd.path = f.path
                                fd.isFile = false
                                fd.isChecked = false
                                fd.isImage = false
                                fd.isVideo = false
                                //fd.setSize(SimpleUtils.getDirectorySize(f));
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
                                imageSelector(f)
                                fd.image = file_image!!

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
            //var host_image: Drawable? = null
            var folder_image: Drawable? = null
            var file_image: Drawable? = null
            //var folders = ArrayList<Folder>()

            //var remoteFolders: ArrayList<RemoteFolder> = ArrayList<RemoteFolder>()
            //var hosts: ArrayList<RemoteModel> = ArrayList<RemoteModel>()
            var share: Array<String>? = null
            var rootPath: String? = null
            var previousPath: String? = null
            var remoteRootPath: String? = null
            var remotePreviousPath: String? = null
            var remoteCurrentPath: String? = null

            fun imageSelector(file: File) {
                val ext = SimpleUtils.getExtension(file.name)
                when (ext) {
                    "ai" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_ai)
                    "avi" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_avi)
                    "bmp" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_bmp)
                    "cdr" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_cdr)
                    "css" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_css)
                    "doc" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_doc)
                    "eps" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_eps)
                    "flv" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_flv)
                    "gif" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_gif)
                    "htm" -> {
                        file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_html)
                        file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_html)
                    }

                    "html" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_html)
                    "iso" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_iso)
                    "js" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_js)
                    "jpg" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_jpg)
                    "mov" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_mov)
                    "mp3" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_mp3)
                    "mpg" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_mpg)
                    "pdf" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_pdf)
                    "php" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_php)
                    "png" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_png)
                    "ppt" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_ppt)
                    "ps" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_ps)
                    "psd" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_psd)
                    "raw" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_raw)
                    "svg" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_svg)
                    "tiff" -> {
                        file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_tif)
                        file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_tif)
                    }

                    "tif" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_tif)
                    "txt" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_txt)
                    "xls" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_xls)
                    "xml" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_xml)
                    "zip" -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_zip)
                    else -> file_image = ContextCompat.getDrawable(instance!!, R.drawable.ic_file)
                }
            }
        }
}
