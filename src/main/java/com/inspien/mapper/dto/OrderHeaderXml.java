package com.inspien.mapper.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderHeaderXml {

    @NotBlank
    @JacksonXmlProperty(localName = "USER_ID")
    private String userId;

    @NotBlank
    @JacksonXmlProperty(localName = "NAME")
    private String name;

    @NotBlank
    @JacksonXmlProperty(localName = "ADDRESS")
    private String address;

    @NotBlank
    @JacksonXmlProperty(localName = "STATUS")
    private String status;
}
