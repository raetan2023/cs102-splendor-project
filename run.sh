#!/bin/bash

# Default main class
MAIN_CLASS=${1:-"com.splendor.Main"}

echo "Starting Splendor..."
java -cp classes "$MAIN_CLASS"
