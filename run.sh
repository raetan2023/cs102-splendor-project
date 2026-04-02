# Generated using Gemini 3.1 Pro on Google Antigravity
#!/bin/bash
# Move to the directory where this script is located
cd "$(dirname "$0")"

# Remove carriage returns from the argument if present
MAIN_CLASS=$(echo "${1:-com.splendor.Main}" | tr -d '\r')

echo "Starting Splendor with: $MAIN_CLASS"
java -cp classes "$MAIN_CLASS"
