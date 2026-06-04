# Tangled Lines

Tangled Lines is a Java desktop game where the player moves nodes to untangle lines.

## Requirements

- JDK 17 or newer
- IntelliJ IDEA
- Gson jar in the `lib/` directory

The application uses levels from the `levels/` directory, so run it from the project root.

## Run in IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Make sure Gson is added as a project library.
3. Open `src/Main.java`.
4. Run the `Main` class.

## Run from Terminal

On macOS/Linux:

```bash
javac -cp "lib/*" -d out $(find src -name "*.java")
java -cp "out:lib/*" Main
```

On Windows PowerShell:

```powershell
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -cp "lib/*" -d out $files
java -cp "out;lib/*" Main
```

## Download Gson Manually

Use this method if Gson is missing or IntelliJ does not see it.

1. Download the latest `gson-<version>.jar` from Maven Central:
   - Artifact page: https://central.sonatype.com/artifact/com.google.code.gson/gson
   - Repository files: https://repo.maven.apache.org/maven2/com/google/code/gson/gson/
2. Move the downloaded jar to the project `lib/` directory.
3. In IntelliJ IDEA, open `File -> Project Structure -> Libraries`.
4. Click `+`, choose `Java`, and select the downloaded jar from `lib/`.
5. Apply the changes and run `src/Main.java` again.

If IntelliJ already has an older Gson library configured, remove it first or replace it with the new jar.
