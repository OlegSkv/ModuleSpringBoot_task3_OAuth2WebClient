package example.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest request, Exception e)   {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorText", e.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ModelAndView handleWebClientResponseError(HttpServletRequest request, WebClientResponseException e)   {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorText", e.getStatusText());
        return modelAndView;
    }
}