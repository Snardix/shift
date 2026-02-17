import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        boolean appendMode = false;
        boolean shortStat = false;
        boolean fullStat = false;
        String outputPath = ".";
        String prefix = "";

        List<String> inputFiles = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a":
                    appendMode = true;
                    break;
                case "-s":
                    shortStat = true;
                    break;
                case "-f":
                    fullStat = true;
                    break;
                case "-o":
                    if (i + 1 < args.length) {
                        outputPath = args[++i];
                    } else {
                        System.err.println("Ошибка: не указан путь после -o");
                        return;
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        prefix = args[++i];
                    } else {
                        System.err.println("Ошибка: не указан префикс после -p");
                        return;
                    }
                    break;
                default:
                    inputFiles.add(args[i]);
            }
        }

        if (inputFiles.isEmpty()) {
            System.err.println("Ошибка: не указаны входные файлы");
            return;
        }

        long intCount = 0;
        long floatCount = 0;
        long stringCount = 0;

        Double intMin = null, intMax = null, intSum = 0.0;
        Double floatMin = null, floatMax = null, floatSum = 0.0;

        Integer minStrLen = null, maxStrLen = null;

        BufferedWriter intWriter = null;
        BufferedWriter floatWriter = null;
        BufferedWriter stringWriter = null;

        try {

            for (String fileName : inputFiles) {

                File file = new File(fileName);
                if (!file.exists()) {
                    System.err.println("Файл не найден: " + fileName);
                    continue;
                }

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                    String line;

                    while ((line = reader.readLine()) != null) {

                        if (isInteger(line)) {

                            if (intWriter == null) {
                                intWriter = createWriter(outputPath, prefix + "integers.txt", appendMode);
                            }

                            intWriter.write(line);
                            intWriter.newLine();

                            double value = Double.parseDouble(line);
                            intCount++;
                            intSum += value;
                            intMin = (intMin == null || value < intMin) ? value : intMin;
                            intMax = (intMax == null || value > intMax) ? value : intMax;

                        } else if (isFloat(line)) {

                            if (floatWriter == null) {
                                floatWriter = createWriter(outputPath, prefix + "floats.txt", appendMode);
                            }

                            floatWriter.write(line);
                            floatWriter.newLine();

                            double value = Double.parseDouble(line);
                            floatCount++;
                            floatSum += value;
                            floatMin = (floatMin == null || value < floatMin) ? value : floatMin;
                            floatMax = (floatMax == null || value > floatMax) ? value : floatMax;

                        } else {

                            if (stringWriter == null) {
                                stringWriter = createWriter(outputPath, prefix + "strings.txt", appendMode);
                            }

                            stringWriter.write(line);
                            stringWriter.newLine();

                            int len = line.length();
                            stringCount++;
                            minStrLen = (minStrLen == null || len < minStrLen) ? len : minStrLen;
                            maxStrLen = (maxStrLen == null || len > maxStrLen) ? len : maxStrLen;
                        }
                    }

                } catch (IOException e) {
                    System.err.println("Ошибка чтения файла: " + fileName);
                }
            }

            if (intWriter != null) intWriter.close();
            if (floatWriter != null) floatWriter.close();
            if (stringWriter != null) stringWriter.close();

        } catch (IOException e) {
            System.err.println("Ошибка записи выходных файлов: " + e.getMessage());
            return;
        }

        if (shortStat) {
            if (intCount > 0) System.out.println("Integers: count = " + intCount);
            if (floatCount > 0) System.out.println("Floats: count = " + floatCount);
            if (stringCount > 0) System.out.println("Strings: count = " + stringCount);
        }

        if (fullStat) {
            if (intCount > 0) {
                System.out.println("\nIntegers:");
                System.out.println("count = " + intCount);
                System.out.println("min = " + intMin);
                System.out.println("max = " + intMax);
                System.out.println("sum = " + intSum);
                System.out.println("avg = " + (intSum / intCount));
            }

            if (floatCount > 0) {
                System.out.println("\nFloats:");
                System.out.println("count = " + floatCount);
                System.out.println("min = " + floatMin);
                System.out.println("max = " + floatMax);
                System.out.println("sum = " + floatSum);
                System.out.println("avg = " + (floatSum / floatCount));
            }

            if (stringCount > 0) {
                System.out.println("\nStrings:");
                System.out.println("count = " + stringCount);
                System.out.println("minLength = " + minStrLen);
                System.out.println("maxLength = " + maxStrLen);
            }
        }
    }

    private static boolean isInteger(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isFloat(String value) {
        try {
            Double.parseDouble(value);
            return value.contains(".") || value.toLowerCase().contains("e");
        } catch (Exception e) {
            return false;
        }
    }

    private static BufferedWriter createWriter(String path, String fileName, boolean append) throws IOException {
        Path fullPath = Path.of(path, fileName);
        Files.createDirectories(fullPath.getParent());
        return new BufferedWriter(new FileWriter(fullPath.toFile(), append));
    }
}
