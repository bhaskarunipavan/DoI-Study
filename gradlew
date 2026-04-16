#!/bin/sh
# Gradle wrapper stub — GitHub Actions uses its own Gradle
# For local builds, install Gradle 8.6+ and run: gradle assembleDebug

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

if command -v gradle &> /dev/null; then
    gradle "$@"
elif [ -f "$GRADLE_HOME/bin/gradle" ]; then
    "$GRADLE_HOME/bin/gradle" "$@"
else
    echo "ERROR: Gradle not found."
    echo "Install Android Studio or Gradle 8.6+ from https://gradle.org/releases/"
    echo "Or push to GitHub and let GitHub Actions build the APK automatically."
    exit 1
fi
