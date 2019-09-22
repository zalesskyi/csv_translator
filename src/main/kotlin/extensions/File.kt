package extensions

import java.io.File

fun File.findFile(name: String): File? = listFiles()?.find { it.name == name }