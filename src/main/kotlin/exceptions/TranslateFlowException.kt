package exceptions

class TranslateFlowException(val errorCode: ErrorCode, message: String?): RuntimeException(message)