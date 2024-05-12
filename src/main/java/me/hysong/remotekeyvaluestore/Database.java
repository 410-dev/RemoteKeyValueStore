package me.hysong.remotekeyvaluestore;

import java.io.*;
import java.util.ArrayList;

public class Database {

    private static String getRoot() {
        String root = System.getProperty("user.home");
        root += "/website/data/RemoteKeyValueStore/";
        return root.replace("/", File.separator);
    }

    public static void write(String name, String value, String key, boolean append, boolean forceAbsolutePath) throws IOException {
        String root = forceAbsolutePath ? getRoot() : "";
        String id = name;
        name = root + name;
        name = name.replace("/", File.separator);

        File file = new File(name);
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            return;
        }
        if (key != null && !key.isEmpty()) Authorization.makeAuth(id, key);

        System.out.println("Writing to " + name + " with value " + value);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
        writer.write(value);
        writer.close();
    }

    public static String read(String name) throws IOException {
        String root = getRoot();
        name = root + name;
        name = name.replace("/", File.separator);

        File file = new File(name);
        if (!file.exists()) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        String value = builder.toString().trim();
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
        String root = getRoot();
        ArrayList<String> list = recursiveSearch(root);
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s.substring(root.length()));
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void delete(String path) {
        String root = getRoot();
        path = root + path;
        path = path.replace("/", File.separator);

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
