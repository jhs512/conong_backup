package com.ll.exam.conong.standard.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mp3.Mp3Directory;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import com.drew.metadata.wav.WavDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.conong.base.AppConfig;
import com.ll.exam.conong.standard.DiskSavedFile;
import com.ll.exam.conong.standard.drewMetadata.TagType;
import com.ll.exam.conong.standard.rsData.RsData;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.gagravarr.ogg.audio.OggAudioStatistics;
import org.gagravarr.vorbis.VorbisFile;
import org.gagravarr.vorbis.VorbisInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static com.ll.exam.conong.standard.util.Ut.date.parseDurationSecondsStringToInt;
import static com.ll.exam.conong.standard.util.Ut.file.genTempFilePath;
import static com.ll.exam.conong.standard.util.Ut.file.renameToMetadata1Format;

@Slf4j
public class Ut {
    public static class media {

        public static String toMp3(String sourceFilePath) {
            File source = new File(sourceFilePath);

            String targetFilePath = genTempFilePath(source.getParent(), "mp3");
            File target = new File(targetFilePath);

            //Audio Attributes
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            //audio.setBitRate(128000);
            //audio.setChannels(2);
            //audio.setSamplingRate(44100);

            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setAudioAttributes(audio);

            Encoder encoder = new Encoder();
            try {
                encoder.encode(new MultimediaObject(source), target, attrs);
            } catch (EncoderException e) {
                throw new RuntimeException(e);
            }

            return renameToMetadata1Format(targetFilePath);
        }

        public static int getMediaLength(String filePath) {
            String ext = Ut.media.detectExt(filePath);

            System.out.println(ext);

            if (ext.equals("mp3")) {
                return getMp3MediaLength(filePath);
            } else if (ext.equals("ogg")) {
                return getOggMediaLength(filePath);
            } else {
                return getUniversalMediaLength(filePath);
            }
        }

        private static int getOggMediaLength(String filePath) {
            try (VorbisFile vorbisFile = new VorbisFile(new File(filePath))) {
                OggAudioStatistics oggAudioStatistics = new OggAudioStatistics(vorbisFile, vorbisFile);
                oggAudioStatistics.calculate();

                long lastGranule = oggAudioStatistics.getLastGranule();

                VorbisInfo info = vorbisFile.getInfo();
                long samples = lastGranule - info.getPreSkip();
                double sampleRate = info.getSampleRate();

                return (int) (samples * 1000 / sampleRate);
            } catch (IOException e) {
                return -1;
            }
        }

        private static int getUniversalMediaLength(String filePath) {
            try (FileInputStream sourceFileInputStream = new FileInputStream(filePath)) {
                Metadata metadata = null;
                try {
                    metadata = ImageMetadataReader.readMetadata(sourceFileInputStream);
                } catch (ImageProcessingException e) {
                    return -1;
                } catch (IOException e) {
                    return -1;
                }

            StreamSupport.stream(metadata.getDirectories().spliterator(), false)
                    .map(Directory::getTags)
                    .flatMap(Collection::stream)
                    .forEach(tag -> {
                        log.debug("tag info start");
                        log.debug("tag.getTagType() : {}", tag.getTagType());
                        log.debug("tag : {}", tag);
                        log.debug("tag info end");
                    });

                Directory dir = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);

                if (dir == null) {
                    dir = metadata.getFirstDirectoryOfType(Mp4Directory.class);
                }

                if (dir == null) {
                    dir = metadata.getFirstDirectoryOfType(Mp3Directory.class);
                }

                if (dir == null) {
                    dir = metadata.getFirstDirectoryOfType(Mp4SoundDirectory.class);
                }

                if (dir == null) {
                    dir = metadata.getFirstDirectoryOfType(WavDirectory.class);
                }

                if (dir == null) {
                    dir = metadata.getFirstDirectoryOfType(WavDirectory.class);
                }

                String durationInSeconds = null;

                if ( dir.getObject(TagType.DurationInSeconds) != null ) {
                    durationInSeconds = dir.getString(TagType.DurationInSeconds);
                }
                else {
                    durationInSeconds = dir.getString(TagType.WaveDurationInSeconds);
                }

                return parseDurationSecondsStringToInt(durationInSeconds);
            } catch (FileNotFoundException e) {
                return -1;
            } catch (IOException e) {
                return -1;
            }
        }

        private static int getMp3MediaLength(String filePath) {
            Mp3File mp3file;
            try {
                mp3file = new Mp3File(filePath);
            } catch (IOException e) {
                return -1;
            } catch (UnsupportedTagException e) {
                return -1;
            } catch (InvalidDataException e) {
                return -1;
            }
            return (int) mp3file.getLengthInMilliseconds();
        }

        private static String detectExt(String filePath) {
            String oldExt = Ut.file.getExtensionByStringHandling(filePath).orElse("unknown");
            String newExt = new Tika().detect(filePath).split("/")[1].toLowerCase().trim();

            switch (newExt) {
                case "mpeg":
                case "vorbis":
                case "vnd.wave":
                    return oldExt;
            }

            return newExt;
        }
    }

    public static class file {
        public static Optional<String> getExtensionByStringHandling(String filename) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1));
        }

        public static String copyToTempFile(String sourceFilePath) {
            String ext = Ut.file.getExtensionByStringHandling(sourceFilePath).orElse("tmp");

            String targetFilePath = genTempFilePathWithExt(ext);

            return copy(sourceFilePath, targetFilePath);
        }

        public static String genTempFilePath(String dirPath, String ext) {
            return (dirPath + "/" + UUID.randomUUID() + "." + ext).replace('\\', '/');
        }

        public static String genTempFilePathWithExt(String ext) {
            return genTempFilePath(getTempFileDirPath(), ext);
        }

        public static String genTempFilePath(String dirPath) {
            return genTempFilePath(dirPath, "temp");
        }

        private static String getTempFileDirPath() {
            return System.getProperty("java.io.tmpdir");
        }

        public static String genTempFilePath() {
            return genTempFilePath(getTempFileDirPath());
        }

        @SneakyThrows
        public static String copy(String sourceFilePath, String targetFilePath) {
            RandomAccessFile sourceFile = new RandomAccessFile(sourceFilePath, "r");
            RandomAccessFile newFile = new RandomAccessFile(targetFilePath, "rw");

            FileChannel source = sourceFile.getChannel();
            FileChannel target = newFile.getChannel();

            source.transferTo(0, source.size(), target);

            return targetFilePath;
        }

        public static void delete(String filePath) {
            if ( filePath == null ) return;
            new File(filePath).delete();
        }

        public static String renameToMetadata1Format(String sourceFilePath) {
            File sourceFile = new File(sourceFilePath);

            String len = Ut.media.getMediaLength(sourceFilePath) + "";
            String fileSize = "" + sourceFile.length();
            String ext = Ut.media.detectExt(sourceFilePath);

            String[] nowBits = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM--dd_HH_mm_ss")).split("--");
            String fileName = nowBits[0] + "_" + nowBits[1];
            fileName += "___";
            fileName += "len_" + len;
            fileName += "__fs_" + fileSize;
            fileName += "." + ext;

            String targetFilePath = sourceFile.getParent().replace('\\', '/') + "/" + fileName;
            rename(sourceFilePath, targetFilePath);

            return targetFilePath;

        }

        @SneakyThrows
        private static void rename(String sourceFilePath, String targetFilePath) {
            if (new File(sourceFilePath).renameTo(new File(targetFilePath)) == false) {
                copy(sourceFilePath, targetFilePath);
                delete(sourceFilePath);
            }
        }

        public static class upload {

            @Getter
            @AllArgsConstructor
            public static class DiskRs implements DiskSavedFile {
                String baseDir;
                String typeCode;
                String originFilename;
                String ext;
                String dirPath;
                String dirName;
                String filePath;
            }

            public static DiskRs disk(String baseDir, String typeCode, MultipartFile file) {
                String originFilename = file.getOriginalFilename();
                String ext = getExtensionByStringHandling(originFilename).orElse("temp");
                String dirName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM"));
                String dirPath = baseDir + "/" + typeCode + "/" + dirName;
                String fileName = UUID.randomUUID().toString();
                String filePath = dirPath + "/" + fileName + "." + ext;

                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File target = new File(filePath);

                try {
                    file.transferTo(target);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                filePath = renameToMetadata1Format(filePath);

                return new DiskRs(baseDir, typeCode, originFilename, ext, dirPath, dirName, filePath);
            }
        }
    }

    @SneakyThrows
    public static void sleep(int millis) {
        Thread.sleep(millis);
    }

    public static class date {
        @SneakyThrows
        public static int parseDurationSecondsStringToInt(String input) {
            int indexOfSlash = input.indexOf("/");

            if (indexOfSlash != -1) {
                int duration = Integer.parseInt(input.substring(0, indexOfSlash));
                int scale = Integer.parseInt(input.substring(indexOfSlash + 1));

                return duration * 1000 / scale;
            }

            if ( input.contains(":") ) {
                List<String> split = Arrays.asList(input.split(":"));
                Collections.reverse(split);
                return IntStream.range(0, split.size())
                        .map(i -> Integer.parseInt(split.get(i)) * (int) Math.pow(60, i))
                        .sum() * 100;
            }

            return Integer.parseInt(input);
        }

        public static LocalDateTime bitsToLocalDateTime(List<Integer> bits) {
            return LocalDateTime.of(bits.get(0), bits.get(1), bits.get(2), bits.get(3), bits.get(4), bits.get(5), bits.get(6));
        }

        public static int getEndDayOf(int year, int month) {
            String yearMonth = year + "-" + "%02d".formatted(month);

            return getEndDayOf(yearMonth);
        }

        public static int getEndDayOf(String yearMonth) {
            LocalDate convertedDate = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            convertedDate = convertedDate.withDayOfMonth(
                    convertedDate.getMonth().length(convertedDate.isLeapYear()));

            return convertedDate.getDayOfMonth();
        }

        public static LocalDateTime parse(String pattern, String dateText) {
            return LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern(pattern));
        }

        public static LocalDateTime parse(String dateText) {
            return parse("yyyy-MM-dd HH:mm:ss.SSSSSS", dateText);
        }
    }

    private static ObjectMapper getObjectMapper() {
        return (ObjectMapper) AppConfig.getContext().getBean("objectMapper");
    }

    public static String nf(long number) {
        return String.format("%,d", (int) number);
    }

    public static String getTempPassword(int length) {
        int index = 0;
        char[] charArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; i++) {
            index = (int) (charArr.length * Math.random());
            sb.append(charArr[index]);
        }

        return sb.toString();
    }

    public static class json {

        public static String toStr(Object obj) {
            try {
                return getObjectMapper().writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Map<String, Object> toMap(String jsonStr) {
            try {
                return getObjectMapper().readValue(jsonStr, LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Map<String, Object> toMap(Object obj) {
            return toMap(toStr(obj));
        }
    }

    public static <K, V> Map<K, V> mapOf(String... args) {
        return mapOf((Object[])args);
    }

    public static <K, V> Map<K, V> mapOf(Object... args) {
        Map<K, V> map = new LinkedHashMap<>();

        int size = args.length / 2;

        for (int i = 0; i < size; i++) {
            int keyIndex = i * 2;
            int valueIndex = keyIndex + 1;

            K key = (K) args[keyIndex];
            V value = (V) args[valueIndex];

            map.put(key, value);
        }

        return map;
    }

    public static class url {
        public static boolean isUrl(String url) {
            if (url == null) return false;
            return url.matches("^(https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/?([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?$");
        }

        public static String addQueryParam(String url, String paramName, String paramValue) {
            if (url.contains("?") == false) {
                url += "?";
            }

            if (url.endsWith("?") == false && url.endsWith("&") == false) {
                url += "&";
            }

            url += paramName + "=" + paramValue;

            return url;
        }

        public static String modifyQueryParam(String url, String paramName, String paramValue) {
            url = deleteQueryParam(url, paramName);
            url = addQueryParam(url, paramName, paramValue);

            return url;
        }

        private static String deleteQueryParam(String url, String paramName) {
            int startPoint = url.indexOf(paramName + "=");
            if (startPoint == -1) return url;

            int endPoint = url.substring(startPoint).indexOf("&");

            if (endPoint == -1) {
                return url.substring(0, startPoint - 1);
            }

            String urlAfter = url.substring(startPoint + endPoint + 1);

            return url.substring(0, startPoint) + urlAfter;
        }

        public static String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }

        public static String getQueryParamValue(String url, String paramName, String defaultValue) {
            String[] urlBits = url.split("\\?", 2);

            if (urlBits.length == 1) {
                return defaultValue;
            }

            urlBits = urlBits[1].split("&");

            String param = Arrays.stream(urlBits)
                    .filter(s -> s.startsWith(paramName + "="))
                    .findAny()
                    .orElse(paramName + "=" + defaultValue);

            String value = param.split("=", 2)[1].trim();

            return value.length() > 0 ? value : defaultValue;
        }
    }

    public static class sp {

        public static <T> ResponseEntity<RsData<T>> responseEntityOf(RsData<T> rsData) {
            return responseEntityOf(rsData, null);
        }

        public static <T> ResponseEntity<RsData<T>> responseEntityOf(RsData<T> rsData, HttpHeaders headers) {
            return new ResponseEntity<>(rsData, headers, rsData.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        }

        public static HttpHeaders httpHeadersOf(String... args) {
            HttpHeaders headers = new HttpHeaders();

            Map<String, String> map = Ut.mapOf(args);

            for (String key : map.keySet()) {
                String value = map.get(key);
                headers.set(key, value);
            }

            return headers;
        }
    }
}
