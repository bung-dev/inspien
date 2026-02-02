package com.inspien.sender.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank String base64Xml
) {}