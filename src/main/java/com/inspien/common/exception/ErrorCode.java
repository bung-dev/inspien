package com.inspien.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    XML_PARSE_ERROR(400, "XML_PARSE_ERROR"),
    VALIDATION_ERROR(400, "VALIDATION_ERROR"),
    HEADER_ITEM_MISMATCH(400, "HEADER_ITEM_MISMATCH"),
    DUPLICATE_KEY(409, "DUPLICATE_KEY"),

    SFTP_SEND_FAIL(400, "SFTP_SEND_FAIL"),
    INTERNAL_ERROR(500,"INTERNAL_ERROR");

    private final int status;
    private final String code;

    public CustomException exception() {
        return new CustomException(this);
    }
}