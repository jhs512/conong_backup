package com.ll.exam.conong.bounded_context.member.domain.model;

import com.ll.exam.conong.base.domain.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity implements Serializable {
    private String username;
    private String password;
    private String nickname;
    private String oauthType;
    private String profileImgUrl;
}

