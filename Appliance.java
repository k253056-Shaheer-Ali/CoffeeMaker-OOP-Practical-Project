/*
 * Main (Parent) Appliance class.
 */
public class Appliance {

    //Private attributes
    private static  double inputPower;
    private static  double outputPower;
    private static String parentCompany;
    
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

    public static double getInputPower(){
        return inputPower;
    }
    public static double getOutputPower(){
        return outputPower;
    }
    public static String getParentCompany(){
        return parentCompany;
    }


    public static void main(String[] args) {
        Appliance a1 = new Appliance(440, 20, "Philips");
        
        double input;
        double output;
        String company;

        System.out.println(a1.getInputPower() +"\n"+ a1.getOutputPower() +"\n"+ a1.getParentCompany());

        a1.setInputPower(420);
        a1.setOutputPower(15);
        a1.setParentCompany("Brother");

        input = a1.getInputPower();
        output = a1.getOutputPower();
        company = a1.getParentCompany();

        System.out.println(input +"\n"+ output +"\n"+ company);

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
