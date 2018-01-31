package gothos.DatabaseCore;

public class DatabaseParameter {

    private String type;

    private String value_string;
    private int value_int;
    private double value_double;

    public DatabaseParameter(String value){
        this.type = "string";
        this.value_string = value;
    }

    public DatabaseParameter(int value){
        this.type = "int";
        this.value_int = value;
    }

    public DatabaseParameter(double value){
        this.type = "double";
        this.value_double = value;
    }

    public String getType(){
        return this.type;
    }

    public String getStringValue(){
        return this.value_string;
    }

    public int getIntValue(){
        return this.value_int;
    }

    public double getDoubleValue(){
        return this.value_double;
    }
}
