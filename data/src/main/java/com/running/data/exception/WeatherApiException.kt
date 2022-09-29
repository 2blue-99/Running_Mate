package com.jaehyeon.data.exception

sealed class WeatherApiException(message: String): Throwable(message = message) {

    class ApplicationErrorException(message: String): WeatherApiException(message)
    class DataBaseErrorException(message: String): WeatherApiException(message)
    class NoDataErrorException(message: String): WeatherApiException(message)
    class HttpErrorException(message: String): WeatherApiException(message)
    class ServiceTimeOutException(message: String): WeatherApiException(message)
    class InvalidRequestParameterErrorException(message: String): WeatherApiException(message)
    class NoMandatoryRequestParametersErrorException(message: String): WeatherApiException(message)
    class NoOpenApiServiceErrorException(message: String): WeatherApiException(message)
    class ServiceAccessDeniedErrorException(message: String): WeatherApiException(message)
    class TemporarilyDisableTheServiceKeyErrorException(message: String): WeatherApiException(message)
    class LimitedNumberOfServiceRequestsExceedsErrorException(message: String): WeatherApiException(message)
    class ServiceKeyIsNotRegisteredErrorException(message: String): WeatherApiException(message)
    class DeadlineHasExpiredErrorException(message: String): WeatherApiException(message)
    class UnregisteredIpErrorException(message: String): WeatherApiException(message)
    class UnSignedCallErrorException(message: String): WeatherApiException(message)
    class UnknownErrorException(message: String): WeatherApiException(message)

}