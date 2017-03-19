package ru.skuptsov.robot.indexer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.testng.annotations.Test;
import ru.skuptsov.robot.indexer.impl.WordTokenizer;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class WordTokenizerTest {

    @Test
    public void test() throws IOException {
        URL url = Resources.getResource("wiki_test_page_utf.html");
        String html_page_data = Resources.toString(url, Charsets.UTF_8);

        Set<String> tokens = WordTokenizer.getTokens(html_page_data);

        assertTrue(tokens.contains("жанра"));
        assertTrue(!tokens.contains(""));
    }
}
