set JAVA_EXE=E:/Code/Stage/graal/graalvm-ce-java8-20.1.0/bin/java
set LAUNCHER_PATH=./launcher/target/ini-launcher.jar
set LANGUAGE_PATH=./language/target/ini.jar
set MAIN_CLASS=ini.IniMain

set CMD_LINE_ARGS=%*
"%JAVA_EXE%" -Dtruffle.class.path.append="%LANGUAGE_PATH%" -cp "%LAUNCHER_PATH%" "%MAIN_CLASS%" "%CMD_LINE_ARGS%"

@rem E:/Code/Stage/graal/graalvm-ce-java8-20.1.0/bin/java -Dtruffle.class.path.append=./language/target/ini.jar -cp ./launcher/target/ini-launcher.jar ini.IniMain
@rem .\run_ini.bat language/ini/truffle/TestPrintln.ini