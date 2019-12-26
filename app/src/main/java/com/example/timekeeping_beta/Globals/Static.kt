package com.example.timekeeping_beta.Globals

import com.example.timekeeping_beta.Globals.Models.listOption

object Static {

    val OS_VERSION = android.os.Build.VERSION.SDK_INT

    const val ROOT_URL = "http://timekeeping.caimitoapps.com:8081/"
    //const val ROOT_URL = "http://192.168.137.1/timekeeping/tk/"
    //const val ROOT_URL = "http://192.168.1.53/timekeeping/tk/"
    const val VERSION_ROOT_URL = "http://timekeeping.caimitoapps.com:999"

    const val APP_CODE = "TKAPP";
    const val APP_VERSION = 1.0;

    var listOptions = ArrayList<listOption>()

    init {

        listOptions.add(listOption("10", 10))
        listOptions.add(listOption("20", 20))
        listOptions.add(listOption("30", 30))
    }
}