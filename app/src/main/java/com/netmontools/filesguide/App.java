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
    public static String rootPath, previousPath;//, remoteRootPath, remotePreviousPath, remoteCurrentPath;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        new PopulateDbAsyncTask().execute();

        //database = Room.databaseBuilder(this, AppDatabase.class, "database")
        //        .addMigrations(AppDatabase.MIGRATION_1_2)
        //        .build();

        //host_image = ContextCompat.getDrawable(this, R.drawable.ic_desktop_windows_black_24dp);
       //folder_image = ContextCompat.getDrawable(this, R.drawable.baseline_folder_yellow_24);
        //file_image = ContextCompat.getDrawable(this, R.drawable.ic_file);

    }

    public static App getInstance() {
        return instance;
    }

    //public AppDatabase getDatabase() {
    //    return database;
    //}


    class PopulateDbAsyncTask extends AsyncTask<Void, Void, String> {

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
                            //fd.setImage(folder_image);
                            folders.add(fd);
                        }
                    }
                }
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    if (f.exists()) {
                        fd = new Folder();
                        if (f.isFile()) {
                            //App.imageSelector(f);
                            fd.setName(f.getName());
                            fd.setPath(f.getPath());
                            fd.setFile(true);
                            fd.setChecked(false);
                            fd.setSize(f.length());
                            //fd.setImage(file_image);
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
                                //App.imageSelector(f);
                                //fd.setImage(App.folder_image);
                                fd.setName(f.getName());
                                fd.setPath(f.getPath());
                                fd.setFile(false);
                                fd.setChecked(false);
                                fd.setImage(false);
                                fd.setVideo(false);
                                //fd.setSize(SimpleUtils.getDirectorySize(f));
                                fd.setSize(0L);
                                folders.add(fd);
                            } else if(f.isFile()) {
                                fd = new Folder();
                                fd.setName(f.getName());
                                fd.setPath(f.getPath());
                                fd.setFile(true);
                                fd.setChecked(false);
                                fd.setSize(f.length());
                                //App.imageSelector(f);
                                //fd.setImage(App.file_image);

                                String ext = SimpleUtils.getExtension(f.getName());
                                if (ext.equalsIgnoreCase("jpg") ||
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
                                //App.imageSelector(f);
                                //fd.setImage(App.file_image);

                                String ext = SimpleUtils.getExtension(f.getName());
                                if(ext.equalsIgnoreCase("jpg") ||
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
}