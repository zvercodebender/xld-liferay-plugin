<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->

#!/bin/sh

while [ -f "${deployed.container.deployDirectory}/${deployed.file.name}" ]
do
echo "File is still not picked up by Liferay. Retrying in 5 seconds..."
sleep 5
done

echo "File is picked up by Liferay."