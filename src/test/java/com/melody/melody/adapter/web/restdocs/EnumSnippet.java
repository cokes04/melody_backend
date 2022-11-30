package com.melody.melody.adapter.web.restdocs;


import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.restdocs.snippet.TemplatedSnippet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EnumSnippet extends AbstractFieldsSnippet {

    public EnumSnippet(String type, PayloadSubsectionExtractor<?> subsectionExtractor,
                       List<FieldDescriptor> descriptors, Map<String, Object> attributes,
                       boolean ignoreUndocumentedFields) {
        super(type, descriptors, attributes, ignoreUndocumentedFields,
                subsectionExtractor);
    }

    @Override
    protected MediaType getContentType(Operation operation) {
        return operation.getResponse().getHeaders().getContentType();
    }

    @Override
    protected byte[] getContent(Operation operation) throws IOException {
        return operation.getResponse().getContent();
    }
}