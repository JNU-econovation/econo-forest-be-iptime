package com.example.iptimeAPI.web.exception;

import com.example.iptimeAPI.service.macAddress.exception.MacAddressValidateException;
import com.example.iptimeAPI.service.user.exception.OuterServiceValidateException;
import com.example.iptimeAPI.web.response.ApiResponse;
import com.example.iptimeAPI.web.response.ApiResponseGenerator;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "ERROR_FILE_LOGGER")
@RestControllerAdvice
public class ExControllerAdvice {
    private static void logger(Exception exception) {
        log.error(exception.getClass()
                .getSimpleName() + " = [{}][{}]",
            exception.getClass(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalStateException.class})
    public ApiResponse<ApiResponse.FailureBody> illegalStateExHandler(
        IllegalStateException exception) {
        logger(exception);
        String defaultMessage = exception.getMessage();
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST,
            HttpStatus.BAD_REQUEST.value() + "001",
            exception.getClass()
                .getSimpleName(), defaultMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public List<ApiResponse<ApiResponse.FailureBody>> bindingExHandler(BindException exception) {
        logger(exception);
        List<ApiResponse<ApiResponse.FailureBody>> errorResults = new ArrayList<>();
        exception.getAllErrors()
            .forEach(error -> {
                FieldError fieldError = (FieldError) error;
                String field = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                ApiResponse<ApiResponse.FailureBody> bindingException = ApiResponseGenerator.fail(
                    HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value() + "001",
                    exception.getClass()
                        .getSimpleName(), field + " ?????? ????????? ???????????????. " + defaultMessage);
                errorResults.add(bindingException);
            });
        return errorResults;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MacAddressValidateException.class})
    public ApiResponse<ApiResponse.FailureBody> MacAddressValidateExHandler(
        MacAddressValidateException exception) {
        logger(exception);
        String defaultMessage = exception.getMessage();
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST,
            HttpStatus.BAD_REQUEST.value() + exception.getCode(),
            exception.getClass()
                .getSimpleName(), defaultMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({OuterServiceValidateException.class})
    public ApiResponse<ApiResponse.FailureBody> OuterServiceValidateExHandler(
        OuterServiceValidateException exception) {
        logger(exception);
        String defaultMessage = exception.getMessage();
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST,
            HttpStatus.BAD_REQUEST.value() + exception.getCode(),
            exception.getClass()
                .getSimpleName(), defaultMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiResponse<ApiResponse.FailureBody> exHandler(Exception exception) {
        logger(exception);
        String defaultMessage = exception.getMessage();
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST,
            HttpStatus.BAD_REQUEST.value() + "000",
            exception.getClass()
                .getSimpleName(), defaultMessage);
    }
}
