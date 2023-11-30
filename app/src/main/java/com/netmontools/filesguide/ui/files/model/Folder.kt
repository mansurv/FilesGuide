package com.netmontools.filesguide.ui.files.model

import android.graphics.drawable.Drawable
import java.util.UUID


class Folder {

    var id = UUID.randomUUID()
    lateinit var name: String
    lateinit var path: String
    var size: Long = 0
    lateinit var image: Drawable
    var isFile = false
    var isChecked = false
    var isImage = false
    var isVideo = false

    var folders = ArrayList<Folder>()

    fun getIdItem(): UUID {
        return id
    }

    fun setIdItem(id: UUID?) {
        this.id = id
    }

    fun getItemSize(): Long {
        return size
    }

    fun setItemSize(size: Long) {
        this.size = size
    }

    fun getNameItem(): String {
        return name
    }

    fun setNameItem(name: String) {
        this.name = name
    }

    fun getPathItem(): String {
        return path
    }

    fun setPathItem( path: String) {
        this.path = path
    }

    fun addFolderItem(f: Folder) {
        folders.add(f)
    }

    fun getItemFolders(): ArrayList<Folder> {
        return folders
    }

    fun getFolderItem(id: UUID): Folder? {
        for (f in folders) {
            if (f.getIdItem() === id) return f
        }
        return null
    }

    fun getImageItem(): Drawable {
        return image
    }

    fun setImageItem(image: Drawable) {
        this.image = image
    }

}
