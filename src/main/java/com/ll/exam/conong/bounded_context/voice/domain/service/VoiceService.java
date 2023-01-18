package com.ll.exam.conong.bounded_context.voice.domain.service;

import com.ll.exam.conong.bounded_context.member.domain.model.Member;
import com.ll.exam.conong.bounded_context.voice.adapter.out.jpa.VoiceRepository;
import com.ll.exam.conong.bounded_context.voice.domain.model.Voice;
import com.ll.exam.conong.standard.fieldGenFile.PlayableFieldGenFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoiceService {
    private final VoiceRepository voiceRepository;

    @Transactional
    public Voice save(Member author, String filePath) {
        Voice voice = Voice.builder()
                .author(author)
                .playableFieldGenFile(new PlayableFieldGenFile(filePath))
                .build();

        return voiceRepository.save(voice);
    }

    public List<Voice> getAllByAuthor(Member author) {
        return voiceRepository.findAllByAuthorOrderByIdDesc(author);
    }

    public Optional<Voice> getById(Long id) {
        return voiceRepository.findById(id);
    }

    @Transactional
    public void delete(Voice voice) {
        voice.deleteDiskFiles();
        voiceRepository.delete(voice);
    }
}
