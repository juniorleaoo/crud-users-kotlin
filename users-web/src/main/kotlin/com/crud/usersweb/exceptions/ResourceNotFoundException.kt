package com.crud.usersweb.exceptions

class ResourceNotFoundException(
    message: String,
    val error: APIErrorEnum = APIErrorEnum.NOT_FOUND
) : Exception(message) {

}