<#assign envVars=containerEnvVars />
<#list envVars?keys as envVar>
${envVar}="${envVars[envVar]}"
export ${envVar}
</#list>
<#include "/tomcat/liferay/status-tc.sh.ftl">
cd "${containerHome}"
if [ "$status" != "STOPPED" ] ; then
${containerStopCommand}
echo 'Server successfully stopped'
else
echo "Server is already $status, nothing to do"
fi