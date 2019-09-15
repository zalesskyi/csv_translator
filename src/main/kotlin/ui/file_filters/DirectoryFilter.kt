package ui.file_filters

import utils.EMPTY_STRING
import java.io.File
import javax.swing.filechooser.FileFilter

class DirectoryFilter : FileFilter() {

    override fun accept(f: File?) = f?.isDirectory ?: false

    override fun getDescription() = EMPTY_STRING
}