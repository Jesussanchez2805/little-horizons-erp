package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Domain entity representing an enrolled student at Little Horizons School.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class Student {

    private int id;
    private String documentType;
    private String documentNumber;
    private String firstName;
    private String secondName;
    private String firstLastName;
    private String secondLastName;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String phone;
    private String email;
    private String bloodType;
    private String allergies;
    private String medicalConditions;
    private LocalDate entryDate;
    private String status; // 'Activo', 'Graduado', 'Retirado', 'Suspendido'

    public Student() {
    }

    public Student(int id, String documentType, String documentNumber, String firstName, String secondName,
                   String firstLastName, String secondLastName, LocalDate birthDate, String gender,
                   String address, String phone, String email, String bloodType, String allergies,
                   String medicalConditions, LocalDate entryDate, String status) {
        this.id = id;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.firstName = firstName;
        this.secondName = secondName;
        this.firstLastName = firstLastName;
        this.secondLastName = secondLastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.medicalConditions = medicalConditions;
        this.entryDate = entryDate;
        this.status = status;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder(firstName);
        if (secondName != null && !secondName.isBlank()) sb.append(" ").append(secondName);
        sb.append(" ").append(firstLastName);
        if (secondLastName != null && !secondLastName.isBlank()) sb.append(" ").append(secondLastName);
        return sb.toString();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSecondName() { return secondName; }
    public void setSecondName(String secondName) { this.secondName = secondName; }

    public String getFirstLastName() { return firstLastName; }
    public void setFirstLastName(String firstLastName) { this.firstLastName = firstLastName; }

    public String getSecondLastName() { return secondLastName; }
    public void setSecondLastName(String secondLastName) { this.secondLastName = secondLastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * @return age in complete years on the given date
     */
    public int getAge(LocalDate onDate) {
        return java.time.Period.between(birthDate, onDate).getYears();
    }

    /**
     * @return current age in complete years
     */
    public int getAge() {
        return getAge(LocalDate.now());
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder pattern: replaces the 17-argument constructor with readable, step-by-step construction.
     * Document type, document number, first name, first last name and birth date are mandatory.
     * Status defaults to 'Activo' and entry date to today.
     */
    public static class Builder {
        private int id;
        private String documentType;
        private String documentNumber;
        private String firstName;
        private String secondName;
        private String firstLastName;
        private String secondLastName;
        private LocalDate birthDate;
        private String gender;
        private String address;
        private String phone;
        private String email;
        private String bloodType;
        private String allergies;
        private String medicalConditions;
        private LocalDate entryDate = LocalDate.now();
        private String status = "Activo";

        public Builder id(int id) { this.id = id; return this; }
        public Builder documentType(String documentType) { this.documentType = documentType; return this; }
        public Builder documentNumber(String documentNumber) { this.documentNumber = documentNumber; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder secondName(String secondName) { this.secondName = secondName; return this; }
        public Builder firstLastName(String firstLastName) { this.firstLastName = firstLastName; return this; }
        public Builder secondLastName(String secondLastName) { this.secondLastName = secondLastName; return this; }
        public Builder birthDate(LocalDate birthDate) { this.birthDate = birthDate; return this; }
        public Builder gender(String gender) { this.gender = gender; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder bloodType(String bloodType) { this.bloodType = bloodType; return this; }
        public Builder allergies(String allergies) { this.allergies = allergies; return this; }
        public Builder medicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; return this; }
        public Builder entryDate(LocalDate entryDate) { this.entryDate = entryDate; return this; }
        public Builder status(String status) { this.status = status; return this; }

        /**
         * @throws IllegalStateException listing every mandatory field that is missing
         */
        public Student build() {
            java.util.List<String> missing = new java.util.ArrayList<>();
            if (isBlank(documentType)) missing.add("documentType");
            if (isBlank(documentNumber)) missing.add("documentNumber");
            if (isBlank(firstName)) missing.add("firstName");
            if (isBlank(firstLastName)) missing.add("firstLastName");
            if (birthDate == null) missing.add("birthDate");
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing mandatory student fields: " + String.join(", ", missing));
            }
            return new Student(id, documentType, documentNumber, firstName, secondName, firstLastName,
                    secondLastName, birthDate, gender, address, phone, email, bloodType, allergies,
                    medicalConditions, entryDate, status);
        }

        private static boolean isBlank(String value) {
            return value == null || value.isBlank();
        }
    }

    @Override
    public String toString() {
        return String.format("[%s %s] %s (%s)", documentType, documentNumber, getFullName(), status);
    }
}
