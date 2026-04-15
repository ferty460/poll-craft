package org.example.poll_craft.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object error = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("status", statusCode);

            switch (statusCode) {
                case 404:
                    model.addAttribute("error", "Страница не найдена");
                    model.addAttribute("message", "Запрашиваемая страница не существует");
                    return "404";
                case 403:
                    model.addAttribute("error", "Доступ запрещён");
                    model.addAttribute("message", "У вас нет прав для доступа к этой странице");
                    return "error";
                case 400:
                    model.addAttribute("error", "Некорректный запрос");
                    model.addAttribute("message", error != null ? error : "Некорректные параметры запроса");
                    return "error";
                default:
                    model.addAttribute("error", "Ошибка сервера");
                    model.addAttribute("message", "Произошла ошибка при обработке запроса");
                    return "error";
            }
        }

        model.addAttribute("status", 500);
        model.addAttribute("error", "Внутренняя ошибка");
        model.addAttribute("message", "Произошла непредвиденная ошибка");

        return "error";
    }

}
