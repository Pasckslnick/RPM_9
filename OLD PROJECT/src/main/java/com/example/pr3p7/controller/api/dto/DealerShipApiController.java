package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.DealerShipModel;
import com.example.pr3p7.service.DealerShipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealers")
@Tag(name = "Дилеры"
, description = "Контроллер для работы с дилерами. Позволяет получать список всех дилеров, получать информацию о конкретном дилере по его идентификатору, создавать нового дилера, обновлять информацию о существующем дилере и удалять дилера по его идентификатору.")
@SecurityRequirement(name = "bearerAuth")
public class DealerShipApiController {

    private final DealerShipService service;

    public DealerShipApiController(DealerShipService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Получить всех дилеров", description = "Позволяет получить список всех дилеров, доступных в системе. Каждый дилер представлен в виде объекта DealerShipModel, который содержит информацию о дилере, такую как его идентификатор, название, адрес и контактные данные.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Дилеры не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    public List<DealerShipModel> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить дилера по идентификатору", description = "Позволяет получить информацию о конкретном дилере по его идентификатору. Идентификатор передается в виде параметра пути, и если дилер с таким идентификатором существует, возвращается объект DealerShipModel с информацией о дилере. Если дилер не найден, может быть возвращен соответствующий статус ошибки.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Дилер не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    public DealerShipModel getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать нового дилера", description = "Позволяет создать нового дилера в системе. Данные нового дилера передаются в теле запроса в виде объекта DealerShipModel, который должен содержать необходимую информацию о дилере, такую как его название, адрес и контактные данные. Если создание дилера прошло успешно, возвращается объект DealerShipModel с информацией о созданном дилере, включая его уникальный идентификатор.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для создания дилера"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено")
    })
    public DealerShipModel create(@Valid @RequestBody DealerShipModel item) {
        return service.save(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить информацию о существующем дилере", description = "Позволяет обновить информацию о существующем дилере в системе. Идентификатор дилера, который нужно обновить, передается в виде параметра пути, а новые данные дилера передаются в теле запроса в виде объекта DealerShipModel. Если обновление прошло успешно, возвращается объект DealerShipModel с обновленной информацией о дилере. Если дилер с указанным идентификатором не найден, может быть возвращен соответствующий статус ошибки.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления дилера"),
            @ApiResponse(responseCode = "404", description = "Дилер не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено")
    })
    public DealerShipModel update(
            @PathVariable Integer id,
            @Valid @RequestBody DealerShipModel item) {

        item.setId(id);
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить дилера по идентификатору", description = "Позволяет удалить дилера из системы по его идентификатору. Идентификатор дилера, который нужно удалить, передается в виде параметра пути. Если удаление прошло успешно, возвращается строковое сообщение, подтверждающее удаление дилера. Если дилер с указанным идентификатором не найден, может быть возвращен соответствующий статус ошибки.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Дилер не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Дилер удалён";
    }
}