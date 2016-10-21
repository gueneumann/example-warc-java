# How to Use it to Extract Sentences from Web pages

Günter Neumann 2016/10/20

Maybe I can use some of the code from https://github.com/xdavidjung/cluewebextractor
and adapt it to java.

I have test files in /Users/gune00/ownCloud/cluebweb09-de/

## http://boilerpipe-web.appspot.com/

I integrated boilerpipe via

	<dependency>
		<groupId>com.robbypond</groupId>
		<artifactId>boilerpipe</artifactId>
		<version>1.2.3</version>
	</dependency>

	
Installed and tested, and works.

## How to combined with commoncrawl ?

First test in test.gn.TestReadTextFromWARC

- streams a local *.warc.gz file

- for each record of type response

- gets the URL and Content in UTF-8

- and than extracts title and text using test.gn.TextExtractorDemo called via test.gn.MyProcessWarcRecord

- it creates a boilerpipe HMTLDocument and extracts title and text using getTitle() and getText()

