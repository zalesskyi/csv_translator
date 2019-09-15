package ui.file_filters

import java.io.File
import javax.swing.filechooser.FileFilter

class CsvFilter : FileFilter() {

    companion object {
        private const val CSV_EXT = ".csv"
        private const val CSV_DESCRIPTION = "*$CSV_EXT"
    }

    override fun accept(f: File?) =
        f?.run {
            isDirectory || name.toLowerCase().endsWith(CSV_EXT)
        } ?: false

    override fun getDescription() = CSV_DESCRIPTION
}