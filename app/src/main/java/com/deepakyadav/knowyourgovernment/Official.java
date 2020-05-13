package com.deepakyadav.knowyourgovernment;

import java.io.Serializable;

public class Official implements Serializable {

    // Official personal info
    String office;
    String officialName;
    String officialParty;
    String officialAddress;
    String officialPhone;
    String officialEmail;
    String officialPhotoURL;

    // Official social media
    String officialWebURL;
    String officialYouTube;
    String officialFB;
    String officialTwitter;
    String officialGPlus;

    // Getters
    public String getOffice() {
        return office;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getOfficialParty() {
        return officialParty;
    }

    public String getOfficialAddress() {
        return officialAddress;
    }

    public String getOfficialPhone() {
        return officialPhone;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public String getOfficialPhotoURL() {
        return officialPhotoURL;
    }

    public String getOfficialWebURL() {
        return officialWebURL;
    }

    public String getOfficialYouTube() {
        return officialYouTube;
    }

    public String getOfficialFB() {
        return officialFB;
    }

    public String getOfficialTwitter() {
        return officialTwitter;
    }

    public String getOfficialGPlus() {
        return officialGPlus;
    }

    // Setters
    public void setOffice(String office) {
        this.office = office;
    }

    public void setOfficialName(String name) {
        this.officialName = name;
    }

    public void setOfficialParty(String party) {
        this.officialParty = party;
    }

    public void setOfficialAddress(String address) {
        this.officialAddress = address;
    }

    public void setOfficialPhone(String phone) {
        this.officialPhone = phone;
    }

    public void setOfficialEmail(String email) {
        this.officialEmail = email;
    }

    public void setOfficialPhotoURL(String photoURL) {
        this.officialPhotoURL = photoURL;
    }

    public void setOfficialWebURL(String webURL) {
        this.officialWebURL = webURL;
    }

    public void setOfficialYouTube(String youTubeURL) {
        this.officialYouTube = youTubeURL;
    }

    public void setOfficialFB(String fbURL) {
        this.officialFB = fbURL;
    }

    public void setOfficialTwitter(String twitterURL) {
        this.officialTwitter = twitterURL;
    }

    public void setOfficialGPlus(String gPlusURL) {
        this.officialGPlus = gPlusURL;
    }


}
