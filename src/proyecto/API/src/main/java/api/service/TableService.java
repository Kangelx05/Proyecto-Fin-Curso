package api.service;

import api.domain.Table;
import api.domain.User;
import api.dto.TableRequest;
import api.dto.TableResponse;
import api.mappers.TableMapper;
import api.repository.TableRepository;
import api.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor    // de Lombok
@Transactional
public class TableService {

    private final TableRepository tableRepository;
    private final EntityManager em;
    private final UserRepository userRepository;

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

        User waiter = userRepository.findById(tableRequest.waiter()).orElseThrow(Exception::new);
        table.setWaiter(waiter);

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

    public List<TableResponse> getAllTables() throws Exception {
        List<Table> table = tableRepository.findAll();
        List<TableResponse> response = new ArrayList<>();
        for (Table table1 : table) {
            response.add(TableMapper.toResponse(table1));
        }
        return response;
    }


}