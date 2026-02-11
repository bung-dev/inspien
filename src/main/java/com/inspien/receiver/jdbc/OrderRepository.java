package com.inspien.receiver.jdbc;

import com.inspien.order.domain.Order;
import com.inspien.receiver.jdbc.dto.PendingOrderRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final NamedParameterJdbcTemplate template;

    public int[] batchInsert(List<Order> orders) {
        String sql = """
                INSERT INTO ORDER_TB
                (APPLICANT_KEY, ORDER_ID, USER_ID, ITEM_ID, NAME, ADDRESS, ITEM_NAME, PRICE, STATUS)
                VALUES
                (:applicantKey, :orderId, :userId, :itemId, :name, :address, :itemName, :price, :status)
                """;
        if (orders == null || orders.isEmpty()) {
            return new int[0];
        }

        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(orders);

        return template.batchUpdate(sql, params);
    }

    public List<PendingOrderRow> findPendingForShipment(String applicantKey) {
        String sql = """
                SELECT ORDER_ID, ITEM_ID, APPLICANT_KEY, ADDRESS
                FROM ORDER_TB
                WHERE APPLICANT_KEY = :applicantKey AND STATUS = 'N'
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("applicantKey", applicantKey);
        return template.query(sql,params,(rs, row) ->
                new PendingOrderRow(
                        rs.getString("ORDER_ID"),
                        rs.getString("ITEM_ID"),
                        rs.getString("APPLICANT_KEY"),
                        rs.getString("ADDRESS")
                ));
    }

    public int updateStatusToY(String applicantKey, List<String> orderIds) {
        String sql = """
                UPDATE ORDER_TB
                SET STATUS = 'Y'
                WHERE APPLICANT_KEY = :applicantKey AND ORDER_ID IN (:orderIds)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("applicantKey", applicantKey)
                .addValue("orderIds", orderIds);

        return template.update(sql, params);
    }
}
