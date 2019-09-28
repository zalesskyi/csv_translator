package processing.modules

import extensions.findFile
import io.reactivex.Single
import utils.XML_FILE_NAME
import java.io.File

interface FileModule {

    /**
     * Map of strings.xml.
     *
     * Key: Locale -> Value: strings.xml file related to locale
     */
    val filesIndexMap: MutableMap<String, File>

    /**
     * Check, if there are strings.xml's with such locales.
     * If not, then create.
     *
     * @return [Single] with list of new created files.
     */
    fun indexLocales(locales: List<String>, resDir: File): Single<List<File>>
}

class FileModuleImpl : FileModule {

    override val filesIndexMap: MutableMap<String, File> = mutableMapOf()

    override fun indexLocales(locales: List<String>, resDir: File): Single<List<File>> =
        Single.fromCallable { indexLocalesInternal(locales, resDir) }

    private fun indexLocalesInternal(locales: List<String>, resDir: File): List<File> {
        val newFilesList = mutableListOf<File?>()
        locales.forEach { valuesDirName ->
            var alreadyHave = false
            resDir.listFiles()?.forEach {
                if (it.isDirectory && it.name == valuesDirName) {
                    alreadyHave = true
                    it.findFile(XML_FILE_NAME)?.let { xmlFile ->
                        filesIndexMap[valuesDirName] = xmlFile
                    }
                }
            }
            if (!alreadyHave) {
                createValuesDir(resDir, valuesDirName).let { createdFile ->
                    createdFile?.let { filesIndexMap[valuesDirName] = it }
                    newFilesList.add(createdFile)
                }
            }
        }
        return newFilesList.filterNotNull()
    }

    /**
     * Create a new dir for new locale.
     *
     * @return new created strings.xml [File] or null
     */
    private fun createValuesDir(parentDir: File, name: String): File? =
        File("${parentDir.absolutePath}${File.separator}$name").takeUnless { it.exists() }?.run {
            mkdir()
            File("$absolutePath${File.separator}$XML_FILE_NAME").apply {
                createNewFile()
            }
        }
}