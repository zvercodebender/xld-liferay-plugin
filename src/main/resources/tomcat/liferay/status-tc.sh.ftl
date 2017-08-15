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