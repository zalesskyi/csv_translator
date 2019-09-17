package ui

interface TranslateFlowCallback {

    /**
     * Error, while processing path.
     * Can't find file/directory by this path.
     *
     * @param path invalid path
     */
    fun onPathError(path: String)

    /**
     * Called on xml naming conflict.
     *
     * @param fieldName name, that already exists in XML file.
     */
    fun onXMLNamingConflict(fieldName: String)

    /**
     * Called if there is invalid structure of scv file.
     * Field "name" is missed.
     */
    fun onInvalidCSVStructure()

    /**
     * Called, when processing is finished.
     */
    fun onFinish()
}