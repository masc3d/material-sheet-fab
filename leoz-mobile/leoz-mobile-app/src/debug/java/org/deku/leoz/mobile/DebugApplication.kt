package org.deku.leoz.mobile

import com.facebook.stetho.Stetho
import org.slf4j.LoggerFactory

/**
* Created by masc on 13/12/2016.
*/
class DebugApplication : Application() {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

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