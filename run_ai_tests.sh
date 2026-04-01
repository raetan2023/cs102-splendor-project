#!/bin/bash
# Script to compile and run AIPlayerTestCases

# Move to the directory where this script is located
cd "$(dirname "$0")"

echo "====================================="
echo " AI Player Test Cases Runner"
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
echo " Running AI Player Tests"
echo "====================================="
echo ""

# Run the AIPlayerTestCases
java -cp classes com.splendor.AIPlayerTestCases
