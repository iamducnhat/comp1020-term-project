#!/bin/bash
# ============================================================
# VinTony POS — Build & Run Script
# ============================================================
SRC=src
OUT=out
JAVAC_BIN="${JAVAC:-javac}"
JAVA_BIN="${JAVA:-java}"

if [ -n "$JAVA_HOME" ]; then
    JAVAC_BIN="$JAVA_HOME/bin/javac"
    JAVA_BIN="$JAVA_HOME/bin/java"
fi

echo "==> Compiling..."
rm -rf "$OUT"
find "$SRC" -name "*.java" -print0 | xargs -0 "$JAVAC_BIN" -encoding UTF-8 -sourcepath "$SRC" -d "$OUT"
if [ $? -eq 0 ]; then
    echo "==> Build SUCCESS"
    if [ "${RUN_APP:-1}" = "0" ]; then
        exit 0
    fi
    echo "==> Running..."
    "$JAVA_BIN" -cp "$OUT" Main
else
    echo "==> Build FAILED"
fi
