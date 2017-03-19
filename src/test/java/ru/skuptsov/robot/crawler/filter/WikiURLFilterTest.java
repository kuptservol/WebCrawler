package ru.skuptsov.robot.crawler.filter;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.skuptsov.robot.crawler.filter.impl.WikiURLFilterImpl;

import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class WikiURLFilterTest {

    WikiURLFilterImpl wikiURLFilter = new WikiURLFilterImpl();

    @DataProvider
    public Object[][] getUrls() {
        return new Object[][]{
                {"https://ru.wikipedia.org/wiki/%D0%A8%D0%B5%D1%80%D0%BB%D0%BE%D0%BA_%D0%A5%D0%BE%D0%BB%D0%BC%D1%81", true},
                {"https://en.wikipedia.org/wiki/William_Sherlock", true},
                {"https://ru.wikipedia.org/wiki/29_%D0%B3%D0%BE%D0%B4_%D0%B4%D0%BE_%D0%BD._%D1%8D.", true},
                {"https://d3.ru/", false}
        };
    }

    @Test(dataProvider = "getUrls")
    public void checkUrl(String url, boolean isValid) throws MalformedURLException {
        assertEquals(wikiURLFilter.test(new URL(url)), isValid);
    }
}
