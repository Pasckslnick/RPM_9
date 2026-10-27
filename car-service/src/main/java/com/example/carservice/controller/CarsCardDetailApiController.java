package com.example.carservice.controller;

import com.example.carservice.model.CarsCardDetailModel;
import com.example.carservice.service.CarsCardDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-details")
@Tag(name = "Элементы корзины", description = "Управление элементами корзины")
@SecurityRequirement(name = "bearerAuth")
public class CarsCardDetailApiController {

    private final CarsCardDetailService service;

    public CarsCardDetailApiController(CarsCardDetailService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Получить все элементы корзины")
    public List<CarsCardDetailModel> getAll() {
        return service.getAll();
    }

    /** Позиции конкретной корзины — используется на /card.html */
    @GetMapping("/by-card/{cardId}")
    @Operation(summary = "Позиции корзины")
    public List<CarsCardDetailModel> getByCard(@PathVariable Integer cardId) {
        return service.getByCardId(cardId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить элемент корзины")
    public CarsCardDetailModel getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать элемент корзины")
    public CarsCardDetailModel create(@Valid @RequestBody CarsCardDetailModel item) {
        return service.save(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить элемент корзины")
    public CarsCardDetailModel update(@PathVariable Integer id,
                                      @Valid @RequestBody CarsCardDetailModel item) {
        item.setId(id);
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить элемент корзины")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}