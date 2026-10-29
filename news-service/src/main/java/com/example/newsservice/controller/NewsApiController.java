
package com.example.newsservice.controller;

import com.example.newsservice.model.NewsModel;
import com.example.newsservice.service.NewsService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    public List<NewsModel> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")    @Operation(summary = "Получить новость по ID")
    public NewsModel getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/search")    @Operation(summary = "Поиск новостей по ключевому слову")
    public List<NewsModel> search(@RequestParam String keyword) {
        return service.search(keyword);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новость")
    public NewsModel create(@Valid @RequestBody NewsModel item,
                            Authentication authentication) {
        requireEditor(authentication);
        return service.save(item);
    }

    @PutMapping("/{id}")     @Operation(summary = "Обновить новость")
    public NewsModel update(@PathVariable Integer id,
                            @Valid @RequestBody NewsModel item,
                            Authentication authentication) {
        requireEditor(authentication);
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")    @Operation(summary = "Удалить новость")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id, Authentication authentication) {
        requireAdmin(authentication);
        service.delete(id);
    }

    private void requireEditor(Authentication authentication) {
        if (!hasRole(authentication, "ADMIN") && !hasRole(authentication, "MANAGER")) {
            throw new AccessDeniedException("Создание и изменение новостей доступно ADMIN и MANAGER");
        }
    }

    private void requireAdmin(Authentication authentication) {
        if (!hasRole(authentication, "ADMIN")) {
            throw new AccessDeniedException("Удаление новостей доступно только ADMIN");
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(("ROLE_" + role)::equals);
    }
}
