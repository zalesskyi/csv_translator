package processing.modules

import io.reactivex.Single
import utils.XML_FILE_NAME
import java.io.File

interface FileModule {

    /**
     * Check, if there are strings.xml's with such locales.
     * If not, then create.
     *
     * @return [Single] with true, if there are no such files and they was created. False otherwise.
     */
    fun checkLocales(locales: List<String>, resDir: File): Single<Boolean>
}

class FileModuleImpl : FileModule {

    override fun checkLocales(locales: List<String>, resDir: File): Single<Boolean> =
        Single.fromCallable { checkLocalesInternal(locales, resDir) }

    private fun checkLocalesInternal(locales: List<String>, resDir: File): Boolean {
        var isNewCreated = false
        locales.forEach { valuesDirName ->
            var alreadyHave = false
            resDir.listFiles()?.forEach {
                if (it.isDirectory && it.name == valuesDirName) alreadyHave = true
            }
            if (!alreadyHave) {
                createValuesDir(resDir, valuesDirName)
                isNewCreated = true
            }
        }
        return isNewCreated
    }

    private fun createValuesDir(parentDir: File, name: String) {
        File("${parentDir.absolutePath}${File.separator}$name").takeUnless { it.exists() }?.run {
            mkdir()
            File("$absolutePath${File.separator}$XML_FILE_NAME").createNewFile()
        }
    }
}