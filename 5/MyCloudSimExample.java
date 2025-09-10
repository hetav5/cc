package mycloudsim;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;

import java.util.*;

public class MyCloudSimExample {

    public static void main(String[] args) {
        try {
            System.out.println("Starting CloudSim Example...");

            // Step 1: Initialize CloudSim
            int numUsers = 1;   // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;  // trace events
            CloudSim.init(numUsers, calendar, traceFlag);

            // Step 2: Create Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            // Step 3: Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Step 4: Create VMs
            List<Vm> vmList = new ArrayList<>();
            int mips = 1000;
            int ram = 512; // MB
            long bw = 1000;
            long size = 10000; // image size (MB)
            int pesNumber = 1; // number of CPUs

            // Create 2 VMs
            Vm vm1 = new Vm(0, brokerId, mips, pesNumber, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
            Vm vm2 = new Vm(1, brokerId, mips, pesNumber, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());

            vmList.add(vm1);
            vmList.add(vm2);

            broker.submitVmList(vmList);

            // Step 5: Create Cloudlets
            List<Cloudlet> cloudletList = new ArrayList<>();
            int idShift = 0;
            long length = 40000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            for (int i = 0; i < 5; i++) {
                Cloudlet cloudlet = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize,
                        utilizationModel, utilizationModel, utilizationModel);
                cloudlet.setUserId(brokerId);
                cloudletList.add(cloudlet);
            }

            broker.submitCloudletList(cloudletList);

            // Step 6: Start Simulation
            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            // Step 7: Print results
            printCloudletList(newList);

            System.out.println("Simulation finished!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unexpected error");
        }
    }

    private static Datacenter createDatacenter(String name) {
        // Host List
        List<Host> hostList = new ArrayList<>();

        // A Machine contains one or more PEs or CPUs/Cores.
        List<Pe> peList = new ArrayList<>();
        int mips = 1000;
        peList.add(new Pe(0, new PeProvisionerSimple(mips))); // First PE
        peList.add(new Pe(1, new PeProvisionerSimple(mips))); // Second PE

        int hostId = 0;
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        int bw = 10000;

        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram),
                        new BwProvisionerSimple(bw),
                        storage,
                        peList,
                        new VmSchedulerTimeShared(peList)
                )
        );

        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, 10.0, 3.0, 0.05, 0.001, 0.0);

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
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        String indent = "    ";
        System.out.println("========== CLOUDLET EXECUTION RESULTS ==========");
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