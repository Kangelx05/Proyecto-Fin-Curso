package api.mappers;

import api.domain.QrToken;
import api.domain.Table;
import api.dto.QrTokenRequest;
import api.dto.QrTokenResponse;

public abstract class QrTokenMapper {


    public static QrToken getQrTokenFromRequest(QrTokenRequest qrTokenRequest, Table table) {
        QrToken qrToken = new QrToken();
        qrToken.setId(qrTokenRequest.getId());
        qrToken.setToken(qrTokenRequest.getToken());
        qrToken.setTable(table);
        qrToken.setUsedBy(qrTokenRequest.getUsedBy());
        return qrToken;
    }

    public static QrTokenResponse toResponse(QrToken qrToken){
        if (qrToken == null) return null;
        return new QrTokenResponse(
                qrToken.getId(),
                qrToken.getToken(),
                TableMapper.toResponse(qrToken.getTable()),
                qrToken.getUsedBy(),
                qrToken.getStatus().name(),
                qrToken.getExpiresAt(),
                qrToken.getCreatedAt());
    }
}
