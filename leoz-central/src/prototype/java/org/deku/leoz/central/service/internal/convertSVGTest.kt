package org.deku.leoz.central.service.internal

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.config.ParcelServiceConfiguration
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.ParcelServiceV1
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import sx.time.toTimestamp
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import javax.inject.Inject
import javax.ws.rs.core.MediaType
import org.deku.leoz.node.Storage
import org.h2.util.ToDateParser.toDate
import java.awt.Color
import java.io.FileOutputStream
import sx.time.toDate
import sx.time.toLocalDate
import java.io.FileInputStream
import java.util.Spliterators.iterator

@Category(StandardTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        ParcelServiceConfiguration::class,
        ParcelServiceConfiguration.Settings::class,
        org.deku.leoz.central.service.internal.ParcelServiceV1::class,
        org.deku.leoz.central.service.internal.UserService::class,
        org.deku.leoz.central.service.internal.BagService::class,
        org.deku.leoz.node.service.pub.RoutingService::class,
        org.deku.leoz.node.service.internal.StationService::class
))


class convertSVGTest {


    @Inject
    private lateinit var storage: Storage

    @Inject
    lateinit var parcelService: org.deku.leoz.central.service.internal.ParcelServiceV1

    @Test
    fun SVG_2_JPG_ConvertTest() {
        //transSvg2Jpg
    }

    @Test
    fun Bulg_SVG_convert() {
        var sigFilename = ""
        val path = "/Users/JT/leoz/workTmp/2017/sb/11/29/"
        println(path)

        File(path).walk().forEach {
            println(it.absoluteFile)
            sigFilename = saveImage(it.absoluteFile, Date(), Location.SB, null, UUID.randomUUID().toString(), null, "SVG", Location.SB_Original)

        }
    }

    fun saveImage(exfile: File, date: Date, location: Location, image: String?, number: String, userId: Int?, mimetype: String, locationOriginal: Location?): String {
        if (!exfile.isFile)
            (return "")

        val keepOriginal = (locationOriginal != null) //true else false
        val pathMobile = storage.mobileDataDirectory.toPath()

        val addInfo = userId.toString()//.substringBefore("-")
        val mobileFilename = FileName(
                value = number,
                date = date,
                type = location,
                basePath = pathMobile,
                additionalInfo = addInfo
        )
        val relPathMobile = mobileFilename.getPath()

        val path = storage.workTmpDataDirectory.toPath()

        val mobileWorkFilename = FileName(
                value = number,
                date = date,
                type = location,
                basePath = path,
                additionalInfo = addInfo
        )
        val relPath = mobileWorkFilename.getPath()

        var fileExtension: String
        when (mimetype) {
            MediaType.APPLICATION_SVG_XML -> fileExtension = "svg"
            else -> fileExtension = "jpg"
        }
        val file = mobileFilename.getFilenameWithoutExtension() + "." + fileExtension
        val pathFile = relPath.resolve(file).toFile().toPath()
        val pathFileMobile = relPathMobile.resolve(file).toFile().toPath()

        try {
            var imgPath = exfile.toPath()

            val inFile = imgPath.toFile()
            val outFile = inFile.replaceExtension("jpg")

            FileInputStream(inFile).use { input ->
                FileOutputStream(outFile).use { output ->
                    this.parcelService.transcodeSvg2Jpg(input, output)
                }
            }

            imgPath = outFile.toPath()

            val bmpFile = imgPath.toFile().parentFile.toPath()
                    .resolve(imgPath.toFile().nameWithoutExtension + ".bmp").toFile()

            writeAsBMP(imgPath, bmpFile.toPath())

            return ""
        } catch (e: Exception) {
            return ""
        }
    }

    fun File.replaceExtension(extension: String): File =
            File(this.parentFile, this.nameWithoutExtension + "." + extension)

    fun writeAsBMP(pathFile: java.nio.file.Path, pathBmpFile: java.nio.file.Path): Boolean {
        try {
            val bufferedImageLoad = ImageIO.read(File(pathFile.toUri())) //ImageIO.read(ByteArrayInputStream(img))
            val fileObj = File(pathBmpFile.toUri())

            val bufferedImage = BufferedImage(bufferedImageLoad.width, bufferedImageLoad.height, BufferedImage.TYPE_BYTE_BINARY)

            for (y in 0..bufferedImageLoad.height - 1) {
                for (x in 0..bufferedImageLoad.width - 1) {
                    bufferedImage.setRGB(x, y, bufferedImageLoad.getRGB(x, y))
                }
            }
            return ImageIO.write(bufferedImage, "bmp", fileObj)

        } catch (e: Exception) {
            return false
        }


    }
}
