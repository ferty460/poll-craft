package org.example.poll_craft.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 404 - Страница не найдена
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class, EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, HttpServletRequest request, Model model) {
        log.warn("404 error: {}", ex.getMessage());
        model.addAttribute("status", 404);
        model.addAttribute("error", "Страница не найдена");
        model.addAttribute("message", "Запрашиваемая страница не существует");
        model.addAttribute("path", request.getRequestURI());

        return "404";
    }

    // 403 - Доступ запрещён
    @ExceptionHandler({AccessDeniedException.class, SecurityException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(Exception ex, HttpServletRequest request, Model model) {
        log.warn("403 error: {}", ex.getMessage());
        model.addAttribute("status", 403);
        model.addAttribute("error", "Доступ запрещён");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error";
    }

    // 400 - Bad Request (ошибки валидации)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception ex, HttpServletRequest request, Model model) {
        log.warn("400 error: {}", ex.getMessage());
        model.addAttribute("status", 400);
        model.addAttribute("error", "Некорректный запрос");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error";
    }

    // 500 - Внутренняя ошибка сервера
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, HttpServletRequest request, Model model) {
        log.error("500 error: ", ex);
        model.addAttribute("status", 500);
        model.addAttribute("error", "Внутренняя ошибка сервера");
        model.addAttribute("message", "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
        model.addAttribute("path", request.getRequestURI());


        return "error";
    }

}
