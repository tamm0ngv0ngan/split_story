package com.company;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String DIRECTORY = "/Users/ducnguyen/test/Text";
    private static FileWriter trashedFw;

    public static void writeTrashedFile(String data) {
        try {
            Main.trashedFw.write(data + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean checkIgnore(String data) {
        Pattern pattern = Pattern.compile("^[\\s]*C\\u1ea9m[\\s]*Y[\\s]*Xu\\u00e2n[\\s]*Thu[\\s]*");
        if (pattern.matcher(data).matches()) {
            return true;
        }

        pattern = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}");
        if (pattern.matcher(data).find()) {
            return true;
        }

        pattern = Pattern.compile("thienthucac");
        if (pattern.matcher(data).find()) {
            return true;
        }

        pattern = Pattern.compile("This Useful Post:");
        if (pattern.matcher(data).find()) {
            return true;
        }

        pattern = Pattern.compile("book\\.zongheng\\.com");
        if (pattern.matcher(data).find()) {
            return true;
        }

        pattern = Pattern.compile("^[\\s]*Tr\\u1ea3[\\s]l\\u1eddi[\\s]*[\\s]*Tr\\u1ea3[\\s]l\\u1eddi[\\s]*k\\u00e8m[\\s]*Tr\\u00edch[\\s]*d\\u1eabn[\\s]*");
        if (pattern.matcher(data).find()) {
            return true;
        }

        pattern = Pattern.compile("\\u11ff\\u051e\\u0e0e\\u04de\\u143f\\u0541\\u0751");
        if (pattern.matcher(data).find()) {
            return true;
        }

        pattern = Pattern.compile("\\s*#[0-9]*\\s*");
        if (pattern.matcher(data).find()) {
            return true;
        }

        pattern = Pattern.compile("^[\\s]*T\\u00e1c[\\s]*gi\\u1ea3:[\\s]*Sa[\\s]*M\\u1ea1c[\\s]*");
        return pattern.matcher(data).matches();
    }

    public static HashMap<String, String> checkNewChapter(String data) {
        HashMap<String, String> result = new HashMap<>();
        Pattern pattern = Pattern.compile("^Ch\u01b0\u01a1ng[\\s]+([0-9]+):[\\s]*(\\p{L}[\\p{L}|\\s]+\\p{L})[\\.|\\s]*");
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches()) {
            result.put("chapter_number", matcher.group(1));
            result.put("chapter_name", matcher.group(2));
            result.put("is_multiple", "false");
        }

        pattern = Pattern.compile("^\u1ea8n[\\s]+Ch\u01b0\u01a1ng[\\s]+([0-9]+):[\\s]*(\\p{L}[\\p{L}|\\s]+\\p{L})[\\.|\\s]*");
        matcher = pattern.matcher(data);
        if (matcher.matches()) {
            result.put("chapter_number", matcher.group(1));
            result.put("chapter_name", matcher.group(2));
            result.put("is_multiple", "false");
        }

        pattern = Pattern.compile("^Ch\u01b0\u01a1ng[\\s]+([0-9]+)\\.[0-9]:[\\s]*(\\p{L}[\\p{L}|\\s]+\\p{L})[\\.|\\s]*");
        matcher = pattern.matcher(data);
        if (matcher.matches()) {
            result.put("chapter_number", matcher.group(1));
            result.put("chapter_name", matcher.group(2));
            result.put("is_multiple", "true");
        }


        pattern = Pattern.compile("^\u1ea8n[\\s]+Ch\u01b0\u01a1ng[\\s]+([0-9]+)\\.[0-9]:[\\s]*(\\p{L}[\\p{L}|\\s]+\\p{L})[\\.|\\s]*");
        matcher = pattern.matcher(data);
        if (matcher.matches()) {
            result.put("chapter_number", matcher.group(1));
            result.put("chapter_name", matcher.group(2));
            result.put("is_multiple", "true");
        }

        return result;
    }

    public static HashMap<Integer, Chapter> getFileData(File extractedFile) throws IOException {
        int chapterNumber = 0;
        Scanner reader = new Scanner(extractedFile);
        HashMap<Integer, Chapter> chapters = new HashMap<>();
        Chapter chapter = new Chapter();
        boolean hasFirst = false;
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            if (checkIgnore(data)) {
                writeTrashedFile(data);
                continue;
            }
            HashMap<String, String> checkNewChap = checkNewChapter(data);
            if (checkNewChap.size() == 0) {
                if (hasFirst) {
                    chapter.increaseNumOfLines();
                    chapter.appendData(data);
                } else {
                    writeTrashedFile(data);
                }
            } else {
                if (chapter.getNumOfLines() > 50 && hasFirst) {
                    if (chapters.containsKey(chapterNumber)) {
                        Chapter tmpChapter = chapters.get(chapterNumber);
                        tmpChapter.mergeChapter(chapter.clone());
                        chapters.put(chapterNumber, tmpChapter);
                    } else {
                        chapters.put(chapterNumber, chapter.clone());
                    }
                    chapter.resetData();
                }
                chapterNumber = Integer.parseInt(checkNewChap.get("chapter_number"));
                chapter.setChapterNumber(chapterNumber);
                chapter.setChapterName(checkNewChap.get("chapter_name"));
                chapter.setMultiple(Boolean.parseBoolean(checkNewChap.get("is_multiple")));
                hasFirst = true;
                writeTrashedFile(data);
            }
        }
        if (chapter.getNumOfLines() > 50 && hasFirst) {
            if (chapters.containsKey(chapterNumber)) {
                Chapter tmpChapter = chapters.get(chapterNumber);
                tmpChapter.mergeChapter(chapter);
                chapters.put(chapterNumber, tmpChapter);
            } else {
                chapters.put(chapterNumber, chapter);
            }
        }
        return chapters;
    }

    public static void generateChapterFile(HashMap<Integer, Chapter> chapters) {
        for (Map.Entry<Integer, Chapter> entry: chapters.entrySet()) {
            Chapter chapter = entry.getValue();
            chapter.generateFileChapter();
        }
    }

    public static void main(String[] args) throws IOException {
        Main.trashedFw = new FileWriter("/Users/ducnguyen/test/trashed.txt", true);
        File folder = new File(Main.DIRECTORY);
        File[] files = folder.listFiles();

//        checkCode();
        assert files != null;
        for (File file: files) {
            if (file.isFile()) {
                HashMap<Integer, Chapter> chapters = getFileData(file);
                generateChapterFile(chapters);
            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getName());
            }
        }

        Main.trashedFw.close();
    }

    private static void checkCode() {
        System.out.println( "\\u" + Integer.toHexString('ᇿ' | 0x10000).substring(1) );
        System.out.println( "\\u" + Integer.toHexString('Ԟ' | 0x10000).substring(1) );
        System.out.println( "\\u" + Integer.toHexString('ฎ' | 0x10000).substring(1) );
        System.out.println( "\\u" + Integer.toHexString('Ӟ' | 0x10000).substring(1) );
        System.out.println( "\\u" + Integer.toHexString('ᐿ' | 0x10000).substring(1) );
        System.out.println( "\\u" + Integer.toHexString('Ձ' | 0x10000).substring(1) );
        System.out.println( "\\u" + Integer.toHexString('ݑ' | 0x10000).substring(1) );

//        Cẩm Y Xuân Thu
        Pattern pattern = Pattern.compile("^[\\s]*Tr\\u1ea3[\\s]l\\u1eddi[\\s]*[\\s]*Tr\\u1ea3[\\s]l\\u1eddi[\\s]*k\\u00e8m[\\s]*Tr\\u00edch[\\s]*d\\u1eabn[\\s]*");
//        Pattern pattern = Pattern.compile("^Ch\u01b0\u01a1ng\s([0-9]+):[\\p{L}|\\s|.]+");
        Matcher match = pattern.matcher("Trả lời   Trả lời kèm Trích dẫn");
        System.out.println(match.matches());
        while(match.find()) {
            System.out.println("aaaa");
        }


        System.exit(0);
    }
}
