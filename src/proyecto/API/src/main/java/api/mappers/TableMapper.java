package api.mappers;

import api.domain.Table;

public abstract class TableMapper {

    public static void updateTableFromRequest(Table table, TableRequest tableRequest) {

        table.setNumTable(tableRequest.getNumTable());
        table.setWaiter_id(tableRequest.getWaiter_id());
        table.setState(tableRequest.getState());
        table.setNumCustomers(tableRequest.getNumCustomers());

    }

    public static TableResponse toResponse(Table table){
        if (table == null) return null;
        return new TableResponse(
                table.getId(),
                table.getNumTable(),
                table.getNumCustomers(),
                table.getWaiter_id(),
                table.getState()
        );

    }
}
