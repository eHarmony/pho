export MAVEN_OPTS="-Xms512m -Xmx512m -XX:MaxPermSize=384m -XX:NewSize=256m -XX:MaxNewSize=256m  -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8459,server=y,suspend=n"



mvn jetty:run
