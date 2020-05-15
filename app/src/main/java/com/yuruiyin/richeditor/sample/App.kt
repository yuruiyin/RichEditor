package com.yuruiyin.richeditor.sample

import android.app.Application
import com.yuruiyin.richeditor.utils.RichEditorGlobalHelper

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        RichEditorGlobalHelper.setIsLogEnable(BuildConfig.DEBUG)
    }

}