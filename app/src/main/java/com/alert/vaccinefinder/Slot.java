package com.alert.vaccinefinder;

public class Slot {
 private String hospital_name,capacity,min_age,vaccine_name,fee_type,fee,from,to,pincode,block,district;

 Slot(String hospital_name,String capacity,String min_age,String vaccine_name,String fee_type,String fee,String from,String to,String pincode,String block,String district){
     this.hospital_name = hospital_name;
     this.capacity = capacity;
     this.min_age = min_age;
     this.vaccine_name = vaccine_name;
     this.fee_type = fee_type;
     this.fee = fee;
     this.from = from;
     this.to = to;
     this.pincode = pincode;
     this.block = block;
     this.district = district;
 }
 public String get_hospital_name(){return hospital_name;}
 public String get_capacity(){return capacity;}
 public String get_min_age(){return min_age;}
 public String get_Vaccine_name(){return vaccine_name;}
 public String get_fee_type(){return fee_type;}
 public String get_fee(){return fee;}
 public String get_from(){return from;}
 public String get_to(){return to;}
 public String get_pincode(){return pincode;}
 public String get_block(){return block;}
 public String get_district(){return district;}
}
