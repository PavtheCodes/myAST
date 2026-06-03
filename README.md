# Java Static Code Analyzer

A static code analysis tool for Java that automatically detects naming conventions and enforces best practices from *Effective Java*.

[![Java Version](https://img.shields.io/badge/Java-17-blue.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## Features

- **Automatic naming convention detection** – learns your project's style (no configuration needed)
- **10+ rules from Effective Java** – catches common issues before they become bugs
- **Zero-config by default** – works out of the box, configurable when needed
- **Suggestions, not blockers** – warnings don't break your build
- **Modular design** – easy to add new rules
- **Two-pass analysis** – adapts to your existing codebase

## Quick Start

### As a CLI tool

```bash
# Download the JAR
wget https://github.com/pavthecodes/java-linter/releases/download/v1.0/linter.jar

# Analyze a single file
java -jar linter.jar src/MyClass.java

# Analyze an entire directory
java -jar linter.jar src/main/java
