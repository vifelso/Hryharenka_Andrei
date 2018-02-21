
public class Main {

    public static void main(String[] args) {
        BaseCalculator baseCalculator = new BaseCalculator("input_1.txt", "output_1.txt");
        baseCalculator.readFileByLine();

        SimpleCalculator simpleCalculator = new SimpleCalculator("input_2.txt", "output_2.txt");
        simpleCalculator.readFileByLine();
        try {
            baseCalculator.calculate();
            baseCalculator.writeResults();
            simpleCalculator.calculate();
            simpleCalculator.writeResults();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
