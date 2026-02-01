package com.inspien.order;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "ORDER_REQUEST")
public record OrderRequestXML(
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "HEADER")
        List<OrderHeaderXml> headers,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ITEM")
        List<OrderItemXml> items
) {
    record OrderHeaderXml(
            @JacksonXmlProperty(localName = "USER_ID")
            String userId,
            @JacksonXmlProperty(localName = "NAME")
            String name,
            @JacksonXmlProperty(localName = "ADDRESS")
            String address,
            @JacksonXmlProperty(localName = "STATUS")
            String status
    ){
        public OrderHeaderXml {
            status = status == null ? null : status.trim();
        }
    }

    record OrderItemXml(
            @JacksonXmlProperty(localName = "USER_ID")
            String userId,
            @JacksonXmlProperty(localName = "ITEM_ID")
            String itemId,
            @JacksonXmlProperty(localName = "ITEM_NAME")
            String itemName,
            @JacksonXmlProperty(localName = "PRICE")
            String price
    ){}
}
