package api.controllers;


import api.dto.TableRequest;
import api.dto.TableResponse;
import api.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/table")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @Operation(
            operationId = "getTableById",
            summary     = "Get table by ID",
            description = "Retrieve a table record by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(tableService.findById(id));
    }

    @Operation(
            operationId = "createTable",
            summary     = "Create a new table",
            description = "Create a new table entry with the provided details."
    )
    @PostMapping
    public ResponseEntity<TableResponse> create(@RequestBody @Valid TableRequest request) throws Exception {
        return ResponseEntity.ok(tableService.createTable(request));
    }

    @Operation(
            operationId = "updateTable",
            summary     = "Update table by ID",
            description = "Update the table record identified by its ID with new data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<TableResponse> update(
            @PathVariable int id,
            @RequestBody @Valid TableRequest request
    ) throws Exception {
        return ResponseEntity.ok(tableService.updateTable(request, id));
    }

    @Operation(
            operationId = "deleteTable",
            summary     = "Delete table by ID",
            description = "Remove the table record identified by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}