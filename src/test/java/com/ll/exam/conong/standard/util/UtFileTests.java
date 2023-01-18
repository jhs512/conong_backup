package com.ll.exam.conong.standard.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class UtFileTests {
    @Test
    @DisplayName("기존 파일을 임시폴더에 복사")
    void t1() {
        String filePath = Ut.file.copyToTempFile("src/test/resources/sample/created_on_iphone.mp4");
        System.out.println(filePath);
        assertThat(filePath).isNotNull();
        Ut.file.delete(filePath);
    }

    @Test
    @DisplayName("mp4 파일을 metadata1 형식으로 파일명 변경")
    void t2() {
        String filePath = Ut.file.copyToTempFile("src/test/resources/sample/created_on_iphone.mp4");
        System.out.println(filePath);
        filePath = Ut.file.renameToMetadata1Format(filePath);
        assertThat(new File(filePath).getName()).endsWith("___len_2054__fs_211413.mp4");
        System.out.println(filePath);
        Ut.file.delete(filePath);
    }

    @Test
    @DisplayName("mp4 파일을 mp3 파일로 변경")
    void t3() {
        String filePath = Ut.file.copyToTempFile("src/test/resources/sample/created_on_iphone.mp4");
        System.out.println(filePath);
        filePath = Ut.media.toMp3(filePath);
        System.out.println(filePath);

        assertThat(new File(filePath).getName()).endsWith("___len_2090__fs_33792.mp3");

        Ut.file.delete(filePath);
    }

    @Test
    @DisplayName("mp3 파일의 길이(밀리세컨트) 구하기")
    void t4() {
        String filePath = "src/test/resources/sample/internet_downloaded.mp3";

        int len = Ut.media.getMediaLength(filePath);
        System.out.println(len);

        assertThat(len).isEqualTo(27167);
    }

    @Test
    @DisplayName("mp4 파일의 길이(밀리세컨트) 구하기")
    void t5() {
        String filePath = "src/test/resources/sample/created_on_iphone.mp4";

        int len = Ut.media.getMediaLength(filePath);
        System.out.println(len);

        assertThat(len).isEqualTo(2054);
    }

    @Test
    @DisplayName("internet_downloaded, mp4 파일의 길이(밀리세컨트) 구하기")
    void t6() {
        String filePath = "src/test/resources/sample/internet_downloaded.mp4";

        int len = Ut.media.getMediaLength(filePath);
        System.out.println(len);

        assertThat(len).isEqualTo(30526);
    }

    @Test
    @DisplayName("internet_downloaded, mov 파일의 길이(밀리세컨트) 구하기")
    void t7() {
        String filePath = "src/test/resources/sample/internet_downloaded.mov";

        int len = Ut.media.getMediaLength(filePath);
        System.out.println(len);

        assertThat(len).isEqualTo(13347);
    }

    @Test
    @DisplayName("internet_downloaded, ogg 파일의 길이(밀리세컨트) 구하기")
    void t8() {
        String filePath = "src/test/resources/sample/internet_downloaded.ogg";

        int len = Ut.media.getMediaLength(filePath);
        System.out.println(len);

        assertThat(len).isEqualTo(74349);
    }

    @Test
    @DisplayName("internet_downloaded, wav 파일의 길이(밀리세컨트) 구하기")
    void t9() {
        String filePath = "src/test/resources/sample/internet_downloaded.wav";

        int len = Ut.media.getMediaLength(filePath);
        System.out.println(len);

        assertThat(len).isEqualTo(3400);
    }
}
