package api.mappers;

import api.domain.Table;
import api.dto.TableRequest;
import api.dto.TableResponse;

public abstract class TableMapper {

    public static void updateTableFromRequest(Table table, TableRequest tableRequest) {

        table.setNumTable(tableRequest.getNumTable());
        UserMapper.updateUserFromRequest(table.getWaiter(), tableRequest.getWaiter());
        table.setState(tableRequest.getState());
        table.setNumCustomers(tableRequest.getNumCustomers());

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
