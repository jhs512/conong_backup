package com.ll.exam.conong.bounded_context.voice.adapter.in.http;

import com.ll.exam.conong.base.AppConfig;
import com.ll.exam.conong.bounded_context.voice.domain.model.Voice;
import com.ll.exam.conong.bounded_context.voice.domain.service.VoiceService;
import com.ll.exam.conong.standard.DiskSavedFile;
import com.ll.exam.conong.standard.rq.Rq;
import com.ll.exam.conong.standard.util.Ut;
import com.ll.exam.conong.standard.validation.Validation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/voice")
@RequiredArgsConstructor
public class VoiceController {
    private final Rq rq;
    private final VoiceService voiceService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/upload")
    public String showUpload() {
        return "voice/upload";
    }

    @AllArgsConstructor
    @Getter
    public class UploadRequest {
        @Validation.NotEmptyMultipart
        private MultipartFile playableGenFieldFile;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public String upload(@Valid UploadRequest req) {
        DiskSavedFile diskSavedFile = Ut.file.upload.disk(AppConfig.getGenFileDirPath(), "voice", req.getPlayableGenFieldFile());

        String filePath = null;

        try {
            filePath = Ut.media.toMp3(diskSavedFile.getFilePath());
        }
        catch ( RuntimeException e ) {
            Ut.file.delete(filePath);
            return rq.redirectToBackWithMsg("올바르지 않은 미디어 파일이 업로드 되었습니다. 다른파일로 시도해주세요.");
        }
        finally {
            Ut.file.delete(diskSavedFile.getFilePath());
        }

        Voice newVoice = voiceService.save(rq.getMember(), filePath);

        return Rq.redirectWithMsg("myList", "%d번 녹음파일이 업로드되었습니다.".formatted(newVoice.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myList")
    public String showMyList(Model model) {
        List<Voice> voices = voiceService.getAllByAuthor(rq.getMember());

        model.addAttribute("voices", voices);

        return "voice/myList";
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        Voice voice = voiceService.getById(id).orElse(null);

        if ( voice == null ) {
            return rq.redirectToBackWithMsg("존재하지 않는 녹음파일 입니다.");
        }

        if ( voice.getAuthor().getId() != rq.getMember().getId() ) {
            return rq.redirectToBackWithMsg("삭제할 수 없는 녹음파일 입니다.");
        }

        Ut.file.delete(voice.getPlayableFieldGenFile().getFilePath());

        voiceService.delete(voice);

        return rq.redirectToBackWithMsg("녹음파일이 삭제되었습니다.");
    }
}
