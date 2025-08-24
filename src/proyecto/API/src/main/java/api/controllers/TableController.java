package api.controllers;


import api.domain.Table;
import api.dto.TableRequest;
import api.dto.TableResponse;
import api.mappers.TableMapper;
import api.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        Table table = tableService.findById(id);
        return ResponseEntity.ok(TableMapper.toResponse(table));
    }

    @Operation(
            operationId = "getAllTables",
            summary     = "Get all existent tables",
            description = "Gets all the tables stored in the database"
    )
    @GetMapping("")
    public ResponseEntity<List<TableResponse>> findAll() throws Exception {
        List<Table> tables = tableService.getAllTables();
        List<TableResponse> responses = tables.stream().map(TableMapper::toResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            operationId = "createTable",
            summary     = "Create a new table",
            description = "Create a new table entry with the provided details."
    )
    @PostMapping
    public ResponseEntity<TableResponse> create(@RequestBody @Valid TableRequest request) throws Exception {
        // map the request into a domain entity without resolving the waiter
        Table table = TableMapper.getTableFromRequest(request);
        Table created = tableService.createTable(table, request.waiter());
        return ResponseEntity.ok(TableMapper.toResponse(created));
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
        // map the incoming DTO into a domain entity without waiter
        Table table = TableMapper.getTableFromRequest(request);
        Table updated = tableService.updateTable(table, id, request.waiter());
        return ResponseEntity.ok(TableMapper.toResponse(updated));
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