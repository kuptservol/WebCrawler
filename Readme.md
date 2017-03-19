
<h2> Description

<h4> Web pages crawling

   Crawling is divided into 4 async stages -
   download page, parse, store on disk, update search index.
   Each one is based on its own configurable thread pool.
   Main loop waits for all async stages to complete.
   The main search work task to look is ru.skuptsov.robot.crawler.CrawlTask.

<h4> Web pages word searching

   While crawling pages a reverse word to pages index is build.
   So, finding a word in pages occurence is very fast - you need to look for a word in index,
     then find all documents by ids.
   In order not to scan for a given search word among whole index on disk,
        search index is partitioned into configurable number of batches - so to find
         a word in index you need to read only one file - O(N/n_backets)

<h2> Run

1. Build

        ./gradlew build
2.  Index

        java -jar ./robot-1.0.jar crawl --url https://en.wikipedia.org/wiki/Sherlock_Holmes --depth 2 {optional --config /config.properties}
3. Search for a word

        java -jar ./robot-1.0.jar search --word Watson {optional --config /config.properties}