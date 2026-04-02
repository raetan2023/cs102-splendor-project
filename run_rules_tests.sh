# Move to the directory where this script is located
cd "$(dirname "$0")"

echo "Running RulesTestCases..."
java -cp classes com.splendor.RulesTestCases


echo ""
echo "====================================="
echo " Running Rules Tests"
echo "====================================="
echo ""

# Run the RulesTestCases
java -cp classes com.splendor.RulesTestCases
