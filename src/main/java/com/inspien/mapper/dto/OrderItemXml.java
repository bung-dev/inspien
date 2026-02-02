package com.inspien.mapper.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderItemXml {

    @NotBlank
    @JacksonXmlProperty(localName = "USER_ID")
    private String userId;

    @NotBlank
    @JacksonXmlProperty(localName = "ITEM_ID")
    private String itemId;

    @NotBlank
    @JacksonXmlProperty(localName = "ITEM_NAME")
    private String itemName;

    @NotBlank
    @JacksonXmlProperty(localName = "PRICE")
    private String  price;
}
