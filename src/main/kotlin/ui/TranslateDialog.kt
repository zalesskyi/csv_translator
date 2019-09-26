package ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.ui.DialogWrapper
import extensions.safeLet
import javax.swing.JOptionPane

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

    override fun onPathError(path: String) {
        showError("The path \"$path\" is invalid")
    }

    override fun onInvalidCSVStructure() {
        showError("Structure of CSV file is invalid. Check plugin description, to see how it should look like.")
    }

    override fun onFinish() {
        project.guessProjectDir()?.refresh(true, true)
        super.doOKAction()
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE)
    }
}