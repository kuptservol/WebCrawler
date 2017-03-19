package ru.skuptsov.robot.util.io;

import ru.skuptsov.robot.exception.PageStorageException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class IOUtils {
    public static void createIfNotExists(Path path) {
        if (!exists(path)) {
            {
                try {
                    createDirectory(path);
                } catch (IOException e) {
                    throw new PageStorageException("Can't create directory from path " + path);
                }
            }
        }
    }

    public static BufferedWriter newBufferedWriter(Path path) throws FileNotFoundException {
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        encoder.onMalformedInput(CodingErrorAction.IGNORE);

        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toAbsolutePath().toString()), encoder));
    }
}
