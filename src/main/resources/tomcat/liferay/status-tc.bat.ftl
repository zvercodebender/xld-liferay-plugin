<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->

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