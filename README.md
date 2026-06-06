# Tangled Lines

Tangled Lines is a Java desktop game where the player moves nodes to untangle lines.

## Requirements

- JDK 17 or newer
- IntelliJ IDEA

The application creates levels in code through `SimpleSeeder`.

## Run in IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Open `src/Main.java`.
3. Run the `Main` class.

## Run from Terminal

On macOS/Linux:

```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

On Windows PowerShell:

```powershell
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
java -cp out Main
```
