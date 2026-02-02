package com.inspien.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.inspien.common.util.DataUtil;
import com.inspien.mapper.dto.OrderRequestXML;
import org.springframework.stereotype.Component;

@Component
public class OrderParserXML {

    private final XmlMapper xmlMapper = new XmlMapper();

    public OrderRequestXML parse(String base64) throws JsonProcessingException {
        String xml = DataUtil.decodeEucKr(base64);

        String wrapped = wrap(xml);
        return xmlMapper.readValue(wrapped, OrderRequestXML.class);
    }

    private String wrap(String xml) {
        StringBuilder headers = new StringBuilder();
        StringBuilder items = new StringBuilder();

        for (String block : xml.split("(?=<HEADER>|<ITEM>)")) {
            if (block.startsWith("<HEADER>")) {
                headers.append(block);
            } else if (block.startsWith("<ITEM>")) {
                items.append(block);
            }
        }
        return "<ORDER_REQUEST>" + headers + items + "</ORDER_REQUEST>";
    }
}
