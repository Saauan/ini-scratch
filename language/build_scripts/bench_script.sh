java -Xss10M -classpath target/ini.jar -Dgraal.Dump=Truffle:1 -Dgraal.TruffleBackgroundCompilation=false -Dgraal.TraceTruffleCompilation=true -Dgraal.TraceTruffleCompilationDetails=true ini.IniMain ini/truffle/bench/fibonacci.ini
java -Xss10M -classpath target/ini.jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y ini.IniMain ini/truffle/bench/fibonacci.ini
java -classpath target/ini.jar --cpusampler ini.IniMain ini/truffle/bench/fibonacci.ini
java -Xss10M -classpath target/ini.jar --engine.TraceCompilation ini.IniMain ini/truffle/bench/fibonacci.ini

java -Dgraal.TruffleCompilationStatistics=true -Dgraal.TruffleCompilationExceptionsAreFatal=true -Dgraal.Dump -Dgraal.TruffleBackgroundCompilation=true -Djvmci.option.Dump -classpath target/ini.jar ini.IniMain ini/truffle/bench/fibonacci.ini

/graalvm-ce-java8-20.1.0/bin/java -Dgraal.TraceTruffleCompilation=true -Dgraal.TraceTruffleCompilationDetails=true -classpath target/ini.jar ini.IniMain ini/truffle/bench/fibonacci.ini

java -classpath target/ini.jar ini.IniMain ini/truffle/bench/fibonacci.ini

graalvm-ce-java8-20.1.0/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

E:\Code\Stage\graal\graalvm-ce-java8-20.1.0\bin\java