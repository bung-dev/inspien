package com.inspien.receiver.jdbc;

import com.inspien.receiver.jdbc.dto.ShipmentRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
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
                """;

        if (rows == null || rows.isEmpty()) {
            return new int[0];
        }

        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(rows);

        return template.batchUpdate(sql, params);
    }
}
