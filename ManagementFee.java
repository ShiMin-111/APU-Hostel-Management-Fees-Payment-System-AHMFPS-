package ahmfps;

import java.time.LocalDate;

public class ManagementFee {
    private double managementFee;
    private LocalDate changeDate;

    public ManagementFee(double managementFee, LocalDate changeDate) {
        this.managementFee = managementFee;
        this.changeDate = changeDate;
    }

    public double getManagementFee() {
        return managementFee;
    }

    public LocalDate getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
    }
}
