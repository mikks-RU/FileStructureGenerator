import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileStructureGenerator {
    private static final String ROOT_FOLDER = "C:\\dev\\repository\\sbsolife\\services\\kontur-processing\\src\\main\\java\\ru\\sberinsur\\kontur\\jsonmodel\\dockflow";
    private static final String OUTPUT_FILE = "C:\\dev\\repository\\docflow_fns_structure.txt";

    private static final Set<String> EXCLUDED_PATHS = new HashSet<>(Arrays.asList("target", ".git", ".idea"));
    private static final Set<String> EXCLUDED_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".class", ".jar", ".versionsBackup", ".xlsx", ".ttf", ".jasper", ".iml", ".docx", ".xls", ".doc",
            ".png", ".jpg", ".gif", ".ico", ".cmd", ".txt", ".log", ".pdf"));
    private static final Set<String> EXCLUDED_FILES = new HashSet<>(Arrays.asList("example.xml"));

    public static void main(String[] args) {
        Path rootPath = Paths.get(ROOT_FOLDER);
        Path outputPath = Paths.get(OUTPUT_FILE);

        try {
            StringBuilder structure = new StringBuilder();

            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relativePath = rootPath.relativize(file).toString();

                    // Пропускаем файлы с исключенными расширениями
                    for (String extension : EXCLUDED_FILE_EXTENSIONS) {
                        if (relativePath.endsWith(extension)) {
                            return FileVisitResult.CONTINUE;
                        }
                    }

                    // Пропускаем исключенные файлы
                    if (EXCLUDED_FILES.contains(relativePath)) {
                        return FileVisitResult.CONTINUE;
                    }

                    // Пропускаем файлы из исключенных директорий
                    for (String excludedPath : EXCLUDED_PATHS) {
                        if (relativePath.contains(excludedPath)) {
                            return FileVisitResult.CONTINUE;
                        }
                    }

                    structure.append(relativePath).append("\n");
                    String content = new String(Files.readAllBytes(file));
                    structure.append("---\n").append(content).append("\n---\n");

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });

            Files.write(outputPath, structure.toString().getBytes());
            System.out.println("File structure generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}