import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

public class Main {
    public static void main(String[] args) {
        String jarFilePath = "h08-ab12cdef-Max-Mustermann-submission.jar";
        String destDirectory = "./";
        String fileToEdit = "/h08/CalculatorTests.java";
        String oldText = "private <T extends Throwable> void testException";
        String newText = "private <T extends Object> void testException";
        String newJarFilePath = "h08-ab12cdef-Max-Mustermann-submission_t.jar";

        try {
            unpackJar(jarFilePath, destDirectory);
            replaceTextInFile(destDirectory + fileToEdit, oldText, newText);
            repackJar(destDirectory, newJarFilePath);
            System.out.println("JAR file updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void unpackJar(String jarFilePath, String destDirectory) throws IOException {
        FileInputStream fis = new FileInputStream(jarFilePath);
        JarInputStream jis = new JarInputStream(fis);
        JarEntry entry = jis.getNextJarEntry();

        while (entry != null) {
            String fileName = entry.getName();
            File file = new File(destDirectory + File.separator + fileName);

            if (fileName.endsWith("/")) {
                file.mkdirs();
            } else {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = jis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }

                fos.close();
            }

            entry = jis.getNextJarEntry();
        }

        jis.close();
        fis.close();
    }

    private static void replaceTextInFile(String filePath, String oldText, String newText) throws IOException {
        File file = new File(filePath);
        String fileContent = readFile(file);
        String newContent = fileContent.replaceAll(oldText, newText);
        writeFile(file, newContent);
    }

    private static void repackJar(String sourceDirectory, String newJarFilePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(newJarFilePath);
        JarOutputStream jos = new JarOutputStream(fos);
        addToJar(sourceDirectory, jos);
        jos.close();
        fos.close();
    }

    private static void addToJar(String sourceDirectory, JarOutputStream jos) throws IOException {
        File sourceFile = new File(sourceDirectory);
        if (sourceFile.isDirectory()) {
            String[] files = sourceFile.list();
            for (int i = 0; i < files.length; i++) {
                addToJar(sourceFile.getPath() + File.separator + files[i], jos);
            }
        } else {
            FileInputStream fis = new FileInputStream(sourceFile);
            jos.putNextEntry(new JarEntry(sourceFile.getPath()));
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = fis.read(buffer)) != -1) {
                jos.write(buffer, 0, bytesRead);
            }
            jos.closeEntry();
            fis.close();
        }
    }

    private static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        reader.close();
        return stringBuilder.toString();
    }

    private static void writeFile(File file, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.close();
    }
}