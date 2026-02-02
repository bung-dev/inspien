package com.inspien.mapper.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "ORDER_REQUEST")
public class OrderRequestXML {

    @Valid
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "HEADER")
    private List<OrderHeaderXml> headers;

    @Valid
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ITEM")
    private List<OrderItemXml> items;
}
