package me.hysong.remotekeyvaluestore;

import java.io.*;
import java.util.ArrayList;

public class Database {

    public static void write(String name, String value, String key, boolean append) throws IOException {
        String root = System.getProperty("user.home");
        root += "/website/data/RemoteKeyValueStore/";
        name = root + name;
        name = name.replace("/", File.separator);

        File file = new File(name);
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            return;
        }
        Authorization.makeAuth(name, key);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
        writer.write(value);
        writer.close();
    }

    public static String read(String name) throws IOException {
        String root = System.getProperty("user.home");
        root += "/website/data/RemoteKeyValueStore/";
        name = root + name;
        name = name.replace("/", File.separator);

        File file = new File(name);
        if (!file.exists()) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String value = reader.readLine();
        reader.close();
        return value;
    }

    private static ArrayList<String> recursiveSearch(String path) {
        ArrayList<String> list = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            return list;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(recursiveSearch(f.getAbsolutePath()));
            } else {
                list.add(f.getAbsolutePath());
            }
        }
        return list;
    }

    public static String list() {
        String root = System.getProperty("user.home");
        root += "/website/data/RemoteKeyValueStore/";
        root = root.replace("/", File.separator);
        ArrayList<String> list = recursiveSearch(root);
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s.substring(root.length()));
            builder.append("\n");
        }
        return builder.toString();
    }
}
