package ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import extensions.safeLet

class TranslateDialog(private val project: Project) : DialogWrapper(true),
    TranslateFlowCallback {

    private var panel: TranslatePanel? = null

    private val presenter: TranslatePresenter = TranslatePresenterImpl(this)

    init {
        init()
        title = "CSV Translator"
    }

    override fun createCenterPanel() = TranslatePanel(project).also { panel = it }

    override fun doOKAction() {
        safeLet(panel?.csvFilePath, panel?.resFilePath) { csvPath, resPath ->
            presenter.startTranslateFlow(csvPath, resPath)
        }
    }

    override fun onPathError(path: String) = Unit

    override fun onNamingConflict(fieldName: String) = Unit

    override fun onFinish() = Unit
}