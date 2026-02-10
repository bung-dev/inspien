package com.inspien.mapper;

import com.inspien.mapper.dto.OrderHeaderXml;
import com.inspien.mapper.dto.OrderItemXml;
import com.inspien.mapper.dto.OrderRequestXML;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class OrderParserXMLTest {

    private final OrderParserXML parser = new OrderParserXML();

    @Test
    @DisplayName("Base64로 인코딩된 XML 데이터를 객체로 정상 파싱하는지 확인")
    void parse_Base64Xml_To_Object_Success() throws Exception {
        // given
        String xmlContent =
            "<HEADER>" +
            "  <USER_ID>user123</USER_ID>" +
            "  <NAME>홍길동</NAME>" +
            "  <ADDRESS>서울시</ADDRESS>" +
            "  <STATUS>N</STATUS>" +
            "</HEADER>" +
            "<ITEM>" +
            "  <USER_ID>user123</USER_ID>" +
            "  <ITEM_ID>P001</ITEM_ID>" +
            "  <ITEM_NAME>노트북</ITEM_NAME>" +
            "  <PRICE>1500000</PRICE>" +
            "</ITEM>";

        String base64Input = Base64.getEncoder().encodeToString(xmlContent.getBytes("EUC-KR"));

        // when
        OrderRequestXML result = parser.parse(base64Input);

        // then
        assertNotNull(result);
        assertNotNull(result.getHeaders());
        assertNotNull(result.getItems());
        
        // Header 검증
        assertEquals(1, result.getHeaders().size());
        assertEquals("user123", result.getHeaders().get(0).getUserId());
        assertEquals("홍길동", result.getHeaders().get(0).getName());

        // Item 검증
        assertEquals(1, result.getItems().size());
        assertEquals("P001", result.getItems().get(0).getItemId());
        assertEquals("노트북", result.getItems().get(0).getItemName());
    }

    @Test
    @DisplayName("공백이 포함된 UserID 데이터가 정상적으로 조인되는지 확인")
    void flatten_WithWhitespace_Success() {
        // given
        OrderRequestXML req = new OrderRequestXML();
        
        OrderHeaderXml h1 = new OrderHeaderXml();
        h1.setUserId(" USER01 "); // 공백 포함
        h1.setName("홍길동");
        h1.setAddress("서울");
        h1.setStatus("N");
        
        OrderHeaderXml h2 = new OrderHeaderXml();
        h2.setUserId("USER02");
        h2.setName("이순신");
        h2.setAddress("부산");
        h2.setStatus("N");
        
        req.setHeaders(java.util.List.of(h1, h2));
        
        OrderItemXml i1 = new OrderItemXml();
        i1.setUserId("USER01"); // 헤더엔 공백 있음, 여긴 없음
        i1.setItemId("ITEM01");
        i1.setItemName("물건1");
        i1.setPrice("1000");
        
        OrderItemXml i2 = new OrderItemXml();
        i2.setUserId(" USER02 "); // 헤더엔 공백 없음, 여긴 있음
        i2.setItemId(" ITEM02 "); // itemId에도 공백 추가
        i2.setItemName("물건2");
        i2.setPrice("2000");
        
        OrderItemXml i3 = new OrderItemXml();
        i3.setUserId("USER03"); // 헤더 없음
        i3.setItemId("ITEM03");
        i3.setItemName("물건3");
        i3.setPrice("3000");

        req.setItems(java.util.List.of(i1, i2, i3));
        
        OrderMapper mapper = new OrderMapper();
        
        // when
        com.inspien.mapper.dto.FlattenResult flattenResult = mapper.flatten(req);
        java.util.List<com.inspien.order.domain.Order> result = flattenResult.orders();
        
        // then
        assertEquals(2, result.size(), "공백이 있어도 매칭되는 것만 2개 나와야 함 (USER03은 헤더 없음)");
        assertEquals(1, flattenResult.skippedCount(), "USER03 헤더가 없으므로 1개가 스킵되어야 함");
        assertEquals("USER01", result.get(0).getUserId());
        assertEquals("USER02", result.get(1).getUserId());
        assertEquals("ITEM02", result.get(1).getItemId(), "ITEM_ID도 trim 되어야 함");
    }
}