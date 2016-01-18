export JAVA_HOME=/usr/
if pgrep "crawl" > /dev/null
then
    echo "Crawler still working. See you tomorrow!"
else
    ../../../home/edgaras/apache-nutch-1.8/bin/crawl ../../../home/edgaras/apache-nutch-1.8/urls ../../../home/edgaras/apache-nutch-1.8/manoknyga http://158.129.140.188:8983/solr/ 1
fi
