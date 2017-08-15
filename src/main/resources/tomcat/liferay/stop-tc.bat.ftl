@echo off
<#assign envVars=containerEnvVars />
<#list envVars?keys as envVar>
set ${envVar}="${envVars[envVar]}"
</#list>
<#include "/tomcat/liferay/status-tc.bat.ftl">
cd /D "${containerHome}"
if not "%status%" == "STOPPED" (
  ${containerStopCommand}
  echo Server successfully stopped
) else (
  echo Server is already %status%, nothing to do
)