package com.yaazyy.deathnote.config.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Loads and optionally saves YAML files as {@code Map<String, Object>}.
 */
public final class YamlFiles {

    private YamlFiles() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> load(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Missing config file: " + file.getAbsolutePath());
        }
        try (InputStream in = new FileInputStream(file)) {
            Object root = new Yaml().load(in);
            if (root instanceof Map) {
                return (Map<String, Object>) root;
            }
            throw new IOException("YAML root is not a map: " + file.getName());
        }
    }

    public static void save(Map<String, Object> root, File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();
        }
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        opts.setIndent(2);
        try (FileWriter writer = new FileWriter(file)) {
            new Yaml(opts).dump(root, writer);
        }
    }
}
