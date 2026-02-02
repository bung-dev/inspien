package com.inspien.receiver.jdbc;

import com.inspien.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final NamedParameterJdbcTemplate template;

    private static final String INSERT_SQL = """
    INSERT INTO ORDER_TB
    (APPLICANT_KEY, ORDER_ID, USER_ID, ITEM_ID, NAME, ADDRESS, ITEM_NAME, PRICE, STATUS)
    VALUES
    (:applicantKey, :orderId, :userId, :itemId, :name, :address, :itemName, :price, :status)
    """;

    public int[] batchInsert(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return new int[0];
        }

        SqlParameterSource[] params = orders.stream()
                .map(this::toParam)
                .toArray(SqlParameterSource[]::new);

        return template.batchUpdate(INSERT_SQL, params);
    }

    private SqlParameterSource toParam(Order o) {
        return new MapSqlParameterSource()
                .addValue("orderId", o.getOrderId())
                .addValue("userId", o.getUserId())
                .addValue("itemId", o.getItemId())
                .addValue("applicantKey", o.getApplicantKey())
                .addValue("name", o.getName())
                .addValue("address", o.getAddress())
                .addValue("itemName", o.getItemName())
                .addValue("price", o.getPrice())
                .addValue("status", o.getStatus());
    }
}
