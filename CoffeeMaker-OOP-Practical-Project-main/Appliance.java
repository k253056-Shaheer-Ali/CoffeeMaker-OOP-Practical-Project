/*
 * Main (Parent) Appliance class.
 */
public class Appliance {

    //Private attributes
    private double inputPower;
    private double outputPower;
    private String parentCompany;
    
    public Appliance(double inputPower,double outputPower,String parentCompany){
        this.inputPower = inputPower;
        this.outputPower = outputPower;
        this.parentCompany = parentCompany;
    }

    public void setInputPower(double inputPower){
        this.inputPower = inputPower;
    }
    public void setOutputPower(double outputPower){
        this.outputPower = outputPower;
    }
    public void setParentCompany(String parentCompany){
        this.parentCompany = parentCompany;
    }

    public double getInputPower(){
        return inputPower;
    }
    public double getOutputPower(){
        return outputPower;
    }
    public String getParentCompany(){
        return parentCompany;
    }

    public void operate(double inputPower, double outputPower, String parentCompany) {
        setInputPower(inputPower);
        setOutputPower(outputPower);
        setParentCompany(parentCompany);
        System.out.println("Operating appliance with updated attributes:");
        System.out.println("Input Power: " + getInputPower());
        System.out.println("Output Power: " + getOutputPower());
        System.out.println("Parent Company: " + getParentCompany());
    }

}
