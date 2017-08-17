#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

def getContainerListFromDeployedApplication():
   containers = {}
   try:
      env = deployedApplication.getEnvironment()
   except:
      env = previousDeployedApplication.getEnvironment()
   members = env.getMembers()
   for container in members:
      containers[container.host] = container
   # End for
   return [containers[ke] for ke in containers.keys()]

def create_start_stop( containers, context ):
   for container in containers:
      obscure_start_stop_step_creation(
         container = container,
         description = "Stop %s Server" % ( container.name ),
         order = 30,
         script = "tomcat/liferay/stop-tc",
      )
      context.addStep( steps.wait(
         description = "Wait for application %s to be installed" % ( container.name ),
         order = 75,
         seconds = 30
      ))
      obscure_start_stop_step_creation(
         container = container,
         description = "Start %s Server" % ( container.name ),
         order = 70,
         script = "tomcat/liferay/start-tc",
      )
   # End for
# End def

def obscure_start_stop_step_creation( container, description, order, script ):
   contextStartStop  =  { "containerHome":          container.server.home,
                          "containerEnvVars":       container.server.envVars,
                          "containerStatusCommand": container.server.statusCommand,
                          "containerStartCommand":  container.server.startCommand,
                          "containerStopCommand":   container.server.stopCommand }
   context.addStep( steps.os_script(
      description = description,
      order = order,
      target_host = container.host,
      script = script,
      freemarker_context = contextStartStop
   ))

for delta in deltas.deltas:
   if (delta.deployedOrPrevious.type == "liferay.TomcatWarModule" ):
      create_start_stop( getContainerListFromDeployedApplication(), context )
      break
   # End if
# End for
