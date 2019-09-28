package processing.modules

import io.reactivex.Single
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import utils.STRING_XML_ATTRIBUTE_NAME
import utils.STRING_XML_TAG_NAME
import utils.XML_HEADER
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

interface XmlModule {

    fun loadTranslation(file: File, name: String, translation: String): Single<Unit>

    /**
     * Prepare xml file to using.
     * Add header.
     *
     * @param xmlFile file to prepare
     */
    fun prepare(xmlFile: File): Single<Unit>

    /**
     * Format [xmlFile] to readable view.
     */
    fun normalize(xmlFile: File): Single<Unit>
}

class XmlModuleImpl : XmlModule {

    override fun loadTranslation(file: File, name: String, translation: String) =
        Single.just(DocumentBuilderFactory.newInstance().newDocumentBuilder())
            .map { it.parse(file) }
            .map { document ->
                document.also {
                    addElement(it, name, translation)
                }
            }
            .map { updateXmlFile(it, file) }

    override fun prepare(xmlFile: File) =
        Single.just(DocumentBuilderFactory.newInstance().newDocumentBuilder())
            .map { it.newDocument() }
            .map { document ->
                document.apply {
                    appendChild(createElement(XML_HEADER))
                }
            }
            .map { updateXmlFile(it, xmlFile) }

    override fun normalize(xmlFile: File) =
        Single.just(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile))
            .map { document ->
                TransformerFactory.newInstance().newTransformer().let { transformer ->
                    removeIndents(document)
                    transformer.transform(DOMSource(document), StreamResult(xmlFile))
                }
            }
            .map { DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile) }
            .map { document ->
                TransformerFactory.newInstance().newTransformer().run {
                    setOutputProperty(OutputKeys.ENCODING, "UTF-16")
                    setOutputProperty(OutputKeys.INDENT, "yes")
                    setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
                    transform(DOMSource(document), StreamResult(xmlFile))
                }
            }

    private fun updateXmlFile(document: Document, file: File) {
        TransformerFactory.newInstance().newTransformer().run {
            setOutputProperty(OutputKeys.ENCODING, "UTF-16")
            setOutputProperty(OutputKeys.INDENT, "no")
            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
            transform(DOMSource(document), StreamResult(file))
        }
    }

    private fun addElement(document: Document, name: String, translation: String) {
        document.run {
            createElement(STRING_XML_TAG_NAME).let { element ->
                element.setAttributeNode(createAttribute(STRING_XML_ATTRIBUTE_NAME).apply {
                    value = name
                })
                element.appendChild(createTextNode(translation))
                documentElement.appendChild(element)
            }
        }
    }

    private fun removeIndents(document: Document) {
        (XPathFactory.newInstance().newXPath()
            .evaluate("//text()[normalize-space(.) = '']", document, XPathConstants.NODESET) as? NodeList)?.let { emptiesList ->

            emptiesList.forEach {
                it?.parentNode?.removeChild(it)
            }
        }
    }

    private fun NodeList.forEach(action: (Node?) -> Unit) {
        for (i in 0 until length) {
            action(item(i))
        }
    }
}