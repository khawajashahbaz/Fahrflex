package de.hnu.domain;

import jakarta.persistence.*;
import de.hnu.domain.enums.Coverage;

@Entity
@Table(name = "insurance")
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String policyName;
    private String policyNumber;
    
    @Enumerated(EnumType.STRING)
    private Coverage coverage;
    
    private Boolean passengerDriverInsurance;

    public Insurance() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public Coverage getCoverage() {
        return coverage;
    }

    public void setCoverage(Coverage coverage) {
        this.coverage = coverage;
    }

    public Boolean getPassengerDriverInsurance() {
        return passengerDriverInsurance;
    }

    public void setPassengerDriverInsurance(Boolean passengerDriverInsurance) {
        this.passengerDriverInsurance = passengerDriverInsurance;
    }
}