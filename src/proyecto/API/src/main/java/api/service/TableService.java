package api.service;

import api.domain.Table;
import api.dto.TableRequest;
import api.dto.TableResponse;
import api.mappers.TableMapper;
import api.repository.TableRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class TableService {

    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository){
        this.tableRepository = tableRepository;
    }

    public TableResponse findById(int id) throws Exception {
        Table table = tableRepository.findTableById(id);
        if (table == null) {
            throw new Exception();
        }
        return TableMapper.toResponse(table);
    }


    public TableResponse createTable(@NotNull TableRequest tableRequest) throws Exception {
        if (tableRepository.existsByNumTable(tableRequest.num_Table()))
        {
            throw new Exception("The table number already exists: " + tableRequest.num_Table());
        }

        Table  table = new Table();
        TableMapper.updateTableFromRequest(table, tableRequest);
        Table savedTable = tableRepository.save(table);

        return TableMapper.toResponse(savedTable);
    }


    public TableResponse updateTable(@NotNull TableRequest tableRequest, int id) throws Exception {
        Table table = tableRepository.findTableById(id);
        if (table == null) {
            throw new Exception("Id not found: " + id);
        }
        TableMapper.updateTableFromRequest(table, tableRequest);
        return TableMapper.toResponse(tableRepository.save(table));
    }


    public void deleteTable(int id) throws Exception {
        Table table = tableRepository.findTableById(id);
        if (table == null) {
            throw new Exception("Table not found: " + id);
        }
        tableRepository.delete(table);
    }


}