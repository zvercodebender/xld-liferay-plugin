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
<#include "/tomcat/liferay/status-tc.bat.ftl">
cd /D "${containerHome}"
if not "%status%" == "STARTED" (
  ${containerStartCommand}
  echo Server successfully started
) else (
  echo Server is already %status%, nothing to do
)