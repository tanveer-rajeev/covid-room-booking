package com.assignment.booking.Exception;

import java.util.Date;
import java.util.List;

import com.assignment.booking.DTO.BookedInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // handling specific exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundHandling(ResourceNotFoundException exception, WebRequest request){
        ApiException errorDetails =
                new ApiException(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // handling global exception

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception, WebRequest request){
        ApiException errorDetails =
                new ApiException(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BookedNotificationHandler.class)
    public ResponseEntity<?> alreadyBookedNotificationHandling(BookedNotificationHandler bookedNotificationHandler){

        // I tried to give an "BookedInfo" through when exception will trigger with HttpStatus.NOT_FOUND.
        // But from client side I could not able to take that object when the exception triggered.

        // That's why I have get "BookedInfo" directly .
        // so from client side I just check a boolean variable "trigger" that common for both class
        // "BookedInfo" and "Booking" and if (trigger) than given "success message"
        // else given "already booked by name and available room list"
        return new ResponseEntity<>(bookedNotificationHandler.getBookedInfo(),HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleException(MethodArgumentNotValidException exception,WebRequest request) {
        ApiException errorDetails =
                new ApiException(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
