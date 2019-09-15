package processing

import exceptions.ErrorCode
import exceptions.TranslateFlowException
import io.reactivex.Single
import processing.models.TranslationModel
import processing.modules.CsvModule
import processing.modules.CsvModuleImpl
import processing.modules.FileModule
import processing.modules.FileModuleImpl
import java.io.File

interface TranslateProcessor {

    /**
     * Do translate flow.
     *
     * @return [Single] with Unit on success, or error on failure.
     */
    fun doTranslateFlow(csvFile: File, resDir: File): Single<Unit>
}

class TranslateProcessorImpl : TranslateProcessor {

    private val csvModule: CsvModule = CsvModuleImpl()

    private val fileModule: FileModule = FileModuleImpl()

    override fun doTranslateFlow(csvFile: File, resDir: File): Single<Unit> =
        csvModule.getLocales(csvFile)
            .flatMap { fileModule.checkLocales(it, resDir) }
            .map { Unit }

    private fun parseCsvFile(csv: File): Single<List<TranslationModel>> =
        csvModule.parse(csv)
            .onErrorResumeNext() {
                if (it is IllegalArgumentException) {
                    Single.error(TranslateFlowException(ErrorCode.byValue(it.message), null))
                } else {
                    Single.error(it)
                }
            }
}