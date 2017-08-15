<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->

<#assign envVars=containerEnvVars />
<#list envVars?keys as envVar>
${envVar}="${envVars[envVar]}"
export ${envVar}
</#list>
status=UNKNOWN
<#if containerStatusCommand??>
cd "${containerHome}"
echo 'Checking server status...'
${containerStatusCommand}
res=$?
if [ $res != 0 ] ; then
status=STOPPED
else
status=STARTED
fi
echo "Server is currently $status"
</#if>