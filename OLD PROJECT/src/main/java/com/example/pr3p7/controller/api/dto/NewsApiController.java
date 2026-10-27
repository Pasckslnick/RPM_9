package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.NewsModel;
import com.example.pr3p7.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@Tag(name = "Новости", description = "Управление новостями")
@SecurityRequirement(name = "bearerAuth")
public class NewsApiController {

    private final NewsService service;

    public NewsApiController(NewsService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Получить все новости", description = "Возвращает список всех новостей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<NewsModel> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить новость по id")
    public NewsModel getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Создать новость")
    public NewsModel create(@Valid @RequestBody NewsModel item) {
        return service.save(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Обновить новость")
    public NewsModel update(@PathVariable Integer id,
                            @Valid @RequestBody NewsModel item) {
        item.setId(id);
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Удалить новость")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}