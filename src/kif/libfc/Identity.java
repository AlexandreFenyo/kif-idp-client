// A. Fenyo - 2016

package kif.libfc;

public class Identity {
    private String nonce;
    private String state;
    private String sub;
    private String gender;
    private String birthdate;
    private String birthcountry;
    private String birthplace;
    private String given_name;
    private String family_name;
    private String email;
    private Address address;

    public class Address {
        private String formatted;
        private String street_address;
        private String locality;
        private String region;
        private String postal_code;
        private String country;
        
        public String getFormatted() {
            return formatted;
        }
        
        public void setFormatted(final String formatted) {
            this.formatted = formatted;
        }

        public String getStreet_address() {
            return street_address;
        }
        
        public void setStreet_address(final String street_address) {
            this.street_address = street_address;
        }

        public String getLocality() {
            return locality;
        }
        
        public void setLocality(final String locality) {
            this.locality = locality;
        }

        public String getRegion() {
            return region;
        }
        
        public void setRegion(final String region) {
            this.region = region;
        }

        public String getPostal_code() {
            return postal_code;
        }
        
        public void setPostal_code(final String postal_code) {
            this.postal_code = postal_code;
        }

        public String getCountry() {
            return country;
        }
        
        public void setCountry(final String country) {
            this.country = country;
        }
    }
    
    public String getNonce() {
        return nonce;
    }
    public void setNonce(final String nonce) {
        this.nonce = nonce;
    }

    public String getState() {
        return state;
    }
    public void setState(final String state) {
        this.state = state;
    }

    public String getSub() {
        return sub;
    }
    public void setSub(final String sub) {
        this.sub = sub;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(final String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBirthcountry() {
        return birthcountry;
    }

    public void setBirthcountry(final String birthcountry) {
        this.birthcountry = birthcountry;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(final String birthplace) {
        this.birthplace = birthplace;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(final String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(final String family_name) {
        this.family_name = family_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }
}
