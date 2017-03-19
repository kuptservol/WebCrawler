package ru.skuptsov.robot.crawler.parser;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.testng.annotations.Test;
import ru.skuptsov.robot.model.Page;
import ru.skuptsov.robot.model.ParsedPage;
import ru.skuptsov.robot.crawler.parser.impl.WikiPageLinksParser;

import java.io.IOException;
import java.net.URL;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class WikiPageLinksParserTest {
    private WikiPageLinksParser wikiPageLinksParser = new WikiPageLinksParser();

    @Test
    public void test() throws IOException {
        URL url = Resources.getResource("wiki_test_page.html");
        byte[] html_page_data = Resources.toString(url, Charsets.UTF_8).getBytes();

        Page page = new Page(
                new URL("https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D0%BE%D0%B8%D1%87%D0%BD%D1%8B%D0%B9_%D1%84%D0%B0%D0%B9%D0%BB"),
                html_page_data);
        page.setContentType("html");

        ParsedPage parsedPage = wikiPageLinksParser.parse(page);

        assertEquals(parsedPage.getTitle(), "Двоичный файл — Википедия");
        assertEquals(parsedPage.getOutgoingLinks().size(), 22);
    }
}
