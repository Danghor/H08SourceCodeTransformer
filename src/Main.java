import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

public class Main {
    public static void main(String[] args) throws IOException {
        JarFile jarFile = new JarFile("h08-ab12cdef-Max-Mustermann-submission.jar");
        Enumeration en = jarFile.entries();
        while (en.hasMoreElements()) {
            String ent = proc(en.nextElement());
            if (ent.indexOf("/") > 0) {
                String fil = ent.substring(0, ent.indexOf("/"));
                System.out.println(fil);
                File local = new File(fil);
                if (!local.exists())
                    local.mkdirs();
            }
            if (ent.indexOf(".") > 0) {
                int n = ent.length();
                String fil1 = ent.substring(ent.lastIndexOf("/") + 1, n);
                System.out.println(fil1);
                extract(jarFile.getName(), ent);
            }
        }
    }

    public static String proc(Object obj) {
        JarEntry entry = (JarEntry) obj;
        String name = entry.getName();
        System.out.println("\nEntry Name: " + name);
        return (name);
    }

    public static void extract(String jarName, String entryName) throws IOException, ZipException {
        JarFile jar = new JarFile(jarName);
        System.out.println(jarName + " opened.");

        try {
            // Get the entry and its input stream.

            JarEntry entry = jar.getJarEntry(entryName);

            // If the entry is not null, extract it. Otherwise, print a
            // message.

            if (entry != null) {
                // Get an input stream for the entry.

                InputStream entryStream = jar.getInputStream(entry);

                try {
                    // Create the output file (clobbering the file if it exists).

                    File yourFile = new File(entry.getName());
                    if (!yourFile.isDirectory()) {
                        var parentPath = yourFile.getParent();
                        if (parentPath != null) {
                            var parent = new File(yourFile.getParent());
                            parent.mkdirs();
                        }
                    }
                    FileOutputStream file = new FileOutputStream(entry.getName());

                    try {
                        // Allocate a buffer for reading the entry data.

                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        // Read the entry data and write it to the output file.

                        while ((bytesRead = entryStream.read(buffer)) != -1) {
                            file.write(buffer, 0, bytesRead);
                        }

                        System.out.println(entry.getName() + " extracted.");
                    } finally {
                        file.close();
                    }
                } finally {
                    entryStream.close();
                }
            } else {
                System.out.println(entryName + " not found.");
            } // end if
        } finally {
            jar.close();
            System.out.println(jarName + " closed.");
        }
    }
}