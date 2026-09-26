package com.example.pr3p7.controller.api.dto;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request,
                              @RequestParam(value = "status", required = false) Integer statusParam,
                              Model model) {

        int status;
        if (statusParam != null) {
            status = statusParam;
        } else {
            Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;
        }

        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object messageObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object uriObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object exceptionObj = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        //status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        String title;
        String description;
        String icon;

        switch (status) {
            case 400 -> { title = "Неверный запрос";
                description = "Сервер не может обработать запрос из-за некорректных данных.";
                icon = "⚠"; }
            case 401 -> { title = "Требуется вход";
                description = "Вы не авторизованы. Войдите, чтобы продолжить.";
                icon = "🔒"; }
            case 403 -> { title = "Доступ запрещён";
                description = "У вас нет прав для просмотра этой страницы. Обратитесь к администратору или войдите под другой учётной записью.";
                icon = "⛔"; }
            case 404 -> { title = "Страница не найдена";
                description = "Такой страницы не существует. Возможно, ссылка устарела или вы ошиблись в адресе.";
                icon = "🔍"; }
            case 405 -> { title = "Метод не разрешён";
                description = "Этот HTTP-метод не поддерживается для данного адреса.";
                icon = "🚫"; }
            case 409 -> { title = "Конфликт данных";
                description = "Запрос конфликтует с текущим состоянием ресурса. Проверьте данные и попробуйте снова.";
                icon = "⚡"; }
            case 422 -> { title = "Ошибка валидации";
                description = "Некоторые поля заполнены некорректно. Проверьте форму.";
                icon = "✏"; }
            case 500 -> { title = "Ошибка сервера";
                description = "Что-то пошло не так на стороне сервера. Попробуйте позже.";
                icon = "☠"; }
            case 502, 503, 504 -> { title = "Сервис недоступен";
                description = "Сервер временно недоступен. Попробуйте через минуту.";
                icon = "🌐"; }
            default  -> { title = "Ошибка";
                description = "Произошла неизвестная ошибка.";
                icon = "❗"; }
        }

        model.addAttribute("status", status);
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("icon", icon);
        model.addAttribute("path", uriObj != null ? uriObj.toString() : null);
        model.addAttribute("exception",
                exceptionObj != null ? exceptionObj.getClass().getSimpleName() : null);

        // Обрезаем техническую "No message available" / null
        String msg = messageObj != null ? messageObj.toString() : null;
        if ("No message available".equals(msg) || "null".equals(msg)) msg = null;
        model.addAttribute("message", msg);

        return "error"; // templates/error.html
    }
}