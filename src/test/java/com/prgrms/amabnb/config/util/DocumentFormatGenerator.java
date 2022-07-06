package com.prgrms.amabnb.config.util;

import static org.springframework.restdocs.snippet.Attributes.*;

import org.springframework.restdocs.snippet.Attributes;

public class DocumentFormatGenerator {
    public static Attributes.Attribute getDateFormat() {
        return key("format").value("yyyy-MM-dd");
    }
}
