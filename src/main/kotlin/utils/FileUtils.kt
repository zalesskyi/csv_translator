package utils

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

object FileUtils {

    /**
     * Pick the file from File System.
     *
     * @param path current directory path, or null
     * @param filter file filter
     * @param fileSelectionMode selection mode
     *
     * @return full path to picked file or null if no file was picked
     */
    fun pickFile(path: String? = null,
                 filter: FileFilter? = null,
                 fileSelectionMode: Int? = null): String? =
        JFileChooser().apply {
            path?.let { currentDirectory = File(it) }
            fileSelectionMode?.let { setFileSelectionMode(it) }
            filter?.let { fileFilter = it }
        }.run {
            showDialog(null, "Choose").takeIf {
                it == JFileChooser.APPROVE_OPTION
            }?.let { selectedFile.absolutePath }
        }
}