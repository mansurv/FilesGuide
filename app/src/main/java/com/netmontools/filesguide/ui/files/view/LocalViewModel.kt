package com.netmontools.filesguide.ui.files.view

import android.app.Application
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.netmontools.filesguide.ui.files.model.Folder
import com.netmontools.lookatnet.ui.local.repository.LocalRepository
import kotlinx.coroutines.launch
import java.util.UUID

class LocalViewModel(application: Application) : AndroidViewModel(application) {
    private val folder: Folder? = null
    private val repository: LocalRepository
    var allPoints: LiveData<List<Folder>>

    fun update(folder: Folder?) {
        viewModelScope.launch {repository.update(folder)}
    }

    fun scan(folder: Folder?) {
        viewModelScope.launch {repository.scan(folder)}
    }
    val id: UUID
        get() = folder!!.id
    val name: String
        get() = if (!TextUtils.isEmpty(folder!!.name)) folder.name else ""
    val path: String
        get() = if (!TextUtils.isEmpty(folder!!.path)) folder.path else ""
    val size: Long
        get() = folder!!.size
    val image: Drawable
        get() = folder!!.image

    init {
        repository = LocalRepository(application)
            allPoints = repository.allPoints!!
    }


}