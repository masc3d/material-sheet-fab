package org.deku.leoz.mobile

import com.facebook.stetho.Stetho

/**
 * Created by n3 on 13/12/2016.
 */
class DebugApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            log.info("Starting stetho")
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(
                                    Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(
                                    Stetho.defaultInspectorModulesProvider(this))
                            .build())

        }
    }
}