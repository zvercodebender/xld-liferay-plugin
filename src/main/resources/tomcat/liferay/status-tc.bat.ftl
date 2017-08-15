@echo off
<#assign envVars=containerEnvVars />
<#list envVars?keys as envVar>
set ${envVar}="${envVars[envVar]}"
</#list>
set status=UNKNOWN
<#if containerStatusCommand??>
cd /D "${containerHome}"
echo Checking server status...
${containerStatusCommand}
set RES=%ERRORLEVEL%
if not %RES% == 0 (
  set status=STOPPED
) else (
  set status=STARTED
)
echo Server is currently %status%
</#if>