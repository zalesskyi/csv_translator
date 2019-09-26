package ui

import exceptions.ErrorCode
import exceptions.TranslateFlowException
import io.reactivex.Single
import processing.TranslateProcessor
import processing.TranslateProcessorImpl
import java.io.File
import java.lang.ref.WeakReference

interface TranslatePresenter {

    fun startTranslateFlow(pathToCsv: String, pathToRes: String)
}

class TranslatePresenterImpl(callback: TranslateFlowCallback) : TranslatePresenter {

    private val callbackRef = WeakReference(callback)

    private val processor: TranslateProcessor = TranslateProcessorImpl()

    override fun startTranslateFlow(pathToCsv: String, pathToRes: String) {
        Single.just(pathToCsv to pathToRes)
            .flatMap { (csvPath, resPath) ->
                convertPath(csvPath)?.let { csvFile ->
                    convertPath(resPath)?.takeIf { it.isDirectory }?.let { resDir ->
                        processor.doTranslateFlow(csvFile, resDir)
                    } ?: Single.error(TranslateFlowException(ErrorCode.FILE_NOT_FOUND, resPath))
                } ?: Single.error(TranslateFlowException(ErrorCode.FILE_NOT_FOUND, csvPath))
            }
            .subscribe ({
                callbackRef.get()?.onFinish()
            }, {
                callbackRef.get()?.run {
                    (it as? TranslateFlowException)?.let { error ->
                        when (error.errorCode) {
                            ErrorCode.FILE_NOT_FOUND -> error.message?.let { onPathError(it) }
                            ErrorCode.CSV_NAME_FIELD_NOT_FOUND, ErrorCode.CSV_LOCALE_FIELD_NOT_FOUND -> onInvalidCSVStructure()
                            else -> onFinish()
                        }
                    }
                }
            })
    }

    /**
     * @param path path to file
     *
     * @return File if it exists, or null
     */
    private fun convertPath(path: String) =
        File(path).takeIf { it.exists() }
}