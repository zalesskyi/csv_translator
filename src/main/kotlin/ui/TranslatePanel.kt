package ui

import com.intellij.openapi.project.Project
import ui.base.BaseTranslatePanel
import ui.file_filters.CsvFilter
import ui.file_filters.DirectoryFilter
import utils.EMPTY_STRING
import utils.FileUtils

class TranslatePanel(project: Project) : BaseTranslatePanel(project) {

    init {
        setupUi()
    }

    val csvFilePath: String?
        get() = tfCsv.text

    val resFilePath: String?
        get() = tfRes.text

    override fun onPickCsvFile() {
        FileUtils.pickFile(filter = CsvFilter())?.let { pickedPath ->
            tfCsv.text = pickedPath
        }
    }

    override fun onPickResFolder() {
        FileUtils.pickFile(tfRes.text ?: EMPTY_STRING, filter = DirectoryFilter())?.let { pickedPath ->
            tfRes.text = pickedPath
        }
    }
}