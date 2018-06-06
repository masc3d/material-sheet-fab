package org.deku.leoz

/**
 * Some common and custom leoz mime types
 */
enum class MimeType(val value: String, val extension: String) {
    JPEG("image/jpeg", ".jpg"),

    // Leoz specific mime types.
    // ATTENTION: reflects tika custom mimetype configuration in `resources/org/apache/tika/mime/custom-mimetypes.xml`
    LEOZ_DIAGNOSTIC_ZIP("application/leoz-diagnostic-zip", ".diag.zip")
}
