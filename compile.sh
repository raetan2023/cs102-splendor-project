# Generated using Gemini 3.1 Pro on Google Antigravity
#!/bin/bash

echo "Creating classes directory..."
mkdir -p classes

echo "Compiling Java files..."
# Find all .java files in the src directory
find src -name "*.java" > sources.txt

# Compile the found files
javac --release 11 -d classes @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed. Please check the errors above."
fi

# Clean up the temporary file
rm -f sources.txt
