package com.ll.exam.conong.bounded_context.member.adapter.out.jpa;

import com.ll.exam.conong.bounded_context.member.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByUsername(String username);
}
