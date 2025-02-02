package api.mappers;

import api.domain.OrderStateHistory;
import api.dto.OrderStateHistoryRequest;
import api.dto.OrderStateHistoryResponse;


public abstract class OrderStateHistoryMapper {

        public static void updateOrderStateHistoryFromRequest(OrderStateHistory orderStateHistory, OrderStateHistoryRequest orderStateHistoryRequest) {

            orderStateHistory.setNewState(orderStateHistoryRequest.newState());
            orderStateHistory.setOldState(orderStateHistoryRequest.oldState());
            OrderMapper.updateOrderFromRequest(orderStateHistory.getOrder(), orderStateHistoryRequest.order());
            orderStateHistory.setDate(orderStateHistoryRequest.date());
            UserMapper.updateUserFromRequest(orderStateHistory.getUser(), orderStateHistoryRequest.user());

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


