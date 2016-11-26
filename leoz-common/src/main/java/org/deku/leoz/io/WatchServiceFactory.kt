package org.deku.leoz.io

import com.barbarysoftware.watchservice.WatchableFile
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.WatchService
import java.nio.file.Watchable

/**
 * Factory for WatchService and Watchable instances
 * Created by masc on 22/03/16.
 */
object WatchServiceFactory {
    /**
     * Creates watchable for a file and referring watch service
     * @param file File
     * @param watchService Watch service
     */
    fun newWatchable(file: File, watchService: WatchService): Watchable {
        if (watchService is com.barbarysoftware.watchservice.AbstractWatchService) {
            return WatchableFile(file)
        } else {
            return file.toPath()
        }
    }

    /**
     * Create appropriate watch service for this platform
     */
    fun newWatchService(): WatchService {
        if (SystemUtils.IS_OS_MAC) {
            // Provide barbary watch service on OSX as the default implementation is polling and buggy too.
            return com.barbarysoftware.watchservice.WatchServiceFactory.newWatchService()
        } else {
            return FileSystems.getDefault().newWatchService()
        }
    }
}