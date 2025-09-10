package mycloudsim;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;


import java.util.*;

/**
 * Round Robin Load Balancing Example in CloudSim
 */
public class RoundRobinLoadBalancer {

    public static void main(String[] args) {
        try {
            // Step 1: Initialize CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            CloudSim.init(numUsers, calendar, traceFlag);

            System.out.println("Starting CloudSim Round Robin Example...");

            // Step 2: Create Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            // Step 3: Create Broker
            RoundRobinBroker broker = new RoundRobinBroker("RoundRobinBroker");
            int brokerId = broker.getId();

            // Step 4: Create VMs
            List<Vm> vmlist = new ArrayList<>();

            int vmid = 0;
            int mips = 250;
            long size = 10000; // image size (MB)
            int ram = 512;     // vm memory (MB)
            long bw = 1000;
            int pesNumber = 1; // number of CPUs per VM
            String vmm = "Xen";

            for (int i = 0; i < 3; i++) { // 3 VMs
                Vm vm = new Vm(vmid + i, brokerId, mips, pesNumber, ram, bw, size,
                        vmm, new CloudletSchedulerTimeShared());
                vmlist.add(vm);
            }

            broker.submitVmList(vmlist);

            // Step 5: Create Cloudlets
            List<Cloudlet> cloudletList = new ArrayList<>();

            int idShift = 0;
            int cloudletLength = 40000;
            int cloudletFileSize = 300;
            int cloudletOutputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            for (int i = 0; i < 9; i++) { // 9 Cloudlets
                Cloudlet cloudlet = new Cloudlet(idShift + i, cloudletLength, pesNumber,
                        cloudletFileSize, cloudletOutputSize,
                        utilizationModel, utilizationModel, utilizationModel);
                cloudlet.setUserId(brokerId);
                cloudletList.add(cloudlet);
            }

            broker.submitCloudletList(cloudletList);

            // Step 6: Start simulation
            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            // Step 7: Print results
            printCloudletList(newList);

            System.out.println("Round Robin Simulation finished!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Simulation has failed.");
        }
    }

    // --------- Create Datacenter ----------
    private static Datacenter createDatacenter(String name) throws Exception {
        List<Host> hostList = new ArrayList<>();

        // Host specs
        List<Pe> peList = new ArrayList<>();
        int mips = 1000;

        // Create 4 CPU cores
        for (int i = 0; i < 4; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(mips)));
        }

        int hostId = 0;
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        int bw = 10000;

        hostList.add(new Host(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
        ));

        String arch = "x86";      // system architecture
        String os = "Linux";      // operating system
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        LinkedList<Storage> storageList = new LinkedList<>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
    }

    // --------- Print Cloudlet Results ----------
    private static void printCloudletList(List<Cloudlet> list) {
        String indent = "    ";
        System.out.println("========== OUTPUT ==========");
        System.out.println("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent +
                "Time" + indent + "Start Time" + indent + "Finish Time");

        for (Cloudlet cloudlet : list) {
            System.out.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                System.out.println("SUCCESS" + indent + indent +
                        cloudlet.getResourceId() + indent + indent + cloudlet.getVmId() +
                        indent + cloudlet.getActualCPUTime() + indent +
                        cloudlet.getExecStartTime() + indent + cloudlet.getFinishTime());
            }
        }
    }
}

// --------- Custom Round Robin Broker ----------
class RoundRobinBroker extends DatacenterBroker {
    private int lastVmIndex;

    public RoundRobinBroker(String name) throws Exception {
        super(name);
        this.lastVmIndex = -1;
    }

    @Override
    protected void submitCloudlets() {
        int vmListSize = getVmList().size();

        for (Cloudlet cloudlet : getCloudletList()) {
            lastVmIndex = (lastVmIndex + 1) % vmListSize;
            Vm vm = getVmList().get(lastVmIndex);

            // Skip if VM wasn't created
            if (getVmsToDatacentersMap().get(vm.getId()) == null) {
                continue;
            }

            cloudlet.setVmId(vm.getId());

            // Cloudlet submit event
            final int CLOUDLET_SUBMIT = 21;
            sendNow(getVmsToDatacentersMap().get(vm.getId()), CLOUDLET_SUBMIT, cloudlet);
        }

        getCloudletList().clear();
    }
}