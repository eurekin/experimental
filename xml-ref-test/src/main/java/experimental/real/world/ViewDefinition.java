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
        // somehow the following doesn't happen automatically
        //baseObjectViewModel.attributeViewModel.set(baseObjectViewModel.attributeProperty.get());

//        baseObjectViewModel.attributeProperty.signalExternalUpdate();
//        AttributeClass oldval = baseObjectViewModel.attributeProperty.get();
//        baseObjectViewModel.attributeProperty.set(new AttributeClass());
//        baseObjectViewModel.attributeProperty.set(oldval);
        System.out.println("attributeClass.base: " + baseObjectViewModel.attributeViewModel.base());
        System.out.println("attributeClass.age: " + baseObjectViewModel.attributeViewModel.ageProperty.get());
        System.out.println("UPDATE: ");
        baseObjectViewModel.attributeViewModel.ageProperty.set(11);
        System.out.println("attributeClass.age: " + baseObjectViewModel.attributeViewModel.ageProperty.get());

        AttributeClassViewModel savedProperty = baseObjectViewModel.attributeViewModel;
        initDataModel();
        baseObjectViewModel.set(baseObject);
        System.out.println("ROOT REINITIALIZATION: ");
        System.out.println("attributeClass.age: " + savedProperty.ageProperty.get());
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
