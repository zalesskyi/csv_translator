package processing.modules

import exceptions.ErrorCode
import io.reactivex.Single
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import processing.models.TranslationModel
import utils.CSV_DELIMITER
import utils.CSV_NAME_FIELD
import utils.CSV_NAME_POSITION
import utils.EMPTY_STRING
import java.io.File
import java.io.FileReader

interface CsvModule {

    /**
     * Parse csv-file.
     * Can throw [IllegalArgumentException], if [csvFile] has not correct structure.
     *
     * @param csvFile file to parse
     *
     * @return [Single] with results list.
     */
    fun parse(csvFile: File): Single<List<TranslationModel>>

    /**
     * Get list of locales in csv-file.
     *
     * @param csvFile file to parse
     *
     * @return [Single] with list of locales.
     */
    fun getLocales(csvFile: File): Single<List<String>>
}

class CsvModuleImpl : CsvModule {

    override fun parse(csvFile: File): Single<List<TranslationModel>> =
        Single.just(FileReader(csvFile))
            .map { CSVParser(it, CSVFormat.DEFAULT.withDelimiter(CSV_DELIMITER).withHeader()) }
            .map { parser -> processParsing(parser) }

    override fun getLocales(csvFile: File): Single<List<String>> =
        Single.just(CSVParser(FileReader(csvFile), CSVFormat.DEFAULT.withDelimiter(CSV_DELIMITER).withHeader()))
            .map { parser -> parser.also { checkName(it) }.headerNames.drop(CSV_NAME_POSITION + 1) }  // exclude name field.

    private fun processParsing(parser: CSVParser) =
        parser.also { checkName(it) }
            .mapNotNull { record -> record.toTranslationModel(parser) }

    @Throws(IllegalArgumentException::class)
    private fun checkName(parser: CSVParser) {
        if (parser.headerNames[CSV_NAME_POSITION].toLowerCase() != CSV_NAME_FIELD)
            throw IllegalArgumentException(ErrorCode.CSV_NAME_FIELD_NOT_FOUND())
    }

    private fun checkLocales(parser: CSVParser) {

    }

    private fun CSVRecord.toTranslationModel(parser: CSVParser): TranslationModel? =
        parser.headerNames?.let { headerNames ->
            val sourcesMap = mutableMapOf<String, String?>()
            for (i in 1 until headerNames.count()) {
                sourcesMap[headerNames[i]] = getOrNull(i)
            }
            TranslationModel(get(CSV_NAME_POSITION), sourcesMap)
        }

    private fun CSVRecord.getOrNull(i: Int) =
        get(i).takeUnless { it == EMPTY_STRING }
}