package com.inspien.mapper;

import com.inspien.common.exception.ErrorCode;
import com.inspien.mapper.dto.OrderHeaderXml;
import com.inspien.mapper.dto.OrderRequestXML;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class OrderRequestValidator {

    public void validate(OrderRequestXML req) {
           if (req == null || req.getHeaders() == null || req.getHeaders().isEmpty() || req.getItems() == null || req.getItems().isEmpty()) {
        throw ErrorCode.VALIDATION_ERROR.exception();
    }

    Set<String> headerUserIds = new HashSet<>();
        for (
    OrderHeaderXml h : req.getHeaders()) {
        String uid = trim(h.getUserId());
        if (uid == null || uid.isBlank()) {
            throw ErrorCode.VALIDATION_ERROR.exception();
        }
        if (!headerUserIds.add(uid)) {
            throw ErrorCode.VALIDATION_ERROR.exception();
        }
    }
}

    private String trim(String s) {

        return s == null ? null : s.trim();
    }
}
