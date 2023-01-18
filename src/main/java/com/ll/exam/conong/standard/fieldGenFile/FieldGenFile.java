package com.ll.exam.conong.standard.fieldGenFile;

import com.ll.exam.conong.base.AppConfig;
import com.ll.exam.conong.standard.util.Ut;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FieldGenFile {
    @Getter
    private final String filePath;

    private Map<String, String> infs;

    private Map<String, String> _getInfs() {
        Map<String, String> infs = new HashMap<>();

        String[] filePathBits = filePath.split("/");
        infs.put("typeCode", filePathBits[filePathBits.length - 3]);
        infs.put("dirName", filePathBits[filePathBits.length - 2]);
        infs.put("fileName", filePathBits[filePathBits.length - 1]);
        infs.put("fileExt", filePathBits[filePathBits.length - 1].split("\\.")[1]);

        String metadataStr = infs.get("fileName").split("___")[1];
        metadataStr = metadataStr.split("\\.")[0];

        Arrays.stream(metadataStr.split("__"))
                .map(s -> s.split("_"))
                .forEach(strings -> {
                    String key = "meta__" + strings[0];
                    String value = strings[1];
                    infs.put(key, value);
                });

        return infs;
    }

    private Map<String, String> getInfs() {
        if (infs == null) {
            infs = _getInfs();
        }

        return infs;
    }

    private String getTypeCode() {
        return getInfs().get("typeCode");
    }

    private String getDirName() {
        return getInfs().get("dirName");
    }

    private String getFileName() {
        return getInfs().get("fileName");
    }

    private String getFileExt() {
        return getInfs().get("fileExt");
    }

    private int getSize() {
        return Integer.parseInt(getInfs().get("meta__len"));
    }

    public String getSizeHr() {
        return "%.1f".formatted(getSize() / 1000.0) + "s";
    }

    private int getFileSize() {
        return Integer.parseInt(getInfs().get("meta__fs"));
    }

    public String getFileSizeHr() {
        return "%.3f".formatted(getFileSize() / (1024 * 1024.0)) + "MB";
    }

    public FieldGenFile(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return "/" + AppConfig.getGenDirName() + "/" + getTypeCode() + "/" + getDirName() + "/" + getFileName();
    }

    public String getSourceType() {
        return "audio/" + getFileExt();
    }

    @Override
    public String toString() {
        return filePath;
    }

    public void deleteOnDisk() {
        Ut.file.delete(filePath);
    }
}
