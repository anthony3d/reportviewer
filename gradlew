#!/bin/sh

JAVA_CMD="java"
if [ -n "$JAVA_HOME" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
fi

# Упрощенная версия без сложных парсеров
CLASSPATH="gradle/wrapper/gradle-wrapper.jar"

exec "$JAVA_CMD" -Xmx64m -Xms64m -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
