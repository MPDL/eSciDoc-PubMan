java -XX:+HeapDumpOnOutOfMemoryError -Xmx8192m -XX:HeapDumpPath=/X/indexer_test/dumps/heapdump.log -Dlog4j.configuration=file:///X/index_test/indexer/log4j.properties -jar indexer-jar-with-dependencies.jar -i /opt/fedora/data/objects 1>x 2>xx&

