package processing.models

data class TranslationModel(val name: String,
                            // {config -> translation}, example: {"da" -> {"danish translation"}}
                            val sources: Map<String, String?>)