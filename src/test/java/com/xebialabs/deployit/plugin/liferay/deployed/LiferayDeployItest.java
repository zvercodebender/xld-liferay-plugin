package com.xebialabs.deployit.plugin.liferay.deployed;

import java.io.IOException;
import java.util.List;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.google.common.collect.Sets;

import com.xebialabs.deployit.deployment.planner.DeltaSpecificationBuilder;
import com.xebialabs.deployit.plugin.api.boot.PluginBooter;
import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentStep;
import com.xebialabs.deployit.plugin.api.deployment.execution.Plan;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.execution.Step;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.jee.artifact.War;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.deployit.plugin.wls.container.*;
import com.xebialabs.deployit.test.deployment.DeployitTester;
import com.xebialabs.deployit.test.support.LoggingDeploymentExecutionContext;

import static com.google.common.collect.Lists.newArrayList;
import static com.xebialabs.deployit.test.support.TestUtils.createArtifact;
import static com.xebialabs.deployit.test.support.TestUtils.createDeployedApplication;
import static com.xebialabs.deployit.test.support.TestUtils.createDeploymentPackage;
import static com.xebialabs.deployit.test.support.TestUtils.createEnvironment;
import static com.xebialabs.deployit.test.support.TestUtils.newInstance;
import static com.xebialabs.overthere.ConnectionOptions.ADDRESS;
import static com.xebialabs.overthere.ConnectionOptions.PASSWORD;
import static com.xebialabs.overthere.ConnectionOptions.USERNAME;
import static com.xebialabs.overthere.OperatingSystemFamily.UNIX;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.CONNECTION_TYPE;
import static com.xebialabs.overthere.ssh.SshConnectionType.SFTP;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class LiferayDeployItest {

	protected static LoggingDeploymentExecutionContext context;
	protected boolean performExecution = true;

	private War war, updatedWar;
	private Deployed deployedWar, updatedDeployedWar;
	private DeployedApplication deployedApplication, updatedDeployedApplication;

	private WlsContainer target;

	public static Host wls10gUnixHost;
	public static Domain wls10gUnixDomain;
	public static Cluster wls10gUnixCluster1;
	public static Server wls10gUnixServer1;

	public LiferayDeployItest(WlsContainer target) {
		this.target = target;
	}

	@Before
	public void setup() throws IOException {
		war = (War) createArtifact("PetClinic", "1.0", "artifacts/PetClinic-1.0.ear", "liferay.War", folder.newFolder());
		deployedWar = tester.generateDeployed(war, target, Type.valueOf("liferay.WarModule"));
		deployedApplication = createDeployedApplication(createDeploymentPackage(war), createEnvironment(target));

		updatedWar = (War) createArtifact("PetClinic", "2.0", "artifacts/PetClinic-2.0.ear", "liferay.War", folder.newFolder());
		updatedDeployedWar = tester.generateDeployed(updatedWar, target, Type.valueOf("liferay.WarModule"));
		updatedDeployedApplication = createDeployedApplication(createDeploymentPackage(updatedWar), createEnvironment(target));
	}

	@Test
	public void deployUpgradeUndeployWar() throws Exception {

		//Initial
		{
			DeltaSpecification spec = new DeltaSpecificationBuilder()
					.initial(deployedApplication)
					.create(deployedWar)
					.build();
			Plan resolvedPlan = tester.resolvePlan(spec);
			List<DeploymentStep> resolvedSteps = resolvedPlan.getSteps();
			System.out.println("resolvedSteps = " + resolvedSteps);
			assertThat(7, is(resolvedSteps.size()));
			executePlan(resolvedPlan);
		}

		//Upgrade
		{
			DeltaSpecification spec = new DeltaSpecificationBuilder()
					.upgrade(deployedApplication)
					.modify(deployedWar, updatedDeployedWar)
					.build();
			Plan resolvedPlan = tester.resolvePlan(spec);
			List<DeploymentStep> resolvedSteps = resolvedPlan.getSteps();
			System.out.println("resolvedSteps = " + resolvedSteps);
			assertThat(9, is(resolvedSteps.size()));
			executePlan(resolvedPlan);
		}

		//Undeploy
		{
			DeltaSpecification spec = new DeltaSpecificationBuilder()
					.undeploy(deployedApplication)
					.destroy(deployedWar)
					.build();
			Plan resolvedPlan = tester.resolvePlan(spec);
			List<DeploymentStep> resolvedSteps = resolvedPlan.getSteps();
			System.out.println("resolvedSteps = " + resolvedSteps);
			assertThat(2, is(resolvedSteps.size()));
			executePlan(resolvedPlan);
		}
	}

	protected void executePlan(Plan resolvedPlan) {
		if (performExecution) {
			Step.Result result = tester.executePlan(resolvedPlan, context);
			assertThat(result, is(Step.Result.Success));
		}
	}


	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	protected static DeployitTester tester;

	@BeforeClass
	public static void boot() {
		tester = DeployitTester.build();
	}

	@BeforeClass
	public static void createContext() {
		context = new LoggingDeploymentExecutionContext(LiferayDeployItest.class);
	}

	@AfterClass
	public static void destroyContext() {
		if (context != null) {
			context.destroy();
		}
	}

	public boolean isPerformExecution() {
		return performExecution;
	}

	public void setPerformExecution(boolean performExecution) {
		this.performExecution = performExecution;
	}

	@Parameterized.Parameters
	public static List<Object[]> getTargetDomains() {
		return newArrayList(new Object[]{wls10gUnixCluster1}, new Object[]{wls10gUnixServer1});
	}

	static {
		PluginBooter.bootWithoutGlobalContext();
		initialize10gUnixTopology();
	}

	private static void initialize10gUnixTopology() {
		wls10gUnixHost = newInstance("overthere.SshHost");
		wls10gUnixHost.setId("Infrastructure/wls-103");
		wls10gUnixHost.setOs(UNIX);
		wls10gUnixHost.setProperty(CONNECTION_TYPE, SFTP);
		wls10gUnixHost.setProperty(ADDRESS, "wls-103");
		wls10gUnixHost.setProperty(USERNAME, "ubuntu");
		wls10gUnixHost.setProperty(PASSWORD, "ubuntu");

		wls10gUnixDomain = newInstance(Domain.class);
		wls10gUnixDomain.setId("Infrastructure/wls-103/adDomain");
		wls10gUnixDomain.setUsername("weblogic");
		wls10gUnixDomain.setPassword("weblogic10");
		wls10gUnixDomain.setWlHome("/opt/bea-10.3/wlserver_10.3");
		wls10gUnixDomain.setDomainHome("/opt/bea-10.3/user_projects/domains/adDomain");
		wls10gUnixDomain.setHost(wls10gUnixHost);
		wls10gUnixDomain.setStartMode(StartMode.Script);

		wls10gUnixServer1 = newInstance(Server.class);
		wls10gUnixServer1.setId(wls10gUnixDomain.getId() + "/wlserver-1");
		wls10gUnixServer1.setPort(7009);
		wls10gUnixServer1.setHost(wls10gUnixHost);
		wls10gUnixServer1.setDomain(wls10gUnixDomain);
		wls10gUnixServer1.setStartCommand("nohup /opt/bea-10.3/user_projects/domains/adDomain/bin/startManagedWebLogic.sh wlserver-1 &");
		wls10gUnixServer1.setProperty("inputDirectory", "/tmp/in");
		wls10gUnixServer1.setProperty("outputDirectory", "/tmp/out");


		wls10gUnixCluster1 = newInstance(Cluster.class);
		wls10gUnixCluster1.setId(wls10gUnixDomain.getId() + "/Cluster-1");
		wls10gUnixCluster1.setDomain(wls10gUnixDomain);
		wls10gUnixCluster1.setServers(Sets.newHashSet(wls10gUnixServer1));
		wls10gUnixCluster1.setProperty("inputDirectory", "/tmp/in");
		wls10gUnixCluster1.setProperty("outputDirectory", "/tmp/out");
	}


}
