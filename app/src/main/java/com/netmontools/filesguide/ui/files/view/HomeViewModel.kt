package com.netmontools.filesguide.ui.files.view

import android.app.Application
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netmontools.filesguide.ui.files.model.Folder
import com.netmontools.lookatnet.ui.local.repository.LocalRepository
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val folder: Folder? = null
    private val repository: LocalRepository
    lateinit var allPoints: LiveData<List<Folder>>
    fun delete(folder: Folder?) {
        viewModelScope.launch {repository.delete(folder)}
    }
    fun update(folder: Folder?) {
        viewModelScope.launch {repository.update(folder)}
    }
    val id: UUID
        get() = folder!!.id
    val name: String?
        get() = if (!TextUtils.isEmpty(folder!!.name)) folder.name else ""
    val path: String?
        get() = if (!TextUtils.isEmpty(folder!!.path)) folder.path else ""
    val size: Long
        get() = folder!!.size
    val image: Drawable
        get() = folder!!.image

    init {
        repository = LocalRepository(application)
        try {
            allPoints = repository.allPoints!!
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
        }
    }


}