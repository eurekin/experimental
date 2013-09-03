package experimental.real.world;

/**
 * @author greg.matoga@gmail.com
 */
public class ViewDefinition {

    private BaseClass baseObject;
    private BaseClassViewModel baseObjectViewModel;


    public static void main(String ... args) {

        ViewDefinition viewDefinition = new ViewDefinition();
        viewDefinition.init();
        viewDefinition.test();
    }

    private void test() {

        AttributeClass attributeClass = baseObjectViewModel.attributeProperty.get();
        System.out.println("attributeClass: " + attributeClass);
        System.out.println("attributeClass.property: " + baseObjectViewModel.attributeProperty);
        System.out.println("attributeClass.property.val: " + baseObjectViewModel.attributeProperty.get());
        System.out.println("attributeClass.base: " + baseObjectViewModel.attributeViewModel.base);
        System.out.println("attributeClass.age: " + baseObjectViewModel.attributeViewModel.ageProperty.get());
    }


    public void init() {
        initDataModel();

        initViewModel();
    }

    private void initViewModel() {
        baseObjectViewModel = new BaseClassViewModelFactory().newValueModel(baseObject);
    }

    private void initDataModel() {
        baseObject = new BaseClass();
        AttributeClass attributeClass = new AttributeClass();
        attributeClass.age = 10;
        baseObject.attribute = attributeClass;
    }
}
