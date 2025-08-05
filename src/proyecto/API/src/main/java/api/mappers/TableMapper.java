package api.mappers;

import api.domain.Table;
import api.dto.TableRequest;
import api.dto.TableResponse;

public abstract class TableMapper {

    public static void updateTableFromRequest(Table table, TableRequest tableRequest) {

        table.setNumTable(tableRequest.num_Table());
        table.setState(tableRequest.state());
        table.setNumCustomers(tableRequest.num_Customers());
        table.setPosX(tableRequest.posX());
        table.setPosY(tableRequest.posY());

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
