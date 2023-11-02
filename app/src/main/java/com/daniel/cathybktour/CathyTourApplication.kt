package com.daniel.cathybktour

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp //表示整個應用程式都要使用 Hilt 進行注入。它會初始化所需的 Dagger/Hilt 相關組件。
class CathyTourApplication:Application() {



}