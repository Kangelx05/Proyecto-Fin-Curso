package api.mappers;

import api.domain.Table;
import api.dto.TableRequest;
import api.dto.TableResponse;

public abstract class TableMapper {

    public static void updateTableFromRequest(Table table, TableRequest tableRequest) {

        table.setNumTable(tableRequest.num_Table());
        UserMapper.updateUserFromRequest(table.getWaiter(), tableRequest.waiter());
        table.setState(tableRequest.state());
        table.setNumCustomers(tableRequest.num_Customers());

    }

    public static TableResponse toResponse(Table table){
        if (table == null) return null;
        return new TableResponse(
                table.getId(),
                table.getNumTable(),
                table.getNumCustomers(),
                UserMapper.toResponse(table.getWaiter()),
                table.getState()
        );

    }
}
