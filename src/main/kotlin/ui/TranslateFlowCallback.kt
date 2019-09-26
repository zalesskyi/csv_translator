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
     * Called if there is invalid structure of csv file.
     * Field "name" is missed.
     */
    fun onInvalidCSVStructure()

    /**
     * Called, when processing is finished.
     */
    fun onFinish()
}