package exceptions

class TranslateFlowException(val errorCode: ErrorCode, message: String? = null): RuntimeException(message)