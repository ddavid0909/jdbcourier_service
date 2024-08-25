package student;

import rs.etf.sab.operations.*;

import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        CityOperations cityOperations = new dd210102_CityOperations(); // Change this to your implementation.
        DistrictOperations districtOperations = new dd210102_DistrictOperations(); // Do it for all classes.
        CourierOperations courierOperations = new dd210102_CourierOperations(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new dd210102_CourierRequestOperation();
        GeneralOperations generalOperations = new dd210102_GeneralOperations();
        UserOperations userOperations = new dd210102_UserOperations();
        VehicleOperations vehicleOperations = new dd210102_VehicleOperations();
        PackageOperations packageOperations = new dd210102_PackageOperations();

        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
    }
}
