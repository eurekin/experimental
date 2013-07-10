package pl.eurekin.experimental;

public class PropertyAccessor<T, B> {
    private final TemplateGetter<T, B> templateGetter;
    private final TemplateSetter<T, B> templateSetter;

    public PropertyAccessor(TemplateGetter<T, B> templateGetter,
                            TemplateSetter<T, B> templateSetter) {
        this.templateGetter = templateGetter;
        this.templateSetter = templateSetter;
    }

    public T get(B base) {
        return templateGetter.get(base);
    }

    public void set(B base, T value) {
        templateSetter.set(base, value);
    }
}
