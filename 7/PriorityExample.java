package mycloudsim;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;

import java.util.*;

public class PriorityExample {

    public static void main(String[] args) {
        try {
            System.out.println("Starting CloudSim Priority Scheduling Example...");

            // Step 1: Initialize CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            CloudSim.init(numUsers, calendar, traceFlag);

            // Step 2: Create Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_Priority");

            // Step 3: Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Step 4: Create VMs
            List<Vm> vmList = new ArrayList<>();
            Vm vm1 = new Vm(0, brokerId, 1000, 1, 512, 1000, 10000,
                    "Xen", new CloudletSchedulerSpaceShared());
            vmList.add(vm1);
            broker.submitVmList(vmList);

            // Step 5: Create Cloudlets with priority
            List<Cloudlet> cloudletList = new ArrayList<>();
            long length = 40000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            // Assign priority (lower = higher priority)
            int[] priorities = {3, 1, 4, 2, 5};

            for (int i = 0; i < 5; i++) {
                Cloudlet cloudlet = new Cloudlet(i, length, 1, fileSize, outputSize,
                        utilizationModel, utilizationModel, utilizationModel);
                cloudlet.setUserId(brokerId);
                cloudlet.setSubmissionTime(priorities[i]); // store priority in submission time for ordering
                cloudletList.add(cloudlet);
            }

            // Sort cloudlets by priority before submitting
            cloudletList.sort(Comparator.comparingDouble(Cloudlet::getSubmissionTime));

            broker.submitCloudletList(cloudletList);

            // Step 6: Start Simulation
            CloudSim.startSimulation();
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            // Step 7: Print Results
            printCloudletList(newList);

            System.out.println("Priority Scheduling Simulation finished!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000)));

        hostList.add(new Host(0, new RamProvisionerSimple(2048),
                new BwProvisionerSimple(10000), 1000000, peList,
                new VmSchedulerSpaceShared(peList)));

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0);

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics,
                    new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker_Priority");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        String indent = "    ";
        System.out.println("========== PRIORITY CLOUDLET EXECUTION RESULTS ==========");
        System.out.println("ID" + indent + "STATUS" + indent +
                "DataCenter" + indent + "VM" +
                indent + "Time" + indent + "Start" + indent + "Finish");

        for (Cloudlet cloudlet : list) {
            System.out.print(cloudlet.getCloudletId() + indent);
            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                System.out.print("SUCCESS");
                System.out.println(indent + cloudlet.getResourceId() + indent + cloudlet.getVmId() +
                        indent + cloudlet.getActualCPUTime() + indent +
                        cloudlet.getExecStartTime() + indent + cloudlet.getFinishTime());
            }
        }
    }
}