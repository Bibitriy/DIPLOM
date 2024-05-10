package com.example.witte.parseSiteWithExcel


data class SiteScheduleUnit(
    val downloadName: String,
    val date: String,
    val downloadLink: String,
    var extension: String? = null

) {
    fun getLink(): String {
        return downloadLink
    }

    fun getName():String {
        return downloadName
    }

    fun getFileExtension(): String {
        if (extension == null) {
            extension = downloadLink.substringAfterLast('.', "")
        }

        if (extension == null) {
            throw EmptyFileExtensionError(downloadLink)
        }

        when (extension) {
            "xls" -> return extension!!
            "xlsx" -> return extension!!
            "docx" -> return extension!!
            else -> throw DownloadFileExtensionError(extension!!)
        }

    }

    class EmptyFileExtensionError(message: String): Exception(message){}

    class DownloadFileExtensionError(message: String): Exception(message){}


    override fun toString(): String {
        return downloadName
    }
}
