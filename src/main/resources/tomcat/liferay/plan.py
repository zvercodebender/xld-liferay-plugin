#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

def getContainerListFromDeployedApplication():
   print "START: getContainerListFromDeployedApplication"
   containers = {}
   try:
      env = deployedApplication.getEnvironment()
   except:
      env = previousDeployedApplication.getEnvironment()
   print "ENV = %s" % env.name
   members = env.getMembers()
   for container in members:
      containers[container.host] = container
   # End for
   print "END  : getContainerListFromDeployedApplication"
   return [containers[ke] for ke in containers.keys()]

def create_start_stop( containers, context ):
   print "START: create start steps"
   print containers
   for container in containers:
      context.addStep( steps.os_script(
         description = "Stop %s Server" % ( container.name ),
         order = 30,
         target_host = container.host,
         script = "tomcat/liferay/stop-tc",
         freemarker_context = { "containerHome":          container.server.home,
                                "containerEnvVars":       container.server.envVars,
                                "containerStatusCommand": container.server.statusCommand,
                                "containerStartCommand":  container.server.startCommand,
                                "containerStopCommand":   container.server.stopCommand }
      ))
      context.addStep( steps.wait(
         description = "Wait for application %s to be installed" % ( container.name ),
         order = 75,
         seconds = 30
      ))
      context.addStep( steps.os_script(
         description = "Start %s Server" % ( container.name ),
         order = 70,
         target_host = container.host,
         script = "tomcat/liferay/start-tc",
         freemarker_context = { "containerHome":          container.server.home,
                                "containerEnvVars":       container.server.envVars,
                                "containerStatusCommand": container.server.statusCommand,
                                "containerStartCommand":  container.server.startCommand,
                                "containerStopCommand":   container.server.stopCommand }
      ))
   # End for
# End def

for delta in deltas.deltas:
   if (delta.deployedOrPrevious.type == "liferay.TomcatWarModule" ):
      create_start_stop( getContainerListFromDeployedApplication(), context )
      break
   # End if
# End for
