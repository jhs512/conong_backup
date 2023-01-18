package com.ll.exam.conong.bounded_context.voice.adapter.out.jpa;

import com.ll.exam.conong.bounded_context.member.domain.model.Member;
import com.ll.exam.conong.bounded_context.voice.domain.model.Voice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoiceRepository extends JpaRepository<Voice, Long> {
    List<Voice> findAllByOrderByIdDesc();

    List<Voice> findAllByAuthorOrderByIdDesc(Member author);
}
