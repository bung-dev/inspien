package com.inspien.receiver.jdbc;

import com.inspien.receiver.jdbc.dto.ShipmentRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShipmentRepository {

    private final NamedParameterJdbcTemplate template;

    public int[] batchInsert(List<ShipmentRow> rows) {
        String sql = """
                INSERT INTO SHIPMENT_TB
                (SHIPMENT_ID, ORDER_ID, ITEM_ID, APPLICANT_KEY, ADDRESS)
                VALUES
                (:shipmentId, :orderId, :itemId, :applicantKey, :address)
                ON DUPLICATE KEY UPDATE
                SHIPMENT_ID = SHIPMENT_ID
                """;

        if (rows == null || rows.isEmpty()) {
            return new int[0];
        }

        SqlParameterSource[] params = rows.stream()
                .map(this::toParam)
                .toArray(SqlParameterSource[]::new);

        return template.batchUpdate(sql, params);
    }

    private SqlParameterSource toParam(ShipmentRow o) {
        return new MapSqlParameterSource()
                .addValue("shipmentId", o.shipmentId())
                .addValue("orderId", o.orderId())
                .addValue("itemId", o.itemId())
                .addValue("applicantKey", o.applicantKey())
                .addValue("address", o.address());
    }
}
