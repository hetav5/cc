package mycloudsim;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;

import java.util.*;

public class FCFSExample {

    public static void main(String[] args) {
        try {
            System.out.println("Starting CloudSim FCFS Example...");

            // Step 1: Initialize CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            CloudSim.init(numUsers, calendar, traceFlag);

            // Step 2: Create Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_FCFS");

            // Step 3: Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Step 4: Create VMs
            List<Vm> vmList = new ArrayList<>();
            int mips = 1000;
            int ram = 512;
            long bw = 1000;
            long size = 10000;
            int pesNumber = 1;

            Vm vm1 = new Vm(0, brokerId, mips, pesNumber, ram, bw, size, "Xen", new CloudletSchedulerSpaceShared()); // FCFS
            vmList.add(vm1);

            broker.submitVmList(vmList);

            // Step 5: Create Cloudlets
            List<Cloudlet> cloudletList = new ArrayList<>();
            long length = 40000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            for (int i = 0; i < 5; i++) {
                Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize,
                        utilizationModel, utilizationModel, utilizationModel);
                cloudlet.setUserId(brokerId);
                cloudletList.add(cloudlet);
            }

            broker.submitCloudletList(cloudletList);

            // Step 6: Start Simulation
            CloudSim.startSimulation();
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            // Step 7: Print Results
            printCloudletList(newList);

            System.out.println("FCFS Simulation finished!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        int mips = 1000;
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        int ram = 2048;
        long storage = 1000000;
        int bw = 10000;

        hostList.add(new Host(0, new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw), storage, peList,
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
            broker = new DatacenterBroker("Broker_FCFS");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        String indent = "    ";
        System.out.println("========== FCFS CLOUDLET EXECUTION RESULTS ==========");
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