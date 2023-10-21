package com.models;
public class ComboItem {
    private String label;
    private String value;
    
    public ComboItem(){
        
    }
    
   public void setCustomerLable(String val){
        this.label = val;
    }
    
   public void setValue(String val){
        this.value = val;
    }

    public ComboItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
