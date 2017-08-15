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
<#include "/tomcat/liferay/status-tc.sh.ftl">
cd "${containerHome}"
if [ "$status" != "STARTED" ] ; then
${containerStartCommand}
echo 'Server successfully started'
else
echo "Server is already $status, nothing to do"
fi