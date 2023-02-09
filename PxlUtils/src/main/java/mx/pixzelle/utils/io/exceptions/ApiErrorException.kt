package mx.pixzelle.utils.io.exceptions
import mx.pixzelle.utils.enums.HTTPErrorCode
import java.io.IOException

class ApiErrorException(
    val code: HTTPErrorCode?,
    message: String = "",
    val details: List<String>? = null,
) : IOException(message) {

}