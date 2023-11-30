package com.netmontools.filesguide;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.netmontools.filesguide.ui.files.model.Folder;
import com.netmontools.filesguide.utils.SimpleUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class App extends Application {

    public static App instance;
    //private AppDatabase database;
    public static Drawable host_image, folder_image, file_image;
    public static ArrayList<Folder> folders = new ArrayList<Folder>();
    //public static ArrayList<RemoteFolder> remoteFolders = new ArrayList<RemoteFolder>();
    //public static ArrayList<RemoteModel> hosts = new ArrayList<RemoteModel>();
    public static String[] share = null;
    public static String rootPath, previousPath, remoteRootPath, remotePreviousPath, remoteCurrentPath;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        new PopulateDbAsyncTask().execute();

        //database = Room.databaseBuilder(this, AppDatabase.class, "database")
        //        .addMigrations(AppDatabase.MIGRATION_1_2)
        //        .build();

        host_image = ContextCompat.getDrawable(this, R.drawable.ic_desktop_windows_black_24dp);
        folder_image = ContextCompat.getDrawable(this, R.drawable.baseline_folder_yellow_24);
        file_image = ContextCompat.getDrawable(this, R.drawable.ic_file);

    }

    public static App getInstance() {
        return instance;
    }

    //public AppDatabase getDatabase() {
   //    return database;
    //}

    public static void execute(Folder item) {
        new UpdateListAsyncTask().execute(item);
    }


    public static void imageSelector(File file) {
        String ext = SimpleUtils.getExtension(file.getName());
        switch (ext) {
            case "ai" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_ai); break;
            case "avi" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_avi); break;
            case "bmp" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_bmp); break;
            case "cdr" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_cdr); break;
            case "css" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_css); break;
            case "doc" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_doc); break;
            case "eps" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_eps); break;
            case "flv" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_flv); break;
            case "gif" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_gif); break;
            case "htm" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_html);
            case "html" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_html); break;
            case "iso" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_iso); break;
            case "js" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_js); break;
            case "jpg" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_jpg); break;
            case "mov" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_mov); break;
            case "mp3" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_mp3); break;
            case "mpg" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_mpg); break;
            case "pdf" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_pdf); break;
            case "php" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_php); break;
            case "png" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_png); break;
            case "ppt" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_ppt); break;
            case "ps" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_ps); break;
            case "psd" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_psd); break;
            case "raw" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_raw); break;
            case "svg" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_svg); break;
            case "tiff" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_tif);
            case "tif" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_tif); break;
            case "txt" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_txt); break;
            case "xls" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_xls); break;
            case "xml" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_xml); break;
            case "zip" : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_zip); break;
            default : file_image = ContextCompat.getDrawable(App.instance, R.drawable.ic_file); break;
        }
    }

    static class PopulateDbAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            folders.clear();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                Folder fd;
                File file = new File("/");
                if (file.exists()) {
                    rootPath = file.getPath();
                    for (File f : Objects.requireNonNull(file.listFiles())) {
                        if (f.exists()) {
                            fd = new Folder();
                            if (f.isDirectory()) {
                                fd.setName(f.getName());
                                fd.setPath(f.getPath());
                                fd.setFile(false);
                                fd.setChecked(false);
                                fd.setSize(0L);
                                fd.setImage(false);
                                fd.setVideo(false);
                                fd.setImage(folder_image);
                                folders.add(fd);
                            }
                        }
                    }
                    for (File f : Objects.requireNonNull(file.listFiles())) {
                        if (f.exists()) {
                            fd = new Folder();
                            if (f.isFile()) {
                                App.imageSelector(f);
                                fd.setName(f.getName());
                                fd.setPath(f.getPath());
                                fd.setFile(true);
                                fd.setChecked(false);
                                fd.setSize(f.length());
                                fd.setImage(file_image);
                                folders.add(fd);
                            }
                        }
                    }
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                try {
                    Folder fd;
                    File file = new File(Environment.getExternalStorageDirectory().getPath());
                    if (file.exists()) {
                        rootPath = file.getPath();
                        for (File f : Objects.requireNonNull(file.listFiles())) {
                            if (f.exists()) {
                                fd = new Folder();
                                if (f.isDirectory()) {
                                    App.imageSelector(f);
                                    fd.setImage(App.folder_image);
                                    fd.setName(f.getName());
                                    fd.setPath(f.getPath());
                                    fd.setFile(false);
                                    fd.setChecked(false);
                                    fd.setImage(false);
                                    fd.setVideo(false);
                                    //fd.setSize(SimpleUtils.getDirectorySize(f));
                                    fd.setSize(0L);
                                    folders.add(fd);
                                }
                            }
                        }
                        for (File f : Objects.requireNonNull(file.listFiles())) {
                            if (f.exists()) {
                                fd = new Folder();
                                if (f.isFile()) {
                                    fd.setName(f.getName());
                                    fd.setPath(f.getPath());
                                    fd.setFile(true);
                                    fd.setChecked(false);
                                    fd.setSize(f.length());
                                    App.imageSelector(f);
                                    fd.setImage(App.file_image);

                                    /*String ext = SimpleUtils.getExtension(f.getName());
                                    if(ext.equalsIgnoreCase("jpg") ||
                                            ext.equalsIgnoreCase("png") ||
                                            ext.equalsIgnoreCase("webp") ||
                                            ext.equalsIgnoreCase("bmp")) {
                                        fd.isImage = true;
                                        fd.isVideo = false;
                                    } else if (ext.equalsIgnoreCase("mp4") ||
                                            ext.equalsIgnoreCase("avi") ||
                                            ext.equalsIgnoreCase("mkv")) {
                                        fd.isImage = false;
                                        fd.isVideo = true;
                                    } else {
                                        fd.isImage = false;
                                        fd.isVideo = false;
                                    }*/
                                    folders.add(fd);
                                }
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }//2 catch
                //}// 1 catch
                return " ";
            }//doInBackground
            return " ";
        }

    }



    private static class UpdateListAsyncTask extends AsyncTask<Folder, Void, Void> {
        @Override
        protected void onPreExecute() {
            folders.clear();
        }
        @Override
        protected Void doInBackground(Folder... points) {

            try {
                Folder fd, dir;
                File file = null;
                if (points[0].getPath() != null) {
                    file = new File(points[0].getPath());
                }
                if (file != null) {
                    previousPath = file.getPath();
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            dir = new Folder();
                            dir.setName(file.getName());
                            dir.setPath(file.getPath());
                            dir.setFolders(new ArrayList<Folder>());

                            for (File f : Objects.requireNonNull(file.listFiles())) {
                                if (f.exists()) {
                                    fd = new Folder();
                                    fd.setName(f.getName());
                                    fd.setPath(f.getPath());
                                    if (f.isDirectory()) {
                                        fd.setFile(false);
                                        fd.setSize(SimpleUtils.getDirectorySize(f));
                                        fd.setImage(folder_image);
                                        fd.setImage(false);
                                        fd.setVideo(false);
                                    } else {
                                        fd.setFile(true);
                                        fd.setSize(f.length());
                                        imageSelector(f);
                                        fd.setImage(file_image);
                                        String ext = SimpleUtils.getExtension(f.getName());
                                        if(ext.equalsIgnoreCase("jpg") ||
                                                ext.equalsIgnoreCase("jpeg") ||
                                                ext.equalsIgnoreCase("png") ||
                                                ext.equalsIgnoreCase("webp") ||
                                                ext.equalsIgnoreCase("bmp")) {
                                            fd.setImage(true);
                                            fd.setVideo(false);
                                        } else if (ext.equalsIgnoreCase("mp4") ||
                                                ext.equalsIgnoreCase("avi") ||
                                                ext.equalsIgnoreCase("mkv")) {
                                            fd.setImage(false);
                                            fd.setVideo(true);
                                        } else {
                                            fd.setImage(false);
                                            fd.setVideo(false);
                                        }
                                    }
                                    folders.add(fd);
                                    dir.addFolderItem(fd);
                                }
                            }
                        }
                    }
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                npe.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //liveData.setValue(folders);
            //allPoints = liveData;
        }
    }


}


