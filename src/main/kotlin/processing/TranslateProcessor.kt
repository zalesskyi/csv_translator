package processing

import extensions.safeLet
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Single
import processing.models.TranslationModel
import processing.modules.*
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

    private val xmlModule: XmlModule = XmlModuleImpl()

    private val loadTranslationTransformer = FlowableTransformer<TranslationModel, TranslationModel> {
        it.flatMap { translation ->
            loadTranslation(translation)
        }
    }

    override fun doTranslateFlow(csvFile: File, resDir: File): Single<Unit> =
        csvModule.getLocales(csvFile)
            .flatMap { fileModule.indexLocales(it, resDir) }
            .flattenAsFlowable { it }
            .flatMap { newFile ->
                xmlModule.prepare(newFile).toFlowable()
            }
            .toList()
            .flatMap { parseCsvFile(csvFile) }
            .flatMap { translations ->
                addTranslations(translations)
            }
            .flatMap {
                reformatXmlFiles()
            }

    private fun parseCsvFile(csv: File): Single<List<TranslationModel>> =
        csvModule.parse(csv)

    private fun addTranslations(translations: List<TranslationModel>): Single<Unit> =
        Single.just(translations)
            .flattenAsFlowable { it }
            .compose(loadTranslationTransformer)
            .toList()
            .map { Unit }

    private fun loadTranslation(translation: TranslationModel) =
        Flowable.just(translation)
            .map { it.sources.toList() }
            .flatMapIterable { it }
            .flatMap { (locale, translate) ->
                safeLet(fileModule.filesIndexMap[locale], translate) { xmlFile, source ->
                    xmlModule.loadTranslation(xmlFile, translation.name, source).toFlowable()
                } ?: Flowable.just(Unit)
            }
            .toList()
            .toFlowable()
            .map { translation }

    private fun reformatXmlFiles() =
        Single.just(fileModule.filesIndexMap.values)
            .flattenAsFlowable { it }
            .flatMap { xmlFile -> xmlModule.normalize(xmlFile).toFlowable() }
            .toList()
            .map { Unit }
}