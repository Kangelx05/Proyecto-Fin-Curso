package api.service;

import api.domain.Table;
import api.domain.User;
import api.repository.TableRepository;
import api.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor    // de Lombok
@Transactional
public class TableService {

    private final TableRepository tableRepository;
    private final EntityManager em;
    private final UserRepository userRepository;

    /**
     * Retrieve a table by its identifier.
     *
     * @param id the primary key of the table
     * @return the corresponding {@link Table}
     * @throws Exception if no table exists with the provided id
     */
    public Table findById(int id) throws Exception {
        Table table = tableRepository.findTableById(id);
        if (table == null) {
            throw new Exception();
        }
        return table;
    }

    /**
     * Persist a new {@link Table}.  The waiter association will be resolved
     * using the supplied waiterId.  Controllers should map DTOs into a
     * {@link Table} entity and supply the waiter identifier separately.
     *
     * @param table    the table entity to create
     * @param waiterId the identifier of the waiter to associate
     * @return the persisted {@link Table}
     * @throws Exception if the table number already exists or the waiter cannot be found
     */
    public Table createTable(@NotNull Table table, int waiterId) throws Exception {
        if (tableRepository.existsByNumTable(table.getNumTable())) {
            throw new Exception("The table number already exists: " + table.getNumTable());
        }
        User user = userRepository.findUserById(waiterId);
        if (user == null) {
            throw new Exception("The user does not exist");
        }
        table.setWaiter(user);
        // Establecer el estado inicial a "FREE" si no se ha proporcionado explícitamente.
        String state = table.getState();
        if (state == null || state.isBlank()) {
            table.setState("FREE");
        }
        return tableRepository.save(table);
    }

    /**
     * Update an existing {@link Table} with new values.  Scalar fields and the
     * waiter association are updated based on the provided entity and
     * waiterId.  If the table does not exist an exception is thrown.
     *
     * @param updatedTable the incoming entity with desired values
     * @param id           the identifier of the table to update
     * @param waiterId     the identifier of the waiter to associate
     * @return the updated {@link Table}
     * @throws Exception if the table or waiter cannot be found
     */
    public Table updateTable(@NotNull Table updatedTable, int id, int waiterId) throws Exception {
        Table table = tableRepository.findTableById(id);
        if (table == null) {
            throw new Exception("Id not found: " + id);
        }
        User user = userRepository.findUserById(waiterId);
        if (user == null) {
            throw new Exception("The user does not exist");
        }
        // update scalar fields
        table.setNumTable(updatedTable.getNumTable());
        table.setState(updatedTable.getState());
        table.setNumCustomers(updatedTable.getNumCustomers());
        table.setPosX(updatedTable.getPosX());
        table.setPosY(updatedTable.getPosY());
        table.setWaiter(user);
        return tableRepository.save(table);
    }

    /**
     * Delete a table by its identifier.
     *
     * @param id the primary key of the table to delete
     * @throws Exception if the table does not exist
     */
    public void deleteTable(int id) throws Exception {
        Table table = tableRepository.findTableById(id);
        if (table == null) {
            throw new Exception("Table not found: " + id);
        }
        tableRepository.delete(table);
    }

    /**
     * Retrieve all tables.
     *
     * @return a list of all {@link Table} entities
     */
    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }


}