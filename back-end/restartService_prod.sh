ps -ef | grep java | grep vertx-base-1.0.jar |  awk '{print "kill -9 ", $2}' > 1.sh
chmod 755 1.sh
./1.sh

rm -rf 1.sh

sleep 2

nohup java -Xmx1024m -Xms128m -jar -Dlog4j2.configurationFile=config/log4j2.yml -Dfile.encoding=UTF-8 vertx-base-1.0.jar > $(date '+%Y%m%d').txt &