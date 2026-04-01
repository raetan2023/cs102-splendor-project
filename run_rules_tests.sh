#!/bin/bash
# Script to compile and run RulesTestCases

# Move to the directory where this script is located
cd "$(dirname "$0")"

echo "====================================="
echo " Rules Test Cases Runner"
echo "====================================="
echo ""

# Compile the project
echo "Compiling Splendor project..."
./compile.sh

if [ $? -ne 0 ]; then
    echo ""
    echo "Compilation failed. Exiting."
    exit 1
fi

echo ""
echo "====================================="
echo " Running Rules Tests"
echo "====================================="
echo ""

# Run the RulesTestCases
java -cp classes com.splendor.RulesTestCases
