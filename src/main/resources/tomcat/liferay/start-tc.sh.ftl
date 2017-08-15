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