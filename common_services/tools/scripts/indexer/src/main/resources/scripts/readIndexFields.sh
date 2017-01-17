java -XX:+HeapDumpOnOutOfMemoryError -Xmx8192m -XX:HeapDumpPath=/X/indexer_test/dumps/heapdump.log -Dlog4j.configuration=file:///X/index_test/indexer/log4j.properties -classpath ".:./indexer-reader-jar-with-dependencies.jar" de.mpg.escidoc.tools.IndexFieldReader /X/index_test/indexer/index2 1>x 2>xx&

