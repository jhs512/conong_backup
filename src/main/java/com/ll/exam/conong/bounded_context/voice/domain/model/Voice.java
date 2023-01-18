package com.ll.exam.conong.bounded_context.voice.domain.model;

import com.ll.exam.conong.base.domain.BaseEntity;
import com.ll.exam.conong.bounded_context.member.domain.model.Member;
import com.ll.exam.conong.standard.fieldGenFile.PlayableFieldGenFile;
import com.ll.exam.conong.standard.fieldGenFile.PlayableFieldGenFileConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Voice extends BaseEntity {
    @ManyToOne
    private Member author;
    @Convert(converter = PlayableFieldGenFileConverter.class)
    private PlayableFieldGenFile playableFieldGenFile;

    public void deleteDiskFiles() {
        playableFieldGenFile.deleteOnDisk();
    }
}
