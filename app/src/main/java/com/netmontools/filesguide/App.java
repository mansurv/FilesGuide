package com.netmontools.filesguide;

import android.app.Application;
import android.os.Environment;

import com.netmontools.filesguide.ui.files.model.Folder;
import com.netmontools.filesguide.utils.SimpleUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class App extends Application {

    public static App instance;
    //private AppDatabase database;
    public static ArrayList<Folder> folders = new ArrayList<Folder>();
    public static String[] share = null;
    public static String rootPath, currentPath, previousPath;//, remoteRootPath, remotePreviousPath, remoteCurrentPath;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        folders.clear();
        ScanThread scanRootPath = new ScanThread();
        scanRootPath.start();
    }

    public static App getInstance() {
        return instance;
    }

    public class ScanThread extends Thread {
        @Override
        public void run() {
            doInBackground();
        }
    }

    protected void doInBackground(Void... voids) {

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
                                fd.setName(f.getName());
                                fd.setPath(f.getPath());
                                fd.setFile(false);
                                fd.setChecked(false);
                                fd.setImage(false);
                                fd.setVideo(false);
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
        }
    }//doInBackground
}