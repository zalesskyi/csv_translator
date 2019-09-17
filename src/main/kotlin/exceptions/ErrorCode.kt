package exceptions

enum class ErrorCode(private val code: String) {
    FILE_NOT_FOUND("file_not_found"),
    CSV_NAME_FIELD_NOT_FOUND("csv_name_not_found"),
    CSV_LOCALE_FIELD_NOT_FOUND("csv_locale_not_found"),
    UNKNOWN("unknown");

    companion object {

        fun byValue(value: String?) = values().firstOrNull { value == it.code } ?: UNKNOWN
    }


    operator fun invoke() = code
}