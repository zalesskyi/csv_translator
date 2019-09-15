package ui.base

import com.intellij.openapi.project.Project
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

abstract class BaseTranslatePanel(private val project: Project) : JPanel() {

    companion object {
        private val CSV_LABEL_COORDINATES = Rectangle(10, 10, 310, 16)
        private val CSV_FIELD_COORDINATES = Rectangle(10, 30, 260, 30)
        private val PICK_CSV_PATH_COORDINATES = Rectangle(275, 30, 30, 30)
        private val RES_LABEL_COORDINATES = Rectangle(10, 80, 310, 16)
        private val RES_FIELD_COORDINATES = Rectangle(10, 100, 260, 30)
        private val PICK_RES_COORDINATES = Rectangle(275, 100, 30, 30)

        private val PREFERRED_SIZE = Dimension(320, 150)
    }

    protected lateinit var tfCsv: JTextField
    protected lateinit var bPickCsv: JButton
    protected lateinit var tfRes: JTextField
    protected lateinit var bPickRes: JButton

    abstract fun onPickCsvFile()

    abstract fun onPickResFolder()

    protected fun setupUi() {
        layout = null

        JLabel("Path to CSV-file with translations:").apply {
            bounds = CSV_LABEL_COORDINATES
            this@BaseTranslatePanel.add(this)
        }

        tfCsv = JTextField().apply {
            bounds = CSV_FIELD_COORDINATES
            this@BaseTranslatePanel.add(this)
        }

        bPickCsv = JButton().apply {
            bounds = PICK_CSV_PATH_COORDINATES
            text = "..."
            addActionListener { onPickCsvFile() }
            this@BaseTranslatePanel.add(this)
        }

        JLabel("Path to module res folder:").apply {
            bounds = RES_LABEL_COORDINATES
            this@BaseTranslatePanel.add(this)
        }

        tfRes = JTextField().apply {
            bounds = RES_FIELD_COORDINATES
            text = project.basePath
            this@BaseTranslatePanel.add(this)
        }

        bPickRes = JButton().apply {
            bounds = PICK_RES_COORDINATES
            text = "..."
            addActionListener { onPickResFolder() }
            this@BaseTranslatePanel.add(this)
        }
    }

    override fun getPreferredSize() = PREFERRED_SIZE
}