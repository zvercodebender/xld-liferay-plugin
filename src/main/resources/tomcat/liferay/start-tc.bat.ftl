@echo off
<#assign envVars=containerEnvVars />
<#list envVars?keys as envVar>
set ${envVar}="${envVars[envVar]}"
</#list>
<#include "/tomcat/liferay/status-tc.bat.ftl">
cd /D "${containerHome}"
if not "%status%" == "STARTED" (
  ${containerStartCommand}
  echo Server successfully started
) else (
  echo Server is already %status%, nothing to do
)