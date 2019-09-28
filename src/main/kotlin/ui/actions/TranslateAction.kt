package ui.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ui.TranslateDialog

class TranslateAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            TranslateDialog(project).show()
        }
    }
}