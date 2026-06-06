# Tangled Lines

Java game about moving nodes to untangle intersecting lines.

## Requirements

- JDK 17+

## Run

```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

## Test

```bash
javac -cp "lib/*" -d out $(find src tests -name "*.java")
java -jar lib/junit-platform-console-standalone.jar -cp out --scan-classpath
```
