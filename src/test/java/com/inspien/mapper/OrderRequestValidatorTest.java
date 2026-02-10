package com.inspien.mapper;

import com.inspien.common.exception.CustomException;
import com.inspien.common.exception.ErrorCode;
import com.inspien.mapper.dto.OrderHeaderXml;
import com.inspien.mapper.dto.OrderItemXml;
import com.inspien.mapper.dto.OrderRequestXML;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderRequestValidatorTest {

    private final OrderRequestValidator validator = new OrderRequestValidator();

    @Test
    @DisplayName("헤더의 UserID가 중복될 경우 예외 발생")
    void validate_DuplicateUserId_ThrowsException() {
        // given
        OrderRequestXML req = new OrderRequestXML();
        OrderHeaderXml h1 = new OrderHeaderXml(); h1.setUserId("user1"); h1.setName("N1"); h1.setAddress("A1");
        OrderHeaderXml h2 = new OrderHeaderXml(); h2.setUserId("user1"); h2.setName("N2"); h2.setAddress("A2");
        req.setHeaders(List.of(h1, h2));
        req.setItems(List.of(new OrderItemXml()));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> validator.validate(req));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    @DisplayName("필수 데이터(Header/Item)가 누락된 경우 예외 발생")
    void validate_EmptyData_ThrowsException() {
        // given
        OrderRequestXML req = new OrderRequestXML();
        req.setHeaders(List.of());

        // when & then
        assertThrows(CustomException.class, () -> validator.validate(req));
    }
}
