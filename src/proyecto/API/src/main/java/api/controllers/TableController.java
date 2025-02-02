package api.controllers;


import api.dto.TableRequest;
import api.dto.TableResponse;
import api.service.TableService;
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

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(tableService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TableResponse> create(@RequestBody @Valid TableRequest request) throws Exception {
        return ResponseEntity.ok(tableService.createTable(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableResponse> update(@PathVariable int id, @RequestBody @Valid TableRequest request) throws Exception {
        return ResponseEntity.ok(tableService.updateTable(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
