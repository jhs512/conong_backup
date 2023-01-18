package com.ll.exam.conong.standard.fieldGenFile;

import jakarta.persistence.AttributeConverter;
import org.springframework.util.StringUtils;

public class PlayableFieldGenFileConverter implements AttributeConverter<PlayableFieldGenFile, String> {
    @Override
    public String convertToDatabaseColumn(PlayableFieldGenFile attribute) {
        if ( attribute == null ) return "";
        return attribute.toString();
    }

    @Override
    public PlayableFieldGenFile convertToEntityAttribute(String filePath) {
        if (StringUtils.hasText(filePath))
            return new PlayableFieldGenFile(filePath);

        return null;
    }
}
