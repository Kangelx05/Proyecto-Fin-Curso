package api.mappers;

import api.domain.Table;
import api.domain.User;
import api.dto.TableRequest;
import api.dto.TableResponse;

public abstract class TableMapper {

    public static Table getTableFromRequest(TableRequest tableRequest, User waiter) throws Exception {
        Table table = new Table();
        // Set the table number and basic state
        table.setNumTable(tableRequest.num_Table());
        table.setState(tableRequest.state());
        table.setNumCustomers(tableRequest.num_Customers());
        table.setPosX(tableRequest.posX());
        table.setPosY(tableRequest.posY());
        // If a waiter entity has been provided, copy the identifier from the request
        // so that the relationship can be properly resolved when the entity is persisted.
        if (waiter != null) {
            waiter.setId(tableRequest.waiter());
            table.setWaiter(waiter);
        }
        return table;
    }

    /**
     * Create a new {@link Table} from the incoming request without
     * resolving the waiter.  This overload allows controllers to map
     * basic table attributes while leaving waiter resolution to the
     * service layer.
     *
     * @param tableRequest the DTO containing table details
     * @return a new {@link Table} instance populated with the provided values
     */
    public static Table getTableFromRequest(TableRequest tableRequest) {
        Table table = new Table();
        // Map simple properties from the request
        table.setNumTable(tableRequest.num_Table());
        table.setState(tableRequest.state());
        table.setNumCustomers(tableRequest.num_Customers());
        table.setPosX(tableRequest.posX());
        table.setPosY(tableRequest.posY());
        // When no waiter entity is supplied, instantiate one and set its identifier from the DTO.
        User waiter = new User();
        waiter.setId(tableRequest.waiter());
        table.setWaiter(waiter);
        return table;
    }

    /**
     * Update an existing {@link Table} in place using values from the
     * provided request.  The waiter association is intentionally left
     * unchanged; callers should supply the waiter explicitly if it needs
     * to be modified.
     *
     * @param table        the existing entity to update
     * @param tableRequest the DTO containing new values
     * @return the updated {@link Table}
     */
    public static Table updateTableFromRequest(Table table, TableRequest tableRequest) {
        if (table == null) {
            table = new Table();
        }
        table.setNumTable(tableRequest.num_Table());
        table.setState(tableRequest.state());
        table.setNumCustomers(tableRequest.num_Customers());
        table.setPosX(tableRequest.posX());
        table.setPosY(tableRequest.posY());
        return table;
    }

    public static TableResponse toResponse(Table table){
        if (table == null) return null;
        return new TableResponse(
                table.getId(),
                table.getNumTable(),
                table.getNumCustomers(),
                UserMapper.toResponse(table.getWaiter()),
                table.getState(),
                table.getPosX(),
                table.getPosY()
        );

    }
}
