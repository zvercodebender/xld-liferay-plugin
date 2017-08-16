<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->

cd "${containerHome}"

echo "Remove ${deployedName}"
rm -rf "webapps/${deployedName}"

echo "Remove temp files"
rm -rf temp/*
find temp

echo "Remove work files"
rm -rf work/*
find work

