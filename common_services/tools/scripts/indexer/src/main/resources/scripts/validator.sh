java -XX:+HeapDumpOnOutOfMemoryError -Xmx8192m -XX:HeapDumpPath=/X/indexer_test/dumps/heapdump.log -Dlog4j.configuration=file:///X/index_test/indexer/log4j.properties -classpath ".:./indexer-validator-jar-with-dependencies.jar" de.mpg.escidoc.tools/Validator /X/index_test/indexer/index3 /X/index_test/backup/item_container_admin  1>x 2>xx&

