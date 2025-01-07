package api.mappers;

import api.domain.OrderStateHistory;
import api.dto.OrderStateHistoryRequest;
import api.dto.OrderStateHistoryResponse;


public abstract class OrderStateHistoryMapper {

        public static void updateOrderStateHistoryFromRequest(OrderStateHistory orderStateHistory, OrderStateHistoryRequest orderStateHistoryRequest) {

            orderStateHistory.setNewState(orderStateHistoryRequest.getNewState());
            orderStateHistory.setOldState(orderStateHistoryRequest.getOldState());
            OrderMapper.updateOrderFromRequest(orderStateHistory.getOrder(), orderStateHistoryRequest.getOrder());
            orderStateHistory.setDate(orderStateHistoryRequest.getDate());
            UserMapper.updateUserFromRequest(orderStateHistory.getUser(), orderStateHistoryRequest.getUser());

        }

        public static OrderStateHistoryResponse toResponse(OrderStateHistory orderStateHistory){
            if (orderStateHistory == null) return null;
            return new OrderStateHistoryResponse(
                    orderStateHistory.getId(),
                    OrderMapper.toResponse(orderStateHistory.getOrder()),
                    orderStateHistory.getOldState(),
                    orderStateHistory.getNewState(),
                    orderStateHistory.getDate(),
                    UserMapper.toResponse(orderStateHistory.getUser())
            );

        }
}


