package sx.io

import sx.legacy.Disposable
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

/**
 * Utility class representing a temporary file which is removed on disposal
 * Created by masc on 29/06/16.
 * @property file File
 */
open class TemporaryFile
:
        Disposable
{
    val file: File

    /**
     * Creates temporary file from an existing file
     */
    constructor(file: File) {
        this.file = file
    }

    /**
     * Creates temporary file from an input stream
     * @param inputStream Input stream
     * @param temporaryPath Temporary files path
     * @param fileName Override filename, defaults to a random UUID
     * @param fileExtension Override filename extensino, defaults to '.temp'
     */
    constructor(inputStream: InputStream,
                temporaryPath: File,
                fileName: String = UUID.randomUUID().toString(),
                fileExtension: String = ".temp") {
        this.file = File(temporaryPath.absolutePath, "${fileName}${fileExtension}")

        FileOutputStream(this.file, false).use {
            inputStream.copyTo(it)
        }
    }

    override fun close() {
        if (file.exists())
            file.delete()
    }
}